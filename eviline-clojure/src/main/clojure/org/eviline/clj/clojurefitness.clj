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

(defn paint-impossibles [field]
  (reduce paint-impossibles-row [] (vec field))
  )

(defn count-impossibles-row [rev-index row]
  (* 200 (count (filter #(= Block/M %) row)))
  )

(defn count-impossibles [field] 
  (let [painted (paint-impossibles (.getField field))]
    (reduce + (map-indexed count-impossibles-row (reverse painted)))
    )
  )

(defn score [field] 
  (+
    (count-field field)
    (count-impossibles field)
    ))

(defn -newFitness []
  (proxy [Fitness] []
    (score [field] (score field))
    (prepareField [field])
    )
  )

