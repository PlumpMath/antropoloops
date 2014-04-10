(defproject dat00 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [quil/quil "2.0.1-SNAPSHOT"]
                 [org.clojure/data.json "0.2.4"]

                ; [com.datomic/datomic-free "0.9.4556"]
                 ]
  :resource-paths ["lib/gstreamer-java.jar" "lib/video.jar" "lib/jna.jar" "lib/macosx64"]
  )
