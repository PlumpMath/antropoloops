(ns dat00.data
  (:use
   dat00.protocols
   quil.core
   dat00.bd
   dat00.mock)
  (:import
           [toxi.color TColor ColorList ColorRange NamedColor AccessCriteria]
           [toxi.color.theory ColorTheoryStrategy ColorTheoryRegistry]
           ))


(comment "to work with volume"

         (def volumes-changes (filter #(contains? % :volume) sess-history))
         (def volume-changes-processed (map (fn [i ] [ (:track i) (int (/ (- (:time i) first-change-time) 1000)) (:volume i)])  volumes-changes))
         (def grouped (group-by (fn [[track time volume]] [track time]) volume-changes-processed))

         (def ordered-volumes (sort (fn [[track1 time1 _] [track2 time2 _]] (let [res-time (compare time1 time2)] (if (= 0 res-time) (compare track1 track2) res-time))) (map #(last (val %))  grouped)))

)


(def places-color (reduce (fn [c i] (assoc c  i (toxi.color.TColor/newRandom))) {} (distinct (map #(:lugar (:lugar (get readed (try-o (first %))))) res))))

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
            place (:lugar (:lugar (get readed -keys )))
            color (get places-color  place)
            track (inc  (:track -keys))

            start (:IN m)
            end (:OUT m)
            x1  (map-range start 0 duration init total-l)
            x2-raw (map-range end 0 duration init total-l)
            x2 (- x2-raw x1 -100)]
        (no-fill)
        (stroke  (rgb color))
       (r x1 (* track 100) x2 100 )
        (let [volumes (filter (fn [[ t time _]] (and (<= time end) (>= time start) (= t (dec track))) ) sorted-volumes)]
                  (fill (rgb color))
          (begin-shape)
          (vertex x1 (+ 100  (* track 100)))
          (doseq [[_ time volume] volumes]
            (vertex (map-range time 0 duration  init total-l) (+ (* track 100) (map-range volume 0 1 0 100)))
            )

          (vertex x2-raw (+ 100 (* track 100)))
          (end-shape :close))

        (fill 0)
        (text place (+ 5 x1) (+ 20 (* track 100)) )
        )
      )
    ))


















































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
  :size [(screen-width) (screen-height)]
;  :osc-event antropoloops/process-osc-event
  :key-typed listen-to
  )
