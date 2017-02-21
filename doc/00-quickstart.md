# Quickstart

We start with a GraphQL schema:

```clojure
(def schema
  "type Person { name: String!, friends: [Person!]! }
   type QueryRoot { me: Person! }
   schema { query: QueryRoot }")
```

We define one [claro][claro] resolvable per non-root type:

```clojure
(require '[claro.data :as data])

(defrecord Person [id]
  data/Resolvable
  (resolve! [_ _]
    {:name    (str "Person #" id)
     :friends (map ->Person  (range (inc id) (+ id 3)))}))
```

We declare a map for each root type:

```clojure
(def QueryRoot
  {:me (map->Person {:id 0})})
```

We create the GraphQL handler ...

```clojure
(require '[alumbra.core :as alumbra])

(def app
  (alumbra/handler
    {:schema schema
     :query  QueryRoot}))
```

... and pass it to an HTTP server of our choice:

```clojure
(defonce my-graphql-server
  (aleph.http/start-server #'app {:port 3000}))
```

And the GraphQL-fueled rocket takes off!

```shell
curl -XPOST "http://0:3000" -H'Content-Type: application/json' -d'{
  "query": "{ me { name, friends { name } } }"
}'
```

```json
{"data":{"me":{"name":"Person #0","friends":[{"name":"Person #1"},{"name":"Person #2"}]}}}
```
