(ns org.eviline.fitness.clojurefitness
  (:gen-class
    :name org.eviline.fitness.ClojureFitness
    :methods [#^{:static true} [ newFitness [] org.eviline.fitness.Fitness]]))
(clojure.core/use 'clojure.core)
(import '(org.eviline.fitness Fitness))
(import '(org.eviline Field Block))

(defn score-row [index row]
  (* index (count (filter #(and (not (nil? %)) (not (= Block/X %))) row)))
  )

(defn score [field]
  (reduce + (map-indexed score-row (reverse (.getField field))))
  )


(defn -newFitness []
  (proxy [Fitness] []
    (score [field] (score field))
    (prepareField [field])
    )
  )

