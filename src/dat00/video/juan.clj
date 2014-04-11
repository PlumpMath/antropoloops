(ns dat00.video.juan
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
  :size [500 500])

(defn attach-movie-listener-to-fn [applet listener-fn movie-path]
  (gen-interface
   :name quil.MovieI
   :methods [
             [movieEvent
              [processing.video.Movie] Object]
             ])

  (def other (proxy [processing.core.PApplet quil.MovieI] []
               (dataPath [filename]
                 (println filename)
                 (.dataPath applet filename))
               (registerDispose [o]
                 (println "register dispose " o )
                 (.registerDispose applet o))
               (registerMethod [o #^Movie m]
                 (println "register method " o m)
                 (.registerMethod applet o m))
               (unregisterMethod [o #^Movie m]
                 (println "unregister method " o m)
                 (.unregisterMethod applet o m))
               (die [m]
                 (.die applet m))
               (movieEvent [#^Movie movie]
                 (listener-fn movie))
               (dispose []
                 (.dispose applet))
               ))
  (set! (.-g other) (.-g applet))

  (Movie. other movie-path)

  )

(def mov (attach-movie-listener-to-fn juan (fn [movie] (.read movie))  "data/transit.mov"))
  (.play mov)
