(defproject query-processor "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/data.csv "1.0.0"]
                 [cheshire "5.9.0"]
                 [clj-http "3.12.3"]
                 [proto-repl "0.3.1"]]
  :repl-options {:init-ns query-processor.core})
