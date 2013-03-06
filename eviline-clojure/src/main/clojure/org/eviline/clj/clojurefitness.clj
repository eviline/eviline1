(ns org.eviline.clj.clojurefitness
  (:gen-class
    :name org.eviline.clj.ClojureFitness
    :implements [org.eviline.fitness.Fitness]
    :main false
    ))
(clojure.core/use 'clojure.core)
(import '(org.eviline.fitness Fitness))
(import '(org.eviline Field Block))

(def M Block/M)
(def X Block/X)
(def expected-X-count (* Field/WIDTH Field/BUFFER))
(def empty-row (repeat (+ Field/WIDTH Field/BUFFER Field/BUFFER) nil))
(def empty-row-list (list empty-row))

(defn count-row [row]
  (- Field/WIDTH (count (filter nil? row)))
  )

(defn count-field [field-array]
  (- (reduce + (map count-row field-array)) expected-X-count)
  )

(defn unpainting-reversing-reducer [lhs b] 
  (cons (if (and (= M b) (nil? (first lhs))) nil b) lhs))

(defn unpaint-row-reversing [row]
  (reduce unpainting-reversing-reducer '() row)
  )

(defn block-max-painter [b ba] (if (nil? b) (if (nil? ba) nil M) b))

(defn paint-impossibles-row-reducer [prev-rows row-array]
  (let [
        max-painted (map block-max-painter row-array (first prev-rows))
        min-painted (unpaint-row-reversing (unpaint-row-reversing max-painted))
        ]
    (cons min-painted prev-rows)))

(defn paint-impossibles [field-array]
  (rest (reverse (reduce paint-impossibles-row-reducer empty-row-list field-array)))
  )

(defn to-field-array [field-array]
  (into-array (map (fn [row] (into-array Block row)) field-array))
  )

(defn count-impossibles-row [row]
  (* 200 (count (filter #(= M %) row)))
  )

(defn count-impossibles [field] 
  (reduce + (map count-impossibles-row field))
  )

(defn score-block-array [field-array]
  (double (+
    (count-field field-array)
    (count-impossibles field-array)
    )))

(defn -score [this field] 
  (score-block-array (.getField field)))

(defn -prepareField [this field] 
  (.setField field (to-field-array (paint-impossibles (.getField field))))
  field)

