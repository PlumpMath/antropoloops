 (ns dat00.espe
  (:use
   [dat00.oscloops :as osc-loops]
   [dat00.graphics :as g]
   quil.core
   [clojure.data.json :as json :only [read-str]]
   ))

(declare reset    antropo-loops-indexed   tempo current-index )
(def antropo-loops (atom {}))
(def drawing (atom false))

(defn setup  []
  (g/setup-graphics)
  (g/load-resources))


(defn draw []
  (g/draw-background)
  (g/draw-antropoloops-credits)
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
              track (int (:track active-loop))
              posicion-x-disco (* 160 track)
              fecha (:fecha song)]
          (image (:image active-loop) posicion-x-disco 0 160 160)
          (fill (:color-h active-loop) (:color-s active-loop) (:color-b active-loop) 45 )
          (text fecha (+ 5 posicion-x-disco) 190)
          ;; red
          (stroke (:color-h active-loop) (:color-s active-loop) (:color-b active-loop) (* 100 (:volume active-loop)) )
          (stroke-weight 2)
          (line posicion-x-disco 189 (:coordX lugar) (:coordY lugar))
          (push-matrix)
          (translate (:coordX lugar) (:coordY lugar))
          (rotate (radians (/ m (/ 60 (* tempo (int (:loopend active-loop)))))))
          (g/abanica (:coordX lugar) (:coordY lugar) (* 100 (:volume active-loop)) (:color-h active-loop) (:color-s active-loop) (:color-b active-loop))
          (pop-matrix)
          )))))

(defn change-loop-end [loopend-value]
  (let [the-index (nth antropo-loops-indexed (inc @current-index))]
    (swap!  antropo-loops assoc-in [the-index :loopend] loopend-value)
    (swap! current-index inc)))

(defn change-loop-state [{:keys [track-value clip-value state-value]} ]

  (swap!  antropo-loops assoc-in [{:clip clip-value :track track-value} :state] state-value)
  )

(defn update-track-prop-value [track-value the-keyword the-value]
  (let [
;                                              (.intValue (.get message 0))
                         coincidences (filter
                                       (fn [v]
                                         (let [{:keys [track clip]} (key v)]
                                           (= track-value track ))) @antropo-loops)]
                     (doseq [c coincidences]
                       (swap!  antropo-loops assoc-in [(key c) the-keyword] the-value))))

(defn change-volume [{:keys [track volume]}]
  (update-track-prop-value track :volume  volume))

(defn change-solo [{:keys [track solo]}]
  (update-track-prop-value track :solo  solo))



(defn process-osc-event [message]

  (let [osc-event (osc-loops/event-to-keyword message)
                                        ;        model (osc-loops/model-related osc-event)

         ]

    (condp = osc-event
      :clip
      (let [antro-loop (read-clip-info message)]
        (swap! antropo-loops assoc (select-keys antro-loop [:track :clip] ) antro-loop ))

      :loopend (change-loop-end (osc-loops/map-direct-get message [ [:loopend-value 0 :floatValue]]))


      :info (change-loop-state (osc-loops/map-direct-get message [ [:track-value 0 :intValue] [:clip-value 1 :intValue] [:state-value 2 :intValue]]))

      :volume (change-volume  (osc-loops/map-direct-get message [[:track 0 :intValue] [:volume 1 :floatValue]]) )
     :solo (change-solo (osc-loops/map-direct-get message [[:track 0 :intValue] [:solo 1 :intValue]])  )
     :tempo (def tempo (osc-loops/map-direct-get message [[:track 0 :floatValue] [:solo 1 :intValue]])  )
     (do #_(println "not mapped"))
     )
    )
  )



(defn async-request-related-loops-info-from-ableton []
  (println "async-request-related-clips-info-from-ableton")

  (def current-index (atom -1))
  (def antropo-loops-indexed (map key @antropo-loops))

  (doseq [loop antropo-loops-indexed]
    (osc-loops/make-async-call-for-loop loop)))

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


(comment
  (change-loop-state {:clip-value 0, :track-value 0 :state-value 1})
  (change-volume {:track 0 :volume 0.8})
  (change-volume {:track 2 :volume 1.8}))
