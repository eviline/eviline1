(ns org.eviline.clj.clojureaikernel
  (:gen-class 
    :name org.eviline.clj.ClojureAIKernel
    :extends org.eviline.ai.DefaultAIKernel
    :main false
    :init init
    :constructors {[org.eviline.fitness.Fitness] []}
    :post-init post-init
    :exposes-methods {setFitness superSetFitness, bestFor superBestFor}
    ))
(clojure.core/use 'clojure.core)
(import '(org.eviline Field Shape ShapeType))
(import '(org.eviline.ai DefaultAIKernel Context Decision DecisionModifier QueueContext))
(import '(org.eviline.fitness Fitness AbstractFitness))

(defn -init [fitness] [[]])
(defn -post-init [this fitness] (.superSetFitness this fitness))
(defn -setFitness [this fitness] (throw (UnsupportedOperationException.)))

(defrecord ShapeXY [shape x y])
(defrecord ScoredShapeXY [^Field field ^double score ^Shape shape ^long x ^long y])

(def x-range (range (- Field/BUFFER 2) (+ Field/BUFFER Field/WIDTH 2)))
(def y-range (range 0 (+ Field/BUFFER Field/HEIGHT 2)))
(defn shape-intersects-below? [^Field field ^Shape shape ^long x ^long y]
  (if (.intersects shape (.getField field) x (inc y)) (->ShapeXY shape x y)))
(defn y-intersections [^Field field ^Shape shape ^long x]
  (remove nil? (map shape-intersects-below? (repeat field) (repeat shape) (repeat x) y-range)))
(defn y-grounded-reducer [grounded xy]
  (cons xy (if (= (inc (:y (first grounded))) (:y xy)) (rest grounded) (cons xy (rest grounded))))
  )
(defn y-grounded 
  ([xy-pairs]
    (rest (reduce y-grounded-reducer (repeat (if (= 0 (:y (first xy-pairs))) 1 2) (first xy-pairs)) (rest xy-pairs)))
    )
  ([^Field field ^Shape shape ^long x]
    (y-grounded (y-intersections field shape x)))
  ([^Field field ^Shape shape]
    (mapcat y-grounded (repeat field) (repeat shape) x-range))
  )
(defn grounded-locations 
  ([^Field field ^ShapeType type] 
    (mapcat y-grounded (repeat field) (.searchOrientations type))
    )
  )

(defn ^ScoredShapeXY score [^AbstractFitness fitness ^Field field ^ShapeXY sxy]
  (.setLines field 0)
  (.setShape field (:shape sxy))
  (.setShapeX field (:x sxy))
  (.setShapeY field (:y sxy))
  (.clockTick field)
  (let [field-copy (.copy field)
        ]
		  (.setShape field-copy (:shape sxy))
		  (.setShapeX field-copy (:x sxy))
		  (.setShapeY field-copy (:y sxy))
		  (.paintImpossibles fitness field-copy)
		  (->ScoredShapeXY 
		    field
		    (- (.score fitness field-copy) (* 10000 (Math/pow (.getLines field-copy) 1.5)))
		    (:shape sxy)
		    (:x sxy)
		    (:y sxy))
    )
  )

(defn ^Decision -bestFor 
  ([this ^Context context ^ShapeType type]
    (let [fitness (.getFitness this)
          painted-field (.copy (.paintedImpossible context))
          locations (grounded-locations painted-field type)
          original-field (.original context)
          scored-locations (map score (repeat fitness) (repeatedly #(.copy original-field)) locations)
          best (apply min-key #(:score %) scored-locations)
          decision (Decision. type (:score best) (:field best) (:shape best) (:x best) (:y best))
          ]
      decision))
  ([this arg]
    (cond
      (instance? Field arg) (.superBestFor this arg)
      (instance? QueueContext arg) (.superBestFor this arg)
      (instance? Context arg) (.superBestFor this arg)))
  )


