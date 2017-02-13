(ns user
  (:require [alumbra.core :as alumbra]
            [claro.data :as data]
            [claro.projection :as projection]
            [aleph.http :as http]
            [manifold.deferred :as d]
            [clojure.java.io :as io]))

;; ## Resolvables

(declare ->Animal)

(defrecord Tiger [name]
  data/PureResolvable
  data/Resolvable
  (resolve! [_ _]
    {:type :tiger
     :name name
     :number-of-stripes (* 7 (count name))
     :eats [(->Animal "Annie")
            (->Animal "Alex")
            (->Animal "Alan")
            (->Animal "Alice")]}))

(defrecord Antelope [name]
  data/PureResolvable
  data/Resolvable
  (resolve! [_ _]
    {:type :antelope
     :name name
     :number-of-horns (mod (count name) 3)}))

(defrecord Animal [name]
  data/PureResolvable
  data/Resolvable
  (resolve! [_ _]
    (cond (.startsWith name "A") (->Antelope name)
          (.startsWith name "T") (->Tiger name)
          :else nil)))

(defrecord Animals [first]
  data/PureResolvable
  data/Resolvable
  (resolve! [_ _]
    (->> ["Alex"
          "Toby"
          "Alan"
          "Alice"
          "Annie"
          "Tom"]
         (map ->Animal)
         (take (or first 5)))))

;; ## Query Root

(def QueryRoot
  {:all-animals (->Animals nil)
   :animal      (->Animal nil)})

;; ## Handler (w/ constant routing)

(def app
  (let [graphql  (alumbra/handler
                   {:schema (io/resource "Savannah.graphql")
                    :query  QueryRoot})
        graphiql (alumbra/graphiql-handler "/graphql")]
    (fn [{:keys [uri] :as request}]
      (case uri
        "/"        (graphiql request)
        "/graphql" (graphql request)
        {:status 404
         :headers {"cache-control" "public, max-age=600"}}))))

;; ## Server

(defonce server
  (http/start-server #'app {:port 8080}))
