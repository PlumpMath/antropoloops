(ns dat00.protocols

  (:import [geomerative RShape RG RPoint RFont]
           [processing.core PApplet ]
           [toxi.color TColor ColorList ColorRange NamedColor]
           [toxi.color.theory ColorTheoryStrategy ColorTheoryRegistry]
           ))

(comment (defmulti count type)

         (defmethod count toxi.color.ColorList [a]
           (.size a))
         (defmethod count clojure.lang.LazySeq [a]
           (count a)))

(defprotocol countable
  (get-count [this]))

(extend-protocol countable
  ColorList
  (get-count [this]
    (.size this))
  clojure.lang.LazySeq
    (get-count [this]
    (.count this))
  )

(get-count (ColorList.))
(get-count (range 5))



(comment "doesn't work!"
         (extend-type  ColorList
   clojure.lang.Counted

   (count [this]
     (.size this))))


(defmulti rgb type)
(defmethod rgb toxi.color.TColor [a]
  (.toARGB a))



(comment (get-count (range 5)))
