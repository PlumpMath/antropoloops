(ns dat00.analysis)





(defn get-changes-of-state [data]
  (filter (fn [i] (not (nil? (:state i)))) data)
  )


(defn keyword-tracks [] (map (comp  keyword str) (range 0 8)))


(defn get-ordered-changes [data]
  (loop [stock (reduce #(assoc %  %2 []) {} (keyword-tracks))
         changes-of-state (get-changes-of-state data)
         ]
    (let [change (first changes-of-state)

          stock-mod (update-in stock [(keyword (str (:track change)))] conj change)
          ]
      (if (nil? (next changes-of-state))
        stock-mod
        (recur stock-mod (next changes-of-state))
        )

      )

    ))




(defn get-changes-state-track[ordered-changes track data-sess-loop]
  (let [
        active-1 (filter #(= 2 (:state %)) (track ordered-changes))
        a-1 (map #(data-sess-loop (select-keys % [ :clip :track]))  active-1)]
    (map (fn [a] (merge
                 {}
                 ;{:pais (get-in a [:song :lugar])}
                 (select-keys (:lugar a) [:coordX :coordY ])
                 ;(select-keys (:song a) [:titulo])
                 ) ) a-1)
   ))





;(map (juxt :state :clip :track :time) (filter #(not= 3 (:state %)) (:1 ordered-changes)))




;(antropoloops/change-loop-state {:clip-value 4, :track-value 1 :state-value 2})
#_(let [e (first   active-1)]
  (process-history-event e)
  (antropoloops/change-volume {:track 1 :volume 0.8})
  e
  )
