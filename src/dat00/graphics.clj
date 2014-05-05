(ns dat00.graphics
  (:use
   quil.core))

(declare mundi font tucan)

(defn draw-antropoloops-credits []
  (fill 255)
  (text "antropoloops MAP 1.0" 10 775)
  (fill 120)
  (text "by MI-MI NA" 10 790)
)

(defn draw-album [active-loop posicion-x-disco fecha]
  (image (:image active-loop) posicion-x-disco 0 160 160)
  (fill (:color-h active-loop) (:color-s active-loop) (:color-b active-loop) 45 )
  (text fecha (+ 5 posicion-x-disco) 190)
  )
(defn draw-line-country [active-loop posicion-x-disco lugar]
   (stroke (:color-h active-loop) (:color-s active-loop) (:color-b active-loop) (* 100 (:volume active-loop)) )
  (stroke-weight 1)
  (line posicion-x-disco 189 (:coordX lugar) (:coordY lugar))
)
(defn abanica [x y d h s b]
  (doseq [i (range 20)]
    (stroke h s b)
    (stroke-weight 0.5)
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

(defn draw-abanico-country [active-loop lugar tempo m]
  #_(do (println active-loop)
           (println (radians (/ m (/ 60 (* tempo (int (:loopend active-loop)))))))
           (println tempo)
           (println m)
           (println "******"))

  (do (push-matrix)
           (translate (:coordX lugar) (:coordY lugar))
           (rotate (radians (/ m (/ 60 (* tempo (int (:loopend active-loop)))))))
           (abanica (:coordX lugar) (:coordY lugar) (* 100 (:volume active-loop)) (:color-h active-loop) (:color-s active-loop) (:color-b active-loop))
           (pop-matrix))
)
(defn setup-graphics []
  (color-mode :rgb 1 1 1 100))

(defn load-resources []
  (def mundi (load-image "resources/1_BDatos/mapa_1280x800.png"))

  (def font (load-font "resources/1_BDatos/ArialMT-20.vlw"))
      (def tucan (load-shape "data/mapam.svg"))
  )

(def color-bg 0)

(defn draw-background []
  (background 1)
;  (image mundi 0 0 )
  (fill color-bg)
  (no-stroke)
  (rect 0 0 (width) 160)
)
(defn draw-svg []
  (shape tucan -65 180 1400 710))
