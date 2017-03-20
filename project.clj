(defproject alumbra "0.1.0-SNAPSHOT"
  :description "GraphQL for Clojure!"
  :url "https://github.com/alumbra/alumbra"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"
            :year 2016
            :key "mit"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14" :scope "provided"]
                 [alumbra/claro "0.1.4"]
                 [alumbra/ring "0.1.0"]
                 [alumbra/analyzer "0.1.9"]
                 [alumbra/validator "0.1.1"]
                 [alumbra/parser "0.1.6"]]
  :profiles {:dev
             {:dependencies [[aleph "0.4.3"]
                             [riddley "0.1.14"]
                             [clj-http "3.4.1"]]}
             :codox
             {:plugins [[lein-codox "0.10.3"]]
              :dependencies [[codox-theme-rdash "0.1.1"]]
              :codox {:project {:name "alumbra"}
                      :metadata {:doc/format :markdown}
                      :themes [:rdash]
                      :source-uri "https://github.com/alumbra/alumbra/blob/v{version}/{filepath}#L{line}"
                      :namespaces [alumbra.core]}}}
  :aliases {"codox" ["with-profile" "+codox" "codox"]}
  :pedantic? :abort)
