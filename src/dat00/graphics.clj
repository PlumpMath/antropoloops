(ns dat00.graphics
  (:use
   quil.core))

(declare mundi font)

(defn draw-antropoloops-credits []
  (fill 255)
  (text "antropoloops MAP 1.0" 10 775)
  (fill 120)
  (text "by MI-MI NA" 10 790)
)

(defn setup-graphics []
  (color-mode :hsb 360 100 100))

(defn load-resources []
  (def mundi (load-image "resources/1_BDatos/mapa_1280x800.png"))

  (def font (load-font "resources/1_BDatos/ArialMT-20.vlw"))

  )

(defn draw-background []
  (background (unhex "2b2b2b"))
  (image mundi 0 0 )
  (fill 50)
  (no-stroke)
  (rect 0 0 (width) 160)
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
