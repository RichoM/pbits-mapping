(defproject pbits-mapping "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [metasoarous/oz "2.0.0-alpha5" #_"1.6.0-alpha36"]
                 [markdown-to-hiccup "0.6.2"]]
  :main ^:skip-aot pbits-mapping.core
  :target-path "target/%s"
  :repl-options {:init-ns pbits-mapping.core
                 :init (-main)}
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
