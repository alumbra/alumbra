(ns alumbra.executor-test
  (:require [clojure.test :refer :all]
            [alumbra.test :as test]
            [alumbra.core :as alumbra]
            [claro.data :as data]
            [clojure.walk :as walk]))

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

(defn make-executor
  [& [overrides]]
  (let [base (alumbra/executor
               (-> {:schema test/schema
                    :query  {:person (map->Person {})
                             :me     (map->Me {})}}
                   (update :query merge (:query overrides))
                   (merge (dissoc overrides :query))))]
    (fn [& args]
      (let [result (apply base args)]
        (-> (select-keys result [:data :errors])
            (update :data walk/keywordize-keys)
            (assoc :success? true))))))

;; ## Tests

(deftest t-executor
  (let [run (make-executor)]
    (testing "single operation, no variables."
      (are [query-string expected-data]
           (test/has-data? (run query-string) expected-data)

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
             (run
               "query ($id: ID!) { person(id: $id) { name } }"
               {:variables {"id" id}})
             {:person {:name (str "Person #" id)}})
           1
           2
           3))
      (testing "multiple operations, no variables."
        (are [operation-name expected-data]
             (test/has-data?
               (run
                 "query Q1 { person(id: \"1\") { name } }
                  query Q2 { person(id: \"2\") { name } }"
                 {:operation-name operation-name})
               expected-data)
             "Q1" {:person {:name "Person #1"}}
             "Q2" {:person {:name "Person #2"}}))
      (testing "multiple operations, variables."
        (are [operation-name id expected-data]
             (test/has-data?
               (run
                 "query Q1($id: ID!) { person(id: $id) { age } }
                  query Q2($id: ID!) { person(id: $id) { name } }"
                 {:operation-name operation-name
                  :variables {"id" id}})
               expected-data)
             "Q1" 1 {:person {:age 18}}
             "Q1" 2 {:person {:age 27}}
             "Q2" 1 {:person {:name "Person #1"}}
             "Q2" 2 {:person {:name "Person #2"}}))))

(deftest t-executor-with-env
  (let [run (make-executor {:env {:session {:id 1}}})]
    (are [query-string context expected-data]
         (test/has-data? (run query-string {:context context}) expected-data)

         "{ me { name } }"
         {}
         {:me {:name "Person #1"}}

         "{ me { name } }"
         {:session {:id 2}}
         {:me {:name "Person #2"}})))
