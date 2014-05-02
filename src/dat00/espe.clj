(ns dat00.espe
  (:use
   dat00.protocols
   [dat00.oscloops :as osc-loops]
   [dat00.graphics :as g]
   [dat00.analysis :as analysis]
   [dat00.antropoloops :as antropoloops]
   quil.core
   [ dat00.bd :as bd]
   [clojure.data.json :as json :only [read-str]]
   [dat00.util :as ut]
   ))

(def drawing (atom false))

(defn get-colors [^toxi.color.ColorRange cr t-color num-colors variance]

  (.getColors cr t-color num-colors variance)
  )

(declare history-paths)

(defn setup  []

  (when @antropoloops/recording-history
    (ut/remove-file "tmp/history.json")
    (ut/remove-file "tmp/aloops.json")
    (ut/new-io-file "tmp/history.json"))

  (g/setup-graphics)
  (g/load-resources))


(defn lines-history []
  (doseq [track-path-raw history-paths ]
      (let [track-path (first  track-path-raw)
            color-list-path (last track-path-raw) ]

        (no-fill)
        (doseq [n (range  (count  track-path))]

          (let [track-line-path (nth track-path n)
                color-line-path (.get color-list-path n)
                origen (first track-line-path)
                ox (+ 5 (:coordX origen))
                oy (:coordY origen)
                fin (second track-line-path)
                fx (:coordX fin)
                fy (:coordY fin)
                a1x (if (not= ox fx) (+ ox (-  fx ox)) (- ox 20) )
                a1y (if (not= oy fy)  (- oy (abs  (-  fy oy)) 100) (- oy 20))
                a2x (if (not= ox fx) fx (+ fx 50))
                a2y (if (not= oy fy)  fy (- oy 50))
                ]
            (stroke (rgb color-line-path))
            (stroke-weight 1)

            (doseq [i (range 10)]

              (bezier (-  ox i) oy (- a1x i) (- a1y i)  a2x  a2y  fx fy))
            )
          ))
      ))



(def lugares-inside (atom {}))

(def lines  (atom []))
(def main-count (atom 0))


(defn print-interior [c int-lines each-modulo-width [-color lugar volume] contador-lines-int ]
  (let [starting (* c each-modulo-width)
        each-l-w (/ each-modulo-width (count int-lines))
        start-l (* contador-lines-int each-l-w)]
                                        ;    (println volume)
                                        ;(rgb (toxi.color.TColor/WHITE))
    (no-stroke)
;    (stroke (rgb -color)  (map-range volume 1 8 1 100))
    (fill (rgb -color)  (map-range volume 1 8 1 100))
;    (line  (+ starting start-l) (height) (+ starting start-l) (- (height) 40))
    (triangle
     (+ starting start-l)
     (- (height) 100)

     (+ starting start-l each-l-w)
     (- (height) 100)

     (:coordX lugar)
         (:coordY lugar)


     )
    (ellipse (:coordX lugar) (:coordY lugar)  5 5 )
    (rect (+ starting start-l) (- (height) 100)  each-l-w 100 )

    #_(doseq [x1    (range
                    (+ starting start-l)
                    (+ starting start-l each-l-w ))]
      (stroke-weight 2)
      (line  x1 (height) x1 (- (height) 40))

                                        ;                              (line  x1 40 (:coordX lugar) 180)
;      (line  x1 (- (height) 40)  (:coordX lugar) (:coordY lugar) )

      (no-fill)

      #_(bezier
         x1
         40

         (if (< x1 (:coordX lugar))
           (+ x1 100)
           (- x1 100)

           )
         (- (:coordY lugar) 180)


         (:coordX lugar)
         (:coordY lugar)


         (:coordX lugar)
         (:coordY lugar)
         )
;      (fill (rgb -color))
 ;     (ellipse (:coordX lugar) (:coordY lugar)  20 20 )

      )))


