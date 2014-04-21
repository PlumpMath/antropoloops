(ns dat00.util
  (:use [clojure.java.io]
        [clojure.data.json :as json :only [read-str]]))
(defn remove-file [path] (try  (delete-file path)
                               (catch Exception e (str "caught exception: " (.getMessage e)))
                               ))
(defn write-io [path o] (spit path o))

(defn append-io [path o]
  (with-open [wrtr (writer path :append true)]
    (.write wrtr (json/write-str o)))
  )

(defn new-io-file [path ]
 (with-open [wrtr (writer path)]
  (.write wrtr ""))

  )




(defn substring? [sub st]
  (if (not= (.indexOf st sub) -1)
    true
    false))

(defn eval-java-method [v]
  "v is a keyword and o the java object"
   (list (symbol (str "." (name v)))))

(comment (eval-java-method :getClass ))
