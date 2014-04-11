(ns dat00.espe
  (:use
   [dat00.oscloops :as osc-loops]
   [dat00.graphics :as g]
   [dat00.antropoloops :as antropoloops]
   quil.core
   [ dat00.bd :as bd]
   [clojure.data.json :as json :only [read-str]]
   ))

(def drawing (atom false))

(defn setup  []
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
    \1 (antropoloops/reset)
    \2 (async-request-loops-info)
    \3 (do (println "draw running") (reset! drawing true))
    \4 (println "println loops indexed" antropoloops/antropo-loops-indexed)
    \5 (println "print misatropolops" @antropoloops/antropo-loops)
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


(comment "testing antropoloops API!!!"
  (antropoloops/change-loop-state {:clip-value 0, :track-value 0 :state-value 2})
  (antropoloops/change-volume {:track 0 :volume 1.8})
  (antropoloops/change-volume {:track 2 :volume 1.8}))
