(ns dat00.protocols

  (:import [geomerative RShape RG RPoint RFont]
           [processing.core PApplet ]
           [toxi.color TColor ColorList ColorRange NamedColor]
           [toxi.color.theory ColorTheoryStrategy ColorTheoryRegistry]
           ))

(defmulti get-count type)

(defmethod get-count toxi.color.ColorList [a]
  (.size a))
(defmethod get-count clojure.lang.LazySeq [a]
  (count a))



(defmulti rgb type)
(defmethod rgb toxi.color.TColor [a]
  (.toARGB a))



(comment (get-count (range 5)))
