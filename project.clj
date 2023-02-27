(defproject pbits-sreview "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [metasoarous/oz "2.0.0-alpha5" #_"1.6.0-alpha36"]]
  :main ^:skip-aot pbits-sreview.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
