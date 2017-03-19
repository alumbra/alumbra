(ns alumbra.test
  (:require [clj-http.client :as http]
            [aleph.http :as aleph]
            [cheshire.core :as json]))

;; ## Server Fixture

(def ^:dynamic *server-port* nil)

(defn with-server*
  [handler f]
  (with-open [server (aleph/start-server handler {:port 0})]
    (binding [*server-port* (aleph.netty/port server)]
      (f))))

(defmacro with-server
  [handler & body]
  `(with-server* ~handler (fn [] ~@body)))

(defn server-fixture
  "Start up the server and bind the port it's listening on to `*server-port*`."
  [handler]
  (fn [f]
    (with-server* handler f)))

;; ## Query Function

(defn ring-request
  [url query-string variables operation-name overrides]
  {:method  :post
   :url     url
   :headers (merge
              (:headers overrides)
              {"Content-Type" "application/json;charset=UTF-8"})
   :body    (json/generate-string
              (merge
                {:query query-string}
                (some->> variables (hash-map :variables))
                (some->> operation-name (hash-map :operationName))))
   :throw-exceptions? false})

(defn- parse-body
  [{:keys [body]}]
  (if body
    (-> (if (string? body)
          body
          (slurp body))
        (json/parse-string keyword))))

(defn query
  [query-string & [variables operation-name request-overrides]]
  (let [url      (str "http://0:" *server-port* "/graphql")
        request  (ring-request
                   url
                   query-string
                   variables
                   operation-name
                   request-overrides)
        response (http/request request)
        status   (:status response)]
    (merge
      {:success? (= status 200)
       :status   status}
      (parse-body response))))

(defn has-data?
  [{:keys [success? data]} expected-data]
  (when success?
    (= data expected-data)))

(defn has-error?
  [{:keys [success? errors]} error-type expected-error]
  (when-not success?
    (some
      (fn [{:keys [message context]}]
        (and (re-matches expected-error message)
             (= (:type context) error-type)))
      errors)))

(defn has-status?
  [{:keys [status]} expected-status]
  (= status expected-status))

;; ## Test Schema

(def schema
  "type Person { id: ID!, name: String!, age: Int! }
   type QueryRoot { me: Person, person(id: ID!): Person }
   schema { query: QueryRoot }")
