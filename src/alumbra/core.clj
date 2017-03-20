(ns alumbra.core
  (:require [alumbra.ring
             [graphql :as graphql]
             [graphiql :as graphiql]
             [pipeline :as pipeline]]
            [alumbra
             [analyzer :as analyzer]
             [claro :as claro]
             [parser :as parser]
             [validator :as validator]]))

;; ## Helper

(defn- analyze
  "Analyze the given value, producing a result conforming to
   `:alumbra/analyzed-schema`, including all introspection fields and
   base entities."
  [schema]
  {:post [(not (contains? % :alumbra/parser-errors))]}
  (analyzer/analyze-schema
    schema
    parser/parse-schema))

;; ## String Validator

(defn string-validator
  "Generate a validator function that takes GraphQL query document strings
   as input.

   ```clojure
   (def validate
     (string-validator
       \"type Person { id: ID!, name: String!, friends: [Person!] }
        type QueryRoot { person(id: ID!): Person }
        schema { query: QueryRoot }\"))
   ```

   On successful validation, the validator returns `nil`."
  [schema]
  (comp (validator/validator
          (analyze schema))
        parser/parse-document))

;; ## Ring

(defn handler
  "Generate a Ring handler for GraphQL execution based on the given GraphQL
   `:schema`.

   ```graphql
   type Person {
     id: ID!,
     name: String!
   }

   type QueryRoot {
     person(id: ID!): Person
     me: Person
   }

   schema {
     query: QueryRoot
   }
   ```

   The root types defined in the schema have to be given within `opts`:

   - `:query` (required)
   - `:mutation`
   - `:subscription`

   Each root type has to be a map associating field names with claro
   resolvables:

   ```clojure
   (def QueryRoot
     {:person (map->Person {})
      :me     (map->Me {})
      ...})
   ```

   A basic GraphQL handler thus consists of at least:

   ```clojure
   (alumbra.core/handler
     {:schema (io/resource \"Schema.gql\")
      :query  QueryRoot})
   ```

   Schemas can be given as strings, `File` or `URI` values. Multiple schemas can
   be supplied and will be merged in-order.

   A claro resolution environment can be supplied using `:env`. The environment
   can be extended using a request-specific `:context-fn` function, e.g.:

   ```clojure
   (defn context-fn
     [request]
     {:locale  (read-locale request)
      :db      (select-db-for request)
      :session (read-session request)})
   ```

   The claro engine to-be-used for resolution can be specified using `:engine`,
   allowing for custom engine middlewares to be attached.

   Custom directive handlers can be defined in `:directives`. They'll take a
   claro projection and the respective directive arguments, allowing a new
   projection to be generated. For example, the `@skip` handler is defined as:

   ```clojure
   {\"skip\" (fn [projection arguments]
               (when-not (:if arguments)
                 projection))}
   ```

   Custom scalar handlers can be defined in `:scalars`, as `:encode`/`:decode`
   pairs.

   ```clojure
   {\"NumericalID\" {:encode str, :decode #(Long. %)}}
   ```

   Note that, for both directives and scalars, the respective schema definitions
   have to exist."
  [{:keys [schema
           query mutation subscription
           engine env context-fn
           scalars directives]
    :as opts}]
  (let [schema (analyze schema)
        opts   (assoc opts :schema schema)]
    (->> {:parser-fn       #(parser/parse-document %)
          :validator-fn    (validator/validator schema)
          :canonicalize-fn (analyzer/canonicalizer schema)
          :executor-fn     (claro/executor opts)}
         (merge opts)
         (graphql/handler))))

(defn graphiql-handler
  "Generate a Ring handler exposing the interactive [GraphiQL][graphiql]
   environment, pointing at `graphql-path`. (Make sure to set the correct CORS
   options if you're using an absolute URL.)

   ```clojure
   (alumbra.core/graphiql-handler
     \"/people/v1/graphql\"
     {:title \"People API (GraphiQL)\"})
   ```

   By default, the static GraphiQL resources will be fetched from a CDN. You
   can set the respective `*-version` keys, or disable this completely using
   `:use-cdn?`.

   You can add custom tags to the `<head>` element using `:custom-head-tags`.

   [graphiql]: https://github.com/graphql/graphiql"
  [graphql-path
   & [{:keys [graphiql-version
              promise-version
              fetch-version
              react-version
              use-cdn?
              title
              custom-head-tags]
       :as opts}]]
  {:pre [(string? graphql-path)]}
  (graphiql/handler graphql-path opts))
