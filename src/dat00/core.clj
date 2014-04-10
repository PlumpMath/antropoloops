(ns dat00.core
      (:import
     [oscP5 OscP5 OscMessage ]
     [netP5 NetAddress Logger]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn make-call [name val]
  (list (symbol (str ".set" name) val)))

(defmacro m-call [name val]
  (list(symbol (str "." name))  val))

(defmacro message-call [message pos-castto-name-seq]


  (reduce
   (fn [cont pos-castto-name]
     (apply assoc cont (let [[pos# cast-to# name#]  pos-castto-name
            the-value# (list (symbol (str "." cast-to#)) (.get message pos#)) ]
        [ (keyword (str name#)) the-value#]
        ))
     )
   {} pos-castto-name-seq)

  )
;{ (keyword name#) (list(symbol (str "." cast-to#))  (.get ~message pos#))}

(defmacro message-call-def [message  pos-castto-name-seq]


  (reduce
   (fn [cont# pos-castto-name#]
     (apply assoc cont# (let [[pos# cast-to# name#]  pos-castto-name#
                             o# `(.get ~message pos#)
            the-value# (list (symbol (str "." cast-to#)) o#) ]
        [ (keyword (str name#)) (eval the-value#)]
        ))
     )
   {} ~pos-castto-name-seq)

  )

(defmacro juan [hola]

   (str hola))


(defmacro juan3 [message seq-fns-poss-names]
  `(doseq [o# ~seq-fns-poss-names ]
     (let [[fn-# pos#] o#]
       (println (eval (list fn-# (keyword (str (.get  ~message pos#)))))))))
(defmacro juan22 [message seq-fns-poss-names]
  `(doseq [o# ~seq-fns-poss-names ]
     (let [[fn-# pos# name#] o#]
       (println [(keyword name#) (eval (list fn-#  (.get  ~message pos#)))]))))
(defmacro juan2 [message seq-fns-poss-names]
  `(reduce (fn [c# o#]
             (apply assoc c# (let [[fn-# pos# name#] o#
                                   v# (.get ~message pos#)]
                               [(keyword name#)   (eval `(list ~fn-#  ~v# ))  ]))
             ) {} ~seq-fns-poss-names

               ))


;(def jj (OscMessage. "jolin")  )

(def j (proxy [OscMessage] [""]

   (get [ pos]
     (println "ssss") pos)))

(defmacro map-set [class things]
  `(doto ~class ~@(map make-call things)))






(get-values-from-oscp5-message ["the message" "otro"] [:pepe 0 :toString ] [:juan 1 :getClass])
