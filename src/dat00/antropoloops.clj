(ns dat00.antropoloops
  (:use [ dat00.bd :as bd]
        [dat00.oscloops :as osc-loops]
        [dat00.util :as ut]
        [clj-time.local :as l]
          quil.core))

(declare    antropo-loops-indexed   tempo current-index )
(l/local-now)

(def antropo-loops (atom {}))


;; ANTROPOLOOPS API
(defn change-loop-end [loopend-value]
  (let [the-index (nth antropo-loops-indexed (inc @current-index))]
    (swap!  antropo-loops assoc-in [the-index :loopend] loopend-value)
    (swap! current-index inc)))

(defn change-loop-state [{:keys [track-value clip-value state-value]} ]
  (swap!  antropo-loops assoc-in [{:clip clip-value :track track-value} :state] state-value)
  )

(defn- update-track-prop-value [track-value the-keyword the-value]
  (let [coincidences (filter (fn [v]
                               (let [{:keys [track clip]} (key v)]
                                 (= track-value track )))
                             @antropo-loops)]
    (doseq [c coincidences]
      (swap!  antropo-loops assoc-in [(key c) the-keyword] the-value))))

(defn change-volume [{:keys [track volume]}]
  (update-track-prop-value track :volume  volume))

(defn change-solo [{:keys [track solo]}]
  (update-track-prop-value track :solo  solo))

(defn load-clip [{:keys [track clip nombre] } ]
  ;; TODO: check that exist a place and a song if not throw an exception
  (let [song (first (filter #(= (:nombreArchivo %) nombre) bd/loops))
        antro-loop {:nombre nombre :track track :clip clip
                    :color-s (random 50 100 )
                    :color-b (random 80 100)
                    :color-h (condp = (int track)
                               0 (random 105 120)
                               1 (random 145 160)
                               2 (random 300 315)
                               3 (random 330 345)
                               4 (random 195 210)
                               5 (random 230 245)
                               6 (random 25 40)
                               7 (random 50 65)
                               )
                    :image  (load-image (str "resources/0_portadas/" nombre  ".jpg"))
                    :song  song
                    :lugar (first (filter #(= (:lugar %) (:lugar song)) bd/lugares))
                    }
        ]
    (ut/append-io "tmp/history.json" (assoc {:id "load-clip" :time (l/local-now) } antro-loop))
    (swap! antropo-loops assoc (select-keys antro-loop [:track :clip] ) antro-loop )
    )
  )

(defn reset[]
  (reset! antropo-loops {})
  (osc-loops/make-async-call-for-all-clips)
  )

;; antropoloops oscp5 related
(defn async-request-loops-info []
  (println "async-request-related-clips-info-from-ableton")

  (def current-index (atom -1))
  (def antropo-loops-indexed (map key @antropo-loops))

  (doseq [loop antropo-loops-indexed]
    (osc-loops/make-async-call-for-loop loop)))


;; OSCP5 processing events listener
(defn process-osc-event [message]
  (let [osc-event (osc-loops/event-to-keyword message)]

    (condp = osc-event
      :clip (load-clip (osc-loops/read-clip-info message))
      :loopend (change-loop-end (osc-loops/map-direct-get message [ [:loopend-value 0 :floatValue]]))
      :info (change-loop-state (osc-loops/map-direct-get message [ [:track-value 0 :intValue] [:clip-value 1 :intValue] [:state-value 2 :intValue]]))
      :volume (change-volume  (osc-loops/map-direct-get message [[:track 0 :intValue] [:volume 1 :floatValue]]) )
      :solo (change-solo (osc-loops/map-direct-get message [[:track 0 :intValue] [:solo 1 :intValue]])  )
      :tempo (def tempo (osc-loops/map-direct-get message [[:tempo 0 :floatValue]])  )
      (do #_(println "not mapped"))
      )
    )
  )
