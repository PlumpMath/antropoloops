(ns dat00.juan
  (:import (processing.video Movie))
  (:use quil.core
        [quil.helpers.drawing :only [line-join-points]]
        [quil.helpers.seqs :only [range-incl steps]]))
(declare mov)

(defn setup[]
  (println "setup completed!")
)

(defn draw []
  (image mov 0 0 ))

(defsketch juan
    :setup setup
    :draw draw
    :size [500 500]
    :movie-event (fn [movie] (println "reading!!")  (.read movie)))


(def mov (Movie. juan "data/transit.mov"))
(.play mov)
