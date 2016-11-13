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
  data/Resolvable
  (resolve! [_ _]
    {:type :tiger
     :name name
     :numberOfStripes (* 7 (count name))
     :eats [(->Animal "Annie")
            (->Animal "Alex")
            (->Animal "Alan")
            (->Animal "Alice")]}))

(defrecord Antelope [name]
  data/Resolvable
  (resolve! [_ _]
    {:type :antelope
     :name name
     :numberOfHorns (mod (count name) 3)}))

(defrecord Animal [name]
  data/Resolvable
  (resolve! [_ _]
    (cond (.startsWith name "A") (->Antelope name)
          (.startsWith name "T") (->Tiger name)
          :else nil)))

(defrecord Animals [first]
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
  {:allAnimals (->Animals nil)
   :animal     (->Animal nil)})

;; ## Conditionals/Fragments
;;
;; I think this needs to be changed. Ideally, we would have a function that
;; returns the type of each value and then check in the projection whether
;; that means we should apply the projection.
;;
;; Might need to add a bit more type information to the canonical operation,
;; i.e. instead of a single type condition for fragments, supply all possible
;; types (implementations of unions/interfaces, for example).

(def conditional-fns
  {"Tiger"    #(projection/conditional (projection/extract :type) #{:tiger} %)
   "Antelope" #(projection/conditional (projection/extract :type) #{:antelope} %)})

;; ## Handler (w/ constant routing)

(def app
  (let [graphql  (alumbra/handler
                   {:schema (io/resource "Savannah.graphql")
                    :query  QueryRoot
                    :conditional-fns conditional-fns})
        graphiql (alumbra/graphiql-handler "/graphql")]
    (fn [{:keys [uri] :as request}]
      (case uri
        "/"        (graphiql request)
        "/graphql" (graphql request)
        {:status 204}))))

;; ## Server

(defonce server
  (http/start-server #'app {:port 8080}))
