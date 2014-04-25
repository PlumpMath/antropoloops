(ns dat00.util
  (:use [clojure.reflect] [clojure.java.io]
        [clojure.data.json :as json :only [read-str]])
  (:require [clojure.edn :as edn]))
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


(defn all-methods [x]
    (->> x reflect
           :members
           (filter :return-type)
           (map :name)
           sort
           (map #(str "." %) )
           distinct
           println))


(def ops [+ -])
(defn oper [v1 v2]
  (condp = (rand-nth ops)
    + (+ v1 v2)
    - (- v2 v1)))


(comment (def o (slurp "resources/loop-session/aloops.json"))
         (def aloops (edn/read-string o))
         )

(comment
  (def h (slurp "resources/loop-session/history.json"))
  (def history (edn/read-string h))
         )


(defn get-test-json [path]
    (edn/read-string (slurp path))


  )
