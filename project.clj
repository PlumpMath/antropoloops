(defproject dat00 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [quil/quil "2.0.1-juan"]
                 [org.clojure/data.json "0.2.4"]
;                 [quil-video/macosx64 "1"]
 ;                [quil-video/jna "1"]
 ;                [quil-video/video "1"]
 ;                [quil-video/gstreamer-java "1"]
[clj-time "0.7.0"]

                ; [com.datomic/datomic-free "0.9.4556"]
                 ]
  :resource-paths ["lib/gstreamer-java.jar" "lib/video.jar" "lib/jna.jar" "lib/macosx64" "lib/oscP5.jar"]

  )
