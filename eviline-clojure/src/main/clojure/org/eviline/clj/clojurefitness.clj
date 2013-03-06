(ns org.eviline.clj.clojurefitness
  (:gen-class
    :name org.eviline.clj.ClojureFitness
    :methods [#^{:static true} [ newFitness [] org.eviline.fitness.Fitness]]))
(clojure.core/use 'clojure.core)
(import '(org.eviline.fitness Fitness))
(import '(org.eviline Field Block))

(defn count-row [rev-index row]
  (* rev-index (count (filter #(and (not (nil? %)) (not (= Block/X %))) row)))
  )

(defn count-field [field]
  (reduce + (map-indexed count-row (reverse (.getField field))))
  )

(defn paint-impossibles-row [memo row]
  (let [
        prev (if (= 0 (count memo)) (repeat (+ Field/WIDTH Field/BUFFER Field/BUFFER) nil) (peek memo))
        m-row (map (fn [b] (if (nil? b) Block/M b)) row)
        m-nils-row (map-indexed (fn [i b] (if (nil? (nth prev i)) nil b)) m-row)
        paint-across (fn [r b] (conj r (if (and (nil? (peek r)) (= Block/M b)) nil b)))
        m-right-row (reduce paint-across [] m-nils-row)
        m-left-row (reduce paint-across [] (reverse m-right-row))
        painted-row (reverse m-left-row)
        ]
    (conj memo (vec painted-row))
    )
  )

(defn paint-impossible-rows [field]
  (reduce paint-impossibles-row [] (vec field))
  )

(defn count-impossibles-row [rev-index row]
  (* 200 (count (filter #(= Block/M %) row)))
  )

(defn count-impossibles [field] 
  (reduce + (map-indexed count-impossibles-row (.getField field)))
  )

(defn prepare-field-inplace [field]
  (let [
        field-array (.getField field)
        painted-impossible (paint-impossibles-row field-array)
        
        ]
    (map-indexed 
      (fn [row-index row] 
        (let [painted-row (nth painted-impossible row-index)
              ]
          (map-indexed (fn [col-index b] (aset row col-index b)))
          )
        )
      field-array)
    )
  field
  )

(defn score [field] 
  (+
    (count-field field)
    (count-impossibles field)
    ))

(defn -newFitness []
  (proxy [Fitness] []
    (score [field] (score field))
    (prepareField [field] (prepare-field-inplace (.copy field)))
    )
  )

