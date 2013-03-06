(ns org.eviline.fitness.clojurefitness
  (:gen-class
    :name org.eviline.fitness.ClojureFitness
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

(defn paint-impossibles [field]
  field
  )

(defn count-impossibles-row [rev-index row]
  
  )

(defn count-impossibles [field] 
  (let (painted (paint-impossibles (.getField field)))
    (reduce + (map-indexed count-impossibles-row (reverse painted)))
    )
  )

(defn score [field] 
  (+
    (count-field field)
    ))

(defn -newFitness []
  (proxy [Fitness] []
    (score [field] (score field))
    (prepareField [field])
    )
  )

