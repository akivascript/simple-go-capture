(defproject simple-go-capture "0.1.0-SNAPSHOT"
  :description "In the game of Go, determines if a group is captured after a move."
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :main ^:skip-aot simple-go-capture.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
