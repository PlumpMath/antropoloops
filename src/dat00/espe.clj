(ns dat00.espe
  (:use
   [dat00.oscloops :as osc-loops]
   [dat00.graphics :as g]
   [dat00.antropoloops :as antropoloops]
   quil.core
   [ dat00.bd :as bd]
   [clojure.data.json :as json :only [read-str]]
   [dat00.util :as ut]
   ))

(def drawing (atom false))

(defn setup  []

  (ut/remove-file "tmp/history.json")
         (ut/remove-file "tmp/aloops.json")
         (ut/new-io-file "tmp/history.json")

  (g/setup-graphics)
  (g/load-resources))

(defn draw []
  (g/draw-background)
  (g/draw-antropoloops-credits)
  (when (and (not-empty @antropoloops/antropo-loops) @drawing)
    (text "drawing!!" 10 175)
    (let [m (millis)
          active-loops (filter (fn [v]
                                 (let [{:keys [state]} (val v)]
                                   (= state 2 )))
                               @antropoloops/antropo-loops)]
      (doseq [a-loop  active-loops]
        (let [active-loop (val a-loop)
              song (:song active-loop)
              lugar (:lugar active-loop)
              track (int (:track active-loop))
              posicion-x-disco (* 160 track)
              fecha (:fecha song)]
          (g/draw-album active-loop posicion-x-disco fecha)
          (g/draw-line-country active-loop posicion-x-disco lugar)
          (g/draw-abanico-country active-loop lugar tempo m)
          )))))

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
    \6 (do
         (ut/write-io "tmp/aloops.json" @antropoloops/antropo-loops)
         (ut/write-io "tmp/history.json" @antropoloops/history))
    \7  (do
          (def sess-loop (ut/get-test-json "resources/loop-session/aloops.json"))
          antropoloops/antropo-loops
          (def f-sess-loop (first sess-loop))
          (doall (map
                  (fn [f-sess-loop] (antropoloops/load-clip (merge (key f-sess-loop) {:nombre (:nombre (val f-sess-loop))}) ))
                  sess-loop))

          (def sess-history (ut/get-test-json "resources/loop-session/history.json"))
          (def different-keys (reduce conj #{} (map keys sess-history)))



          )
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
     :volume  (do (println "cHANGE VOLUME!!") (antropoloops/change-volume event))
     :solo   (do (println "CHANGE SOLO!!") (antropoloops/change-solo event))
     :state (do (println "change STate!!") (antropoloops/change-loop-state {:clip-value (:clip event)
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

(comment "testing antropoloops API!!!"
         (antropoloops/load-tempo 0.5)
         (antropoloops/change-loop-state {:clip-value 4, :track-value 1 :state-value 2})

  (antropoloops/change-volume {:track 1 :volume 3.8})
  (antropoloops/change-volume {:track 1 :volume 0.8})

(comment
            (let  [facts sess-history
                   init-time (:time (first sess-history))]
              (reduce (fn [c hist]
                        (let [i (:time hist)
                              wait-for (-  i c)
                              ]
                          ;(Thread/sleep wait-for)
                          (println wait-for)
;                          (println  hist)
                          (process-history-event hist)
                          i
                          ) )
                      init-time
                       facts) )
            )
  )
