(defproject alumbra "0.1.0-SNAPSHOT"
  :description "GraphQL for Clojure!"
  :url "https://github.com/alumbra/alumbra"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :year 2016
            :key "mit"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14" :scope "provided"]
                 [alumbra/claro "0.1.0-SNAPSHOT"]
                 [alumbra/ring "0.1.0-SNAPSHOT"]
                 [alumbra/analyzer "0.1.3"]
                 [alumbra/validator "0.1.0-SNAPSHOT"]
                 [alumbra/parser "0.1.4"]]
  :profiles {:example
             {:dependencies [[aleph "0.4.2-alpha10"]]
              :source-paths ["example"]}}
  :aliases {"example-repl" ["with-profile" "+example" "repl"]}
  :pedantic? :abort)
