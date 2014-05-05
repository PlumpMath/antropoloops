(ns dat00.data
  (:use
   quil.core
   dat00.bd
   dat00.mock))


(defn r [a b c d]
  (rect a b c d))

(defn try-o [o]
  (let [c (clojure.string/split o #"-")]
    { :track (read-string (first c)) :clip (read-string (second c))}))


(defn try-this []
  (let [total-l (- (width) 200)
        init 100]
    (doseq [y (range 1 9) ]
      (fill 255)
      (r init (* y 100)  total-l 100)


      )

    (doseq [l res]
      (let [[k m] l
            -keys (try-o k)
            track (inc  (:track -keys))
            start (:IN m)
            end (:OUT m)
            x1  (map-range start 0 duration init total-l)
            x2 (- (map-range end 0 duration init total-l) x1 -100)
            ]


        (fill (random 100 200))
        (r x1 (* track 100) x2 100 ))
      )
    )




  )
;(get readed (try-o (ffirst res)))

(try-o (ffirst res))







































(defn setup [])

(defn draw []
  (background 255)
  (try-this)
  (no-loop)
  )
(defn listen-to[])

(defsketch test
  :setup setup
  :draw draw
  :size [1000 (screen-height)]
;  :osc-event antropoloops/process-osc-event
  :key-typed listen-to
  )
