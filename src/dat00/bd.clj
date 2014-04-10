(ns dat00.bd
  (:use
   [clojure.data.json :as json :only [read-str]]
   ))

(def loops (json/read-str (slurp "resources/1_BDatos/BDLoops.txt") :key-fn keyword))

(def lugares (json/read-str (slurp "resources/1_BDatos/BDLugares.txt") :key-fn keyword))
