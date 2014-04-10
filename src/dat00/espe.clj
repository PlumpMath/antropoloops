 (ns dat00.espe
  (:use
   [dat00.oscloops :as osc-loops]
   quil.core
   [clojure.data.json :as json :only [read-str]]
   ))

(declare reset  mundi font antropo-loops-indexed   tempo current-index )
(def antropo-loops (atom {}))

(def drawing (atom false))

(defn setup  []
  (def mundi (load-image "resources/1_BDatos/mapa_1280x800.png"))
  (color-mode :hsb 360 100 100)
  (def font (load-font "resources/1_BDatos/ArialMT-20.vlw"))

  ;(println "remapea")


  )

(defn abanica [x y d h s b]
  (doseq [i (range 20)]
    (stroke h s b)
    (stroke-weight 1)
(do
                 (line 0 0 0 (- 0 (/ d 4)))
                 (no-stroke)
                 (fill h s b 45)
                 (arc 0 0 (/ d 2) (/ d 2) (- (radians (* i 24)) HALF-PI) (- (radians 360) HALF-PI))
                 (fill h s b 2)
                 (arc 0 0 (* d 2) (* d 2) (- (radians (* i 24)) HALF-PI) (- (radians 360) HALF-PI))
                 )
    #_(cond
     (<= d 60) :a
     (and (< d 60) (<= d 90)) :b
     (and  (> d 40)) :c
     )))

(defn draw []
  (background (unhex "2b2b2b"))
  (image mundi 0 0 )
  (fill 50)
  (no-stroke)
  (rect 0 0 (width) 160)
  (fill 255)
  (text "antropoloops MAP 1.0" 10 775)
  (fill 120)
  (text "by MI-MI NA" 10 790)

  (when (and (not-empty @antropo-loops) @drawing)
    (text "drawing!!" 10 175)
    (let [m (millis)
          active-loops (filter (fn [v]
                                 (let [{:keys [state]} (val v)]
                                   (= state 2 )))
                               @antropo-loops)]
      (doseq [a-loop  active-loops]
        (let [active-loop (val a-loop)
              song (:song active-loop)
              lugar (:lugar active-loop)
              posicion-x-disco (* 160 (int (:track active-loop)))]
          (image (:image active-loop) posicion-x-disco 0 160 160)
          (fill (:color-h active-loop) (:color-s active-loop) (:color-b active-loop) 45 )
          ;; red
          (stroke (:color-h active-loop) (:color-s active-loop) (:color-b active-loop) (* 100 (:volume active-loop)) )
          (stroke-weight 2)
          (line posicion-x-disco 189 (:coordX lugar) (:coordY lugar))
          (push-matrix)
          (translate (:coordX lugar) (:coordY lugar))
          (rotate (radians (/ m (/ 60 (* tempo (int (:loopend active-loop)))))))
          (abanica (:coordX lugar) (:coordY lugar) (* 100 (:volume active-loop)) (:color-h active-loop) (:color-s active-loop) (:color-b active-loop))
          (pop-matrix)
          )))))

(defn change-loop-end [loopend-value]
  (let [the-index (nth antropo-loops-indexed (inc @current-index))]
    (swap!  antropo-loops assoc-in [the-index :loopend] loopend-value)
    (swap! current-index inc)))

(defn process-osc-event [message]

  (let [osc-event (osc-loops/event-to-keyword message)
;        model (osc-loops/model-related osc-event)
        update-fn #(let [track-value (osc-loops/map-direct-get message [ [:track-value 0 :intValue]])
;                                              (.intValue (.get message 0))
                         coincidences (filter
                                       (fn [v]
                                         (let [{:keys [track clip]} (key v)]
                                           (= track-value track ))) @antropo-loops)]
                     (doseq [c coincidences]
                       (swap!  antropo-loops assoc-in [(key c) %] %2)))]

    (condp = osc-event
      :clip
      (let [antro-loop (read-clip-info message)]
        (swap! antropo-loops assoc (select-keys antro-loop [:track :clip] ) antro-loop ))
;     :loopend (apply change-loop-end model)

      :loopend (let [the-index (nth antropo-loops-indexed (inc @current-index))
                     loopend-value (osc-loops/map-direct-get message [ [:loopend-value 0 :floatValue]])]
                 (swap!  antropo-loops assoc-in [the-index :loopend] loopend-value)
                 (swap! current-index inc))


      :info ((fn [{:keys [track-value clip-value state-value]} ]
               (swap!  antropo-loops assoc-in [{:clip clip-value :track track-value} :state] state-value)
               ) (osc-loops/map-direct-get message [ [:track-value 0 :intValue] [:clip-value 1 :intValue] [:state-value 2 :intValue]]))

     :volume (update-fn :volume  (.floatValue (.get message 1)))
     :solo (update-fn :solo  (.intValue (.get message 1)))
     :tempo (def tempo (.floatValue (.get message 0)))
     (do #_(println "not mapped"))
     )
    )
  )





(defn async-request-related-loops-info-from-ableton []
  (println "async-request-related-clips-info-from-ableton")

  (def current-index (atom -1))
  (def antropo-loops-indexed (map key @antropo-loops))

  (doseq [loop antropo-loops-indexed]
    (osc-loops/make-async-call-for-loop loop))


  )

(defn key-press []
  (println (str "Key pressed: " (raw-key)))
  (condp = (raw-key)
    \1 (reset)
    \2 (async-request-related-loops-info-from-ableton)
    \3 (do (println "draw running") (reset! drawing true))
    \4 (println "println loops indexed" antropo-loops-indexed)
    \5 (println "print misatropolops" @antropo-loops)
    (println (str "no mapped key " (raw-key))))
  )
(defsketch juan
  :setup setup
  :draw draw
  :size [(screen-width) (screen-height)]
  :osc-event process-osc-event
  :key-typed key-press
  )
(osc-loops/init-oscP5-communication juan)
(defn reset[]
  (reset! antropo-loops {})
  (osc-loops/make-async-call-for-all-clips)
)