(defn print-module-time [int-lines c]

  (when-not (empty? int-lines)
    (let [each-modulo-width (/ (width) (count @lines))]
      (doall (map (partial print-interior c int-lines each-modulo-width) int-lines (range) )))))


(defn draw []
  (frame-rate 5)
  (g/draw-background)
  (g/draw-antropoloops-credits)
  (swap! main-count inc)
  (when (and (not-empty @antropoloops/antropo-loops) @drawing)
    (text "drawing!!" 10 175)



    (let [m (millis)
          active-loops (filter (fn [v]
                                 (let [{:keys [state]} (val v)]
                                   (= state 2 )))
                               @antropoloops/antropo-loops)
          a-l-c (count active-loops)
          int-lines (atom [])]
      (doseq [a-loop  active-loops]
        (let [active-loop (val a-loop)
              song (:song active-loop)
              lugar (:lugar active-loop)
              track (int (:track active-loop))
              posicion-x-disco (* 160 track)
              fecha (:fecha song)]


          (let [the-key (:lugar lugar)
                the-color (toxi.color.TColor/newRandom) ]
            (if-let [lugar-color  (find @lugares-inside the-key)]

              (swap! int-lines conj [(last lugar-color) lugar a-l-c (:volume active-loop)])
              (do
                (swap! lugares-inside assoc the-key the-color)
                (swap! int-lines conj [the-color lugar a-l-c (:volume active-loop)]))
              ))

          (g/draw-album active-loop posicion-x-disco fecha)
          (g/draw-line-country active-loop posicion-x-disco lugar)
          (g/draw-abanico-country active-loop lugar tempo m)
          )
        (swap! lines conj (shuffle @int-lines))
        )

      )

    )


  (doall
   (map print-module-time @lines (range)))

  (when-not (empty? @lines) (let [each-module (/ (width) (count @lines))
         ]
     (doseq [m (range (count @lines))]
       (stroke 260)
       (stroke-weight 1)
       (let [x-m (* m each-module)]
         (line x-m (height) x-m (- (height) 20)))
       )

     ))

  (g/draw-svg)

;(println (width))
  )





