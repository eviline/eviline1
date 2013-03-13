(ns org.eviline.clj.ClojureAIKernel
  (:gen-class 
    :name org.eviline.clj.ClojureAIKernel
    :extends org.eviline.ai.DefaultAIKernel
    :main false
    :init init
    :post-init post-init
    :exposes-methods {bestFor superBestFor}
    ))
(clojure.core/use 'clojure.core)
(require 'org.eviline.clj.LazyMap)
(import '(org.eviline Field Shape ShapeType PlayerAction PlayerActionType PlayerActionNode))
(import '(org.eviline.ai DefaultAIKernel Context Decision DecisionModifier QueueContext))
(import '(org.eviline.fitness Fitness AbstractFitness))

(def delay-pool-executor (java.util.concurrent.Executors/newCachedThreadPool))

(defn pool-force [delayed] 
  (let [jfut (.submit delay-pool-executor #(force delayed))]
    (delay (.get jfut))
    ))

(defn -init [] [[]])
(defn -post-init [this])

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
  (.setShape field (:shape sxy))
  (.setShapeX field (:x sxy))
  (.setShapeY field (:y sxy))
  (.paintImpossibles fitness field)
  (->ScoredShapeXY 
    field
    (- (.score fitness field) (* 10000 (Math/pow (.getLines field) 1.5)))
    (:shape sxy)
    (:x sxy)
    (:y sxy))
  )

(defn ^Decision -bestFor 
  ([this ^Context context ^ShapeType type]
    (let [fitness (.getFitness this)
          painted-field (.paintedImpossible context)
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
(defn path-ancestor-list [path]
  (if (nil? path) '() (list* path (path-ancestor-list (:origin path)))))

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

(defn extend-path-das-left [^Field field ^PathShapeXY path])

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
      (extend-path field-copy path PlayerActionType/ROTATE_RIGHT (.getShape field-copy) (.getShapeX field-copy) (.getShapeY field-copy)))))

(defn extend-path-counterclockwise [^Field field ^PathShapeXY path]
  (let [field-copy (.copy field)
        ]
    (.setShape field-copy (:shape path))
    (.setShapeX field-copy (:x path))
    (.setShapeY field-copy (:y path))
    (.rotateLeft field-copy)
    (if (not= (:shape path) (.getShape field-copy))
      (extend-path field-copy path PlayerActionType/ROTATE_LEFT (.getShape field-copy) (.getShapeX field-copy) (.getShapeY field-copy)))))

(defn extend-path-singly [^Field field ^PathShapeXY path]
  (remove nil?  
          (list 
            (if (:down? path) (extend-path-down field path))
            (if (:lhs? path) (extend-path-left field path))
            (if (:rhs? path) (extend-path-right field path))
            (extend-path-clockwise field path)
            (extend-path-counterclockwise field path)
            )))

(defn extend-path-singly-down [^Field field ^PathShapeXY path]
  (remove nil?  
          (list 
            (if (:down? path) (extend-path-down field path))
            )))

(defn extend-path-singly-horiz [^Field field ^PathShapeXY path]
  (remove nil?  
          (list 
            (if (:lhs? path) (extend-path-left field path))
            (if (:rhs? path) (extend-path-right field path))
            (extend-path-clockwise field path)
            (extend-path-counterclockwise field path)
            )))


(defn shorter-path? [state ^PathShapeXY path]
  (locking state
    (if (or (nil? (get @state (:shxy path))) (< (:length path) (:length (get @state (:shxy path)))))
      (reset! state (assoc @state (:shxy path) path))
      )))

(defn still-shortest? [state ^PathShapeXY path lookback]
  (locking state
    (cond 
      (<= 0 lookback) true
      (nil? (:origin path)) true
      (= path (get @state (:shxy path))) (still-shortest? state (:origin path) (dec lookback))
      )))

(defn extend-path-fully-rec-adjacentfn [state ^Field field ^PathShapeXY path adjacentfn]
  (let [adjacent (adjacentfn field path)
        shorter-paths (filter (fn [p] (shorter-path? state p)) adjacent)
        still-shorter (filter #(still-shortest? state % 10) shorter-paths)
        ]
    (doall (map extend-path-fully-rec-adjacentfn (repeat state) (repeat field) still-shorter (repeat adjacentfn)))
    state
    )
  )


(defn extend-path-fully-rec [state ^Field field ^PathShapeXY path]
  (extend-path-fully-rec-adjacentfn state field path extend-path-singly)
  )

(defn extend-path-fully-horiz-rec [state ^Field field ^PathShapeXY path]
  (extend-path-fully-rec-adjacentfn state field path extend-path-singly-horiz)
  )


(defn extend-path-fully [^Field field ^PathShapeXY path]
  (vals @(extend-path-fully-rec (atom {}) field path)))

(defn shapexy-to-player-action-node [^Field field ^ShapeXY shxy]
  (PlayerActionNode. (:shape shxy) (:x shxy) (:y shxy)))

(defn shapexy-to-field [^Field field ^ShapeXY shxy]
  (let [fc (.copy field)]
    (.setShape fc (:shape shxy))
    (.setShapeX fc (:x shxy))
    (.setShapeY fc (:y shxy))
    fc
    )
  )

(defn path-head-to-player-action [^Field field ^PathShapeXY path]
  (if-not (nil? (:origin path))
    (PlayerAction. (shapexy-to-field field (:shxy (:origin path))) (:move path) (shapexy-to-field field (:shxy path)))))


;(defn path-to-player-action-reverse-list [^Field field ^PathShapeXY path]
;  (map path-head-to-player-action (repeat field) (path-ancestor-list path))
;  )

(defn path-to-player-action-reverse-list [^Field field ^PathShapeXY path]
  (let [ancestors (reverse (path-ancestor-list path))
        start (first ancestors)
        next (second ancestors)
        first-action (PlayerAction. (shapexy-to-field field (:shxy start)) (:move next) (shapexy-to-field field (:shxy next)))
        ]
    (reduce 
      (fn [actions path-head]
        (list* 
          (PlayerAction. (.getEndField (first actions)) (:move path-head) (shapexy-to-field field (:shxy path-head)))
          actions
          )
        )
      (list first-action)
      (drop 2 ancestors)
      )
    )
  )

(defn -allPathsFrom [this ^Field field]
  (let [fc (.copy field)
        ]
    (let [start-path (extend-path fc nil nil (.getShape fc) (.getShapeX fc) (.getShapeY fc))
          shxys-to-h-paths-state (extend-path-fully-horiz-rec (atom { (:shxy start-path) start-path }) fc start-path)
          adjacentfn (cond
                       (.isHardDropOnly this) extend-path-singly-down
                       :else extend-path-singly
                       )
          swimming (doall (map
                            (fn [start] (pool-force (delay (extend-path-fully-rec-adjacentfn shxys-to-h-paths-state fc start adjacentfn) start)))
                            (vals @shxys-to-h-paths-state)))
          ]
      (doall (map #(force %) swimming))
      (reduce (fn [lazymap keyval] 
                              (.lazyPut
                                lazymap
                                (shapexy-to-player-action-node fc (key keyval))
                                (delay (reverse (path-to-player-action-reverse-list fc (val keyval)))))
                              lazymap)
                            (new org.eviline.clj.LazyMap (new java.util.HashMap))
                            @shxys-to-h-paths-state)
      )
    )
  )

