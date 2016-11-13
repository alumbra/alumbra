(ns alumbra.core
  (:require [alumbra.ring
             [graphql :as graphql]
             [graphiql :as graphiql]]
            [alumbra
             [analyzer :as analyzer]
             [canonical :as canonicalizer]
             [claro :as claro]
             [parser :as parser]
             [validator :as validator]]))

(defn analyze
  "Analyze the given value, producing a result conforming to
   `:alumbra/analyzed-schema`."
  [schema]
  (analyzer/analyze-schema schema))

(defn validator
  "Generate a validator function using the given schema (will be analyzed if
   not already)."
  [schema]
  (-> schema
      (analyze)
      (validator/validator)))

(defn string-validator
  "Generate a validator function that takes GraphQL query document strings
   as input. The given schema will be analyzed if it isn't already.

   ```clojure
   (def validate
     (string-validator
       \"type Person { id: ID!, name: String!, friends: [Person!] }
        type QueryRoot { person(id: ID!): Person }
        schema { query: QueryRoot }\"))
   ```

   On successful validation, the validator returns `nil`."
  [schema]
  (comp (validator schema)
        parser/parse-document))

(defn handler
  "Generate a Ring handler for GraphQL execution based on the given GraphQL
   schema.

   See `alumbra.claro/make-executor` for available/required options."
  [{:keys [schema] :as opts}]
  (let [schema (analyze schema)
        opts   (assoc opts :schema schema)]
    (->> {:parser        #(parser/parse-document %)
          :validator     (validator/validator schema)
          :canonicalizer #(canonicalizer/canonicalize schema %1 %2 %3)
          :executor      (claro/make-executor opts)}
         (merge opts)
         (graphql/handler))))

(defn graphiql-handler
  "Generate a Ring handler exposing the interactive [GraphiQL][graphiql]
   environment. See `alumbra.ring.graphiql/handler` for available options.

   [graphiql]: https://github.com/graphql/graphiql"
  [graphql-path & [opts]]
  (graphiql/handler graphql-path opts))
