(ns org.eviline.clj.clojureaikernel
  (:gen-class 
    :name org.eviline.clj.ClojureAIKernel
    :extends org.eviline.ai.DefaultAIKernel
    :main false
    :init init
    :constructors {[org.eviline.fitness.Fitness] []}
    :post-init post-init
    :exposes-methods {setFitness superSetFitness}
    ))
(clojure.core/use 'clojure.core)
(import '(org.eviline Field Shape ShapeType))
(import '(org.eviline.ai DefaultAIKernel Context Decision DecisionModifier QueueContext))

(defn -init [fitness] [[]])
(defn -post-init [this fitness] (.superSetFitness this fitness))
(defn -setFitness [this fitness] (throw (UnsupportedOperationException.)))

(defrecord ShapeXY [shape x y])
(def x-range (range (- Field/BUFFER 2) (+ Field/BUFFER Field/WIDTH 2)))
(def y-range (range 0 (+ Field/BUFFER Field/HEIGHT 2)))
(defn shape-intersects? [^Field field ^Shape shape ^long x ^long y]
  (if (.intersects shape (.getField field) x y) (->ShapeXY shape x y)))
(defn y-intersections [^Field field ^Shape shape ^long x]
  (remove nil? (map shape-intersects? (repeat field) (repeat shape) (repeat x) y-range)))
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

(defn ^Decision -bestFor [^Context context ^ShapeType type]
  
  )