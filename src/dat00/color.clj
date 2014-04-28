(ns dat00.color
  (:use quil.core
        dat00.protocols
;        [iroh.core :as iroh]
        [dat00.util :as ut]
        [quil.helpers.drawing :only [line-join-points]]
        [quil.helpers.seqs :only [range-incl steps]])


  (:import [geomerative RShape RG RPoint RFont]
           [processing.core PApplet ]
           [toxi.color TColor ColorList ColorRange NamedColor AccessCriteria]
           [toxi.color.theory ColorTheoryStrategy ColorTheoryRegistry]
           )
   )

(defn get-colors [^ColorRange cr t-color num-colors variance]

  (.getColors cr t-color num-colors variance)
  )


(count (take 10 (repeatedly (fn [] (toxi.color.TColor/newRandom)))))

(def strategies (ColorTheoryRegistry/getRegisteredStrategies))
(map #(.getName %) strategies)


(do
  (def random-color  (TColor/newRandom))

  (def cl (ColorList/createUsingStrategy (nth  strategies 4) random-color))

  (def cl2 (. (.getColors (doto (ColorRange. cl) (.addBrightnessRange 0 1) ) nil 10 0.05) (sortByDistance false)))
  (def range-color-list  (map #(.sortByCriteria  (get-colors (val %) random-color 20 0.1 ) AccessCriteria/BRIGHTNESS false) (ColorRange/PRESETS))))


(keys (ColorRange/PRESETS))





;(get-count cl2)
((defn rect-coords-grid [the-widht  c]
   (let [n-items (get-count c)
         item-w (int (/ the-widht n-items))]
     (map #(vector % (+ % item-w)) (range 0 the-widht item-w))
     ))
 500 cl)



(defn setup[]

  )

(map (fn [cl h]
       [cl h]) range-color-list (range 250 (+ 250 (* 50 (count range-color-list))) 20 ))

(defn draw-grid [y1 y2 total-width c]
  (dorun (map (fn [[x1 x2] t-col]
                (fill (rgb t-col))
                (rect x1 y1 x2 y2 ))  (rect-coords-grid total-width c) c)))


(defn fill-background []
  (background (rgb random-color))
  (draw-grid 0 100 (width) cl)

  (draw-grid 200 50 (width) cl2)

  (dorun (map (fn [clist h]
          (draw-grid h 20 (width) clist)) range-color-list (range 250 (+ 250 (* 20 (count range-color-list))) 20 )))





  )

(defn draw []
  (fill-background)
  )

(defn mouse-moved []

  )

(defsketch juan
  :setup setup
  :draw draw
  :size [500 500]
  :mouse-moved mouse-moved)
