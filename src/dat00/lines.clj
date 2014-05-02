(ns dat00.lines
  (:use quil.core
        dat00.protocols
        [dat00.util :as ut]
        [quil.helpers.drawing :only [line-join-points]]
        [quil.helpers.seqs :only [range-incl steps]])

  (:import ;[geomerative RShape RG RPoint RFont]
           ;[processing.core PApplet ]
           [toxi.color TColor ColorList ColorRange NamedColor AccessCriteria]
           [toxi.color.theory ColorTheoryStrategy ColorTheoryRegistry]
           )

  )



(declare tucan)
(def random-color  (atom (TColor/newRandom)))

(def lines  (atom []))

(def origin-x (atom 0))
(def fin-x (atom 500))
(defn setup []
  (background (rgb @random-color))
  (frame-rate 5)
    (def tucan (load-shape "data/mapam.svg"))
  )


(defn draw []
  (background (rgb @random-color))

  (text-size 100)
  (swap! lines conj  (TColor/newRandom))
  (text  (str (count @lines )) 100 100)
  (no-fill)
  (doall
   (map
    (fn [a b]
      (stroke (rgb a))
      (beziber b 0 (+ 30 b) 100 250 400 0 500))
    @lines
    (range)

    ))
    (shape tucan 0 0 500 500)
  (fill 0)

  )
(comment
  (reset! lines []))


(comment
    (no-fill)
  (stroke (rgb (TColor/newRandom)))
  (let [v1-x (if (> (width) (+ 100 @origin-x))   (+ 100 @origin-x) (- @origin-x 50))]
   (bezier @origin-x 0 v1-x 100 250 400 @fin-x 500))
)


(defn mouse-moved []
                                        ;(background (rgb (TColor/newRandom)))
;  (println (mouse-x))
  (reset! origin-x (mouse-x))
  (reset! fin-x (mouse-y))
  )

(defn key-typed []
  (reset! random-color (TColor/newRandom))
  (background (rgb @random-color))  )


(defsketch juan
  :setup setup
  :draw draw
  :size [500 500]
  :mouse-moved mouse-moved
  :key-typed key-typed
  )