(defn key-press []
  (println (str "Key pressed: " (raw-key)))
  (condp = (raw-key)
    \1 (do
         (antropoloops/reset)
         )
    \2 (async-request-loops-info)
    \3 (do (println "draw running")
           (reset! drawing true))
    \4 (println "println loops indexed" antropoloops/antropo-loops-indexed)
    \5 (println "print misatropolops" @antropoloops/antropo-loops)
    \6 (when @antropoloops/recording-history
         (ut/write-io "tmp/aloops.json" @antropoloops/antropo-loops)
         (ut/write-io "tmp/history.json" @antropoloops/history))
    \7  (do
          (def sess-loop (ut/get-test-json "resources/loop-session/aloops.json"))

          (def f-sess-loop (first sess-loop))
          (doall (map
                  (fn [f-sess-loop] (antropoloops/load-clip (merge (key f-sess-loop) {:nombre (:nombre (val f-sess-loop))}) ))
                  sess-loop))

          (def sess-history (ut/get-test-json "resources/loop-session/history.json"))
          (def different-keys (reduce conj #{} (map keys sess-history)))

           (def history-paths (let [oc  (analysis/get-ordered-changes sess-history)]
                                (partition 2 1 (map #(analysis/get-changes-state-track oc % sess-loop) (analysis/keyword-tracks)))
              ))

           )
    \9          (doall (map
                  ((fn [arg-list] ) [f-sess-loop] (antropoloops/load-clip (merge (key f-sess-loop) {:nombre (:nombre (val f-sess-loop))}) ))
                  sess-loop))
    (println (str "no mapped key " (raw-key))))
  )


(defsketch juan
  :setup setup
  :draw draw
  :size [(screen-width) (screen-height)]
  :osc-event antropoloops/process-osc-event
  :key-typed key-press
  )

(osc-loops/init-oscP5-communication juan)

(comment
  )


(defn process-history-event [event]
  (let [res (first  (disj  (set (keys event)) :track :id :time :clip))]

    (condp = res
      :volume  (do ;(println "cHANGE VOLUME!!")
                 (antropoloops/change-volume event))
      :solo   (do ;(println "CHANGE SOLO!!")
                (antropoloops/change-solo event))
      :state (do (println "change STate!!" (:state event))
                 (antropoloops/change-volume (assoc  event :volume 1))
                 (antropoloops/change-loop-state {:clip-value (:clip event)
                                                  :track-value (:track event)
                                                  :state-value (:state event)}))

      (do #_(println "not mapped"))

      res)
    )
  )


(defn -main
  "The application's main function"
  [& args]
  (println args))


(defn print-history [data]
  (let  [facts data
                  init-time (:time (first sess-history))]
             (reduce (fn [c hist]
                       (let [i (:time hist)
                             wait-for (-  i c)
                             ]

                         (Thread/sleep 50)

                         (println  hist)
                         (process-history-event hist)
                         i
                         ) )
                     init-time
                     facts) ))


(defn print-all-history []
  (antropoloops/load-tempo 150)
  (reset! lines [])
  (reset! main-count 0)
  (repeatedly 1 #(let  [facts sess-history
                        init-time (:time (first sess-history))]
                   (reduce (fn [c hist]
                            (let [i (:time hist)
                                  wait-for (-  i c)
                                  ]
                                        ;(Thread/sleep wait-for)

                              (Thread/sleep 2)
                                        ;(println wait-for)
                              (println  hist)
                              (process-history-event hist)
                              i
                              ) )
                          init-time
                          facts) )))


(comment "testing antropoloops API!!!"
         (antropoloops/load-tempo 150)
         (antropoloops/change-loop-state {:clip-value 1, :track-value 1 :state-value 1})

         (antropoloops/change-volume {:track 1 :volume 3.8})
         (antropoloops/change-volume {:track 1 :volume 0.8})


         (antropoloops/reset)



         (comment

           (let [data (filter #(and (or (= (:state %) 1) (= (:state %) 2)) (= (:id %) "change-loop-state" )) sess-history)]
             (println "minutos: "(double (/ (/ (- (:time (last data)) (:time (first data))) 1000) 60) ))
             (map (fn [i] [ (:time i) (:clip i) (if (= (:state i) 2) :IN :OUT)] ) (filter #(= (:track %) 2 ) data ))
             ;(print-history data)

             )
           (let [ey (); res of last fn invocation
                 ]
             (map (fn [o] (filter #(= o (second %) ) ey)) (distinct (map second ey))))




           (let [oc  (analysis/get-ordered-changes sess-history)]
            (partition 2 1 (analysis/get-changes-state-track oc :1 sess-loop))
;             (analysis/get-changes-state-track oc :1 sess-loop)
             )
           (partition 2 1 (range 10))

           (def colors (take 10 (repeatedly (fn [] (toxi.color.TColor/newRandom)))))
           (let [color-range (get  (toxi.color.ColorRange/PRESETS) "FRESH")]
             (.sortByCriteria  (get-colors color-range (toxi.color.TColor/newRandom) 20 0.1 ) toxi.color.AccessCriteria/BRIGHTNESS false))


           (def history-paths (let [oc  (analysis/get-ordered-changes sess-history)
                                    f-res (map #(analysis/get-changes-state-track oc % sess-loop)  (analysis/keyword-tracks))
                                    f-cols (map
                                            #(let [color-range (get  (toxi.color.ColorRange/PRESETS) "FRESH")]
                                               (.sortByCriteria  (get-colors color-range (toxi.color.TColor/newRandom) (count %) 0.1 )
                                                                 toxi.color.AccessCriteria/BRIGHTNESS false))
                                                         f-res)
                                    ]
                                (map #(vector  (partition 2 1 %) %2) f-res f-cols)
                                )
             )
           )
         )
