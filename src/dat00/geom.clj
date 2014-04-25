(ns dat00.geom

  (:use quil.core

        [dat00.util :as ut]

        [quil.helpers.drawing :only [line-join-points]]
        [quil.helpers.seqs :only [range-incl steps]])
  (:require [clojure.zip :as zip])
  (:import [geomerative RShape RG RPoint RFont]
           [processing.core PApplet ]))


(declare grp juan font points-clj tucan )

(defn change-svg [svg input low-limit high-limit]
  (RG/setPolygonizerLength (map-range input low-limit high-limit 1 200 ))
  (def children (. svg children))
  (def points  (.getPoints (first children)))
  (def points-clj (partition 2 1
                             (reduce
                              (fn [c p]
                                (conj c [(.-x p)    (.-y p)]))
                              [] points))))

(def my-rand (atom 0))

(defn get-points-seq [p]
  (reduce (fn [c [x y]] (conj c x y))  []      (map (fn [[[x1 y1] xy2]]  [x1 y1] ) p )))
(defn title []
    (fill 250)
  (text-size 22)
  (text  (str " G E O M E R A T I V E") 10 50)
)

(defn setup[]
  (frame-rate 5)
  (println "setup completed!")
  (RG/init juan)
  (def tucan (RG/loadShape "data/peninsule.svg"))
  (def grp (RG/getText "hola" "data/FreeSans.ttf" 150 PApplet/CENTER))
  (RG/centerIn grp (.-g juan) 100)
  (RG/setPolygonizer RG/UNIFORMLENGTH)
  (reset! my-rand 440)

  (title)
  )



;(count (get-points-seq points-clj))
;(change-svg tucan (- 500 40) 0 500)
(def nervous 5)
(defn draw []
  (change-svg tucan (ut/oper (rand nervous) @my-rand) 0 500)

  (push-matrix)
  (stroke 0 50)
  (translate 100 100)
  (scale 0.5)
  (stroke-weight 2)

  (doseq [[[x1 y1] [x2 y2]] points-clj]
    (line x1  y1 x2  y2 ))


  (fill 255 10)
  (no-stroke)

    (begin-shape)
    (fill 255 10)
    (dorun (map (fn [[x y]] (curve-vertex x y)) (map first points-clj)))
    (end-shape)


  (pop-matrix)
  (comment (push-matrix)
           (push-style)
           (scale 0.5)
           (RG/ignoreStyles)
           (no-fill)
           (stroke 0 50)
           (.draw tucan)
           (pop-style)
           (pop-matrix))

  )

(def h [1 2 3 4 5])
(map-range 50 1 50 0 500)
(ut/oper 3 4)



(defn mouse-moved []
  (push-style )
  (push-matrix)
  (scale 1)
  (fill 150 10)
  (no-stroke)
  (rect 0 0 (width) (height))
  (title)
  (pop-matrix)
  (pop-style)
  (reset! my-rand (mouse-x))

  )

(defsketch juan
  :setup setup
  :draw draw
  :size [500 500]
  :mouse-moved mouse-moved)













(comment "parsing tree svg data with clojure zipper"
         (defn is-parent? [c] (not (zero? (count (.getChildren c)))))
         (def zp (zip/zipper is-parent? (fn [c] (.getChildren c)) (fn [n c] c) tucan))

         (defn zip-reduce
           "Reduce called on a zipper."
           [f acc z]
           (if (zip/end? z)
             acc
             (if (and (not= (type []) (type z)) (= 2 (count  (clojure.string/trim (.-name z)))))
               (recur f (f acc z) (zip/next z))
               (recur f acc (zip/next z))
               )))

         (def p (zip-reduce conj [] zp))

)
