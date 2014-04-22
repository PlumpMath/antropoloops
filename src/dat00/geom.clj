(ns dat00.geom

  (:use quil.core
        [dat00.util :as ut]
        [quil.helpers.drawing :only [line-join-points]]
        [quil.helpers.seqs :only [range-incl steps]])
    (:import [geomerative RShape RG RPoint RFont]
             [processing.core PApplet]))

(declare grp juan font points-clj tucan)
(def my-rand (atom 0))
(def ops [+ -])
(defn oper [v1 v2]
  (condp = (rand-nth ops)
   + (+ v1 v2)
   - (- v2 v1)))

(defn setup[]
  (frame-rate 5)
  (println "setup completed!")
  (RG/init juan)
  (def tucan (RG/loadShape "data/peninsule.svg"))
  (def grp (RG/getText "hola" "data/FreeSans.ttf" 150 PApplet/CENTER))
  (RG/centerIn grp (.-g juan) 100)
  (RG/setPolygonizer RG/UNIFORMLENGTH)
  (reset! my-rand 440)


  )


(change-svg tucan (- 500 40) 0 500)
(def nervous 5)
(defn draw []
  (change-svg tucan (oper (rand nervous) @my-rand) 0 500)

  (background 150)
  (fill 250)
  (text-size 22)
  (text  (str (count (.children grp))  " G E O") 100 100)
  (stroke 0 100)
  (translate 100 100)
  (scale 0.5)
  (stroke-weight 2)

  (doseq [[[x1 y1] [x2 y2]] points-clj]
    (line x1  y1 x2  y2 ))

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
(oper 3 4)

(defn change-svg [svg input low-limit high-limit]
  (RG/setPolygonizerLength (map-range input low-limit high-limit 1 200 ))
  (def children (. svg children))
  (def points  (.getPoints (first children)))
  (def points-clj (partition 2 1
                             (reduce
                              (fn [c p]
                                (conj c [(.-x p)    (.-y p)]))
                              [] points)))
)

(defn mouse-moved []
  (reset! my-rand (mouse-x))

  )

(defsketch juan
  :setup setup
  :draw draw
  :size [500 500]
  :mouse-moved mouse-moved)
