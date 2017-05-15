(ns alumbra.query-test
  (:require [clojure.test :refer :all]
            [alumbra.test :as test]
            [alumbra.core :as alumbra]
            [claro.data :as data]))

;; ## Fixtures

(defrecord Person [id]
  data/PureResolvable
  data/Resolvable
  (resolve! [_ _]
   {:id id
    :name (str "Person #" id)
    :age  (* (inc (Long. id)) 9)}))

(defrecord Me []
  data/PureResolvable
  data/Resolvable
  (resolve! [_ {:keys [session]}]
    (some-> session :id ->Person)))

(defn make-handler
  [& [overrides]]
  (let [base (alumbra/handler
               (-> {:schema test/schema
                    :query  {:person (map->Person {})
                             :me     (map->Me {})}}
                   (update :query merge (:query overrides))
                   (merge (dissoc overrides :query))))]
    (fn [request]
      (let [response (base request)]
        (when-not (:silent? overrides)
          (when-let [ex (:alumbra/exception response)]
            (.printStackTrace ^Throwable ex)))
        response))))

;; ## Tests

(deftest t-query
  (let [handler (make-handler)]
    (test/with-server handler
      (testing "single operation, no variables."
        (are [query-string expected-data]
             (test/has-data? (test/query query-string) expected-data)

             "{ person(id: \"1\") { id, name, age } }"
             {:person {:id "1", :name "Person #1", :age 18}}

             "{ person(id: \"1\") { __typename } }"
             {:person {:__typename "Person"}}

             "{
              firstPerson: person(id: 1) { name }
              secondPerson: person(id: 2) { name }
              }"
             {:firstPerson {:name "Person #1"}
              :secondPerson {:name "Person #2"}}

             "{ me { name } }"
             {:me nil}))
      (testing "single operation, variables."
        (are [id]
             (test/has-data?
               (test/query
                 "query ($id: ID!) { person(id: $id) { name } }"
                 {:id id})
               {:person {:name (str "Person #" id)}})
             1
             2
             3))
      (testing "multiple operations, no variables."
        (are [operation-name expected-data]
             (test/has-data?
               (test/query
                 "query Q1 { person(id: \"1\") { name } }
                  query Q2 { person(id: \"2\") { name } }"
                 nil
                 operation-name)
               expected-data)
             "Q1" {:person {:name "Person #1"}}
             "Q2" {:person {:name "Person #2"}}))
      (testing "multiple operations, variables."
        (are [operation-name id expected-data]
             (test/has-data?
               (test/query
                 "query Q1($id: ID!) { person(id: $id) { age } }
                  query Q2($id: ID!) { person(id: $id) { name } }"
                 {:id id}
                 operation-name)
               expected-data)
             "Q1" 1 {:person {:age 18}}
             "Q1" 2 {:person {:age 27}}
             "Q2" 1 {:person {:name "Person #1"}}
             "Q2" 2 {:person {:name "Person #2"}})))))

(deftest t-query-with-env
  (let [handler (make-handler
                  {:env {:session {:id 1}}})]
    (test/with-server handler
      (are [query-string expected-data]
           (test/has-data? (test/query query-string) expected-data)

           "{ me { name } }"
           {:me {:name "Person #1"}}))))

(deftest t-query-with-context
  (let [handler (make-handler
                  {:env {:session {:id 1}}
                   :context-fn (fn [{:keys [headers]}]
                                 (some->> (get headers "X-Person")
                                          (hash-map :id)
                                          (hash-map :session)))})]
    (test/with-server handler
      (is (test/has-data?
            (->> {:headers {"X-Person" "2"}}
                 (test/query "{ me { name } }" nil nil))
            {:me {:name "Person #2"}})))))

(deftest t-query-parser-error
  (let [handler (make-handler)]
    (test/with-server handler
      (let [result (test/query "{ me { name }")]
        (is (test/has-status? result 400))
        (is (test/has-error?
              result
              "parser-error"
              #"Syntax Error \(1:14\).*"))))))

(deftest t-query-validation-error
  (let [handler (make-handler)]
    (test/with-server handler
      (let [result (test/query "{ me { unknownField } }")]
        (is (test/has-status? result 400))
        (is (test/has-error?
              result
              "validation-error"
              #"Cannot query field \"unknownField\" on type \"Person\"."))))))

(deftest t-input-coercion-error
  (let [handler (make-handler
                  {:scalars
                   {"ID" {:decode #(throw (ex-info "oops." {:value %}))}}
                   :silent? true})]
    (test/with-server handler
      (let [result (test/query "{ person(id: 10) { name } }")]
        (is (test/has-status? result 500))
        (is (test/has-error?
              result
              "uncaught-exception"
              #".*Could not coerce value to 'ID': 10"))))))

(deftest t-output-coercion-error
  (let [handler (make-handler
                  {:scalars
                   {"ID" {:encode #(throw (ex-info "oops." {:value %}))}}})]
    (test/with-server handler
      (let [result (test/query "{ person(id: 10) { id } }")]
        (is (test/has-status? result 500))
        (is (test/has-error?
              result
              nil
              #"Could not coerce value to 'ID': \"10\""))))))
