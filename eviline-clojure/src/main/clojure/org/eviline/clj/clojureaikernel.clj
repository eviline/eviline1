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
(import '(org.eviline Field Shape ShapeType PlayerAction PlayerActionType))
(import '(org.eviline.ai DefaultAIKernel Context Decision DecisionModifier QueueContext))
(import '(org.eviline.fitness Fitness AbstractFitness))

(defn -init [fitness] [[]])
(defn -post-init [this fitness] (.superSetFitness this fitness))
(defn -setFitness [this fitness] (throw (UnsupportedOperationException.)))

(defrecord ShapeXY [^Shape shape ^long x ^long y])
(defrecord ScoredShapeXY [^Field field ^double score ^Shape shape ^long x ^long y])

(def x-range (range (- Field/BUFFER 2) (+ Field/BUFFER Field/WIDTH 2)))
(def y-range (range 0 (+ Field/BUFFER Field/HEIGHT 2)))
(defn shape-intersects-below? [^Field field ^Shape shape ^long x ^long y]
  (if (.intersects shape (.getField field) x (inc y)) (->ShapeXY shape x y)))
(defn shape-intersects-left? [^Field field ^Shape shape ^long x ^long y]
  (if (.intersects shape (.getField field) (dec x) y) (->ShapeXY shape x y)))
(defn shape-intersects-right? [^Field field ^Shape shape ^long x ^long y]
  (if (.intersects shape (.getField field) (inc x) y) (->ShapeXY shape x y)))
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

(defrecord PathShapeXY [
                        origin 
                        ^PlayerActionType move
                        ^Shape shape 
                        ^long x 
                        ^long y 
                        ^boolean down?
                        ^boolean lhs? 
                        ^boolean rhs?
                        ^ShapeXY shxy
                        ^long length
                        ])

(defn path-extendable-down? [^Field field ^PathShapeXY path]
  (not (shape-intersects-below? field (:shape path) (:x path) (:y path))))
(defn path-extendable-left? [^Field field ^PathShapeXY path]
  (not (shape-intersects-left? field (:shape path) (:x path) (:y path))))
(defn path-extendable-right? [^Field field ^PathShapeXY path]
  (not (shape-intersects-right? field (:shape path) (:x path) (:y path))))

(defn extend-path [^Field field ^PathShapeXY origin ^PlayerActionType move ^Shape shape x y]
  (->PathShapeXY 
      origin 
      move
      shape 
      x 
      y
      (not (shape-intersects-below? field shape x y))
      (not (shape-intersects-left? field shape x y))
      (not (shape-intersects-right? field shape x y))
      (->ShapeXY shape x y)
      (if (nil? origin) 0 (inc (:length origin)))
      ))

(defn extend-path-down [^Field field ^PathShapeXY path]
  (if (path-extendable-down? field path)
    (extend-path field path PlayerActionType/DOWN_ONE (:shape path) (:x path) (inc (:y path)))))

(defn extend-path-left [^Field field ^PathShapeXY path]
  (if (path-extendable-left? field path)
    (extend-path field path PlayerActionType/SHIFT_LEFT (:shape path) (dec (:x path)) (:y path))))

(defn extend-path-right [^Field field ^PathShapeXY path]
  (if (path-extendable-right? field path)
    (extend-path field path PlayerActionType/SHIFT_RIGHT (:shape path) (inc (:x path)) (:y path))))

(defn extend-path-clockwise [^Field field ^PathShapeXY path]
  (let [field-copy (.copy field)
        ]
    (.setShape field-copy (:shape path))
    (.setShapeX field-copy (:x path))
    (.setShapeY field-copy (:y path))
    (.rotateRight field-copy)
    (if (not= (:shape path) (.getShape field-copy))
      (extend-path field path PlayerActionType/ROTATE_RIGHT (.getShape field-copy) (.getShapeX field-copy) (.getShapeY field-copy)))))

(defn extend-path-counterclockwise [^Field field ^PathShapeXY path]
  (let [field-copy (.copy field)
        ]
    (.setShape field-copy (:shape path))
    (.setShapeX field-copy (:x path))
    (.setShapeY field-copy (:y path))
    (.rotateLeft field-copy)
    (if (not= (:shape path) (.getShape field-copy))
      (extend-path field path PlayerActionType/ROTATE_LEFT (.getShape field-copy) (.getShapeX field-copy) (.getShapeY field-copy)))))

(defn path-has-origin? [^PathShapeXY path ^ShapeXY shapexy]
  (cond 
    (nil? path) nil
    (= shapexy (:shxy path)) true
    (nil? (:origin path)) nil
    :else (path-has-origin? (:origin path) shapexy)))

(defn path-is-looping? [^PathShapeXY path]
  (path-has-origin? (:origin path) (:shxy path)))

(defn extend-path-singly [^Field field ^PathShapeXY path]
  (remove nil?  
          (list 
            (if (:down? path) (extend-path-down field path))
            (if (and (:lhs? path) (or (nil? (:origin path)) (:lhs? (:origin path))))
              (extend-path-left field path))
            (if (and (:rhs? path) (or (nil? (:origin path)) (:rhs? (:origin path))))
              (extend-path-right field path))
            (extend-path-clockwise field path)
            (extend-path-counterclockwise field path)
            )))

(defn shorter-path? [state ^PathShapeXY path]
  (locking state
    (if (or (nil? (get @state (:shxy path))) (< (:length path) (:length (get @state (:shxy path)))))
      (do
        (swap! state into {(:shxy path) path})
        path
        )
      )))

(defn extend-path-fully-rec [state ^Field field ^PathShapeXY path]
  (let [adjacent (extend-path-singly field path)
        shorter-paths (filter (fn [p] (shorter-path? state p)) adjacent)
        ]
    (doall (map extend-path-fully-rec (repeat state) (repeat field) shorter-paths))
    state
    )
  )

(defn extend-path-fully [^Field field path]
  (vals @(extend-path-fully-rec (atom {}) field path)))

;(defn -allPathsFrom [^Field field]
;  
;  )

