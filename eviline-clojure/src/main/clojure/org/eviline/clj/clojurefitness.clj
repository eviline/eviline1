(ns org.eviline.clj.clojurefitness
  (:gen-class
    :name org.eviline.clj.ClojureFitness
    :implements [org.eviline.fitness.Fitness]
    :methods [
              ;#^{:static true} [ newFitness [] org.eviline.fitness.Fitness]
              ]))
(clojure.core/use 'clojure.core)
(import '(org.eviline.fitness Fitness))
(import '(org.eviline Field Block))

(def M Block/M)
(def X Block/X)
(def expected-X-count (* Field/WIDTH Field/BUFFER))
(def empty-row (repeat (+ Field/WIDTH Field/BUFFER Field/BUFFER) nil))

(defn count-row [row]
  (- Field/WIDTH (count (filter nil? row)))
  )

(defn count-field [field]
  (- (reduce + (map count-row (.getField field))) expected-X-count)
  )

(defn unpaint-row-reversing [row]
  (reduce (fn [lhs b] (cons (if (and (= M b) (nil? (first lhs))) nil b) lhs)) '() row)
  )

(defn paint-impossibles-row-reducer [prev-rows row-array]
  (let [
        max-painted (map (fn [b ba] (if (nil? b) (if (nil? ba) nil M) b)) row-array (first prev-rows))
        min-painted (unpaint-row-reversing (unpaint-row-reversing max-painted))
        ]
    (cons min-painted prev-rows)))

(defn paint-impossibles [field]
  (let [
        rev-painted (reduce paint-impossibles-row-reducer (list empty-row) (.getField field))
        painted (reverse rev-painted)
        ]
    (rest painted)
    ))


(defn count-impossibles-row [row]
  (* 200 (count (filter #(= M %) row)))
  )

(defn count-impossibles [field] 
  (reduce + (map count-impossibles-row (paint-impossibles field)))
  )

(defn prepare-field-inplace [field]
  field
  )

(defn -score [self field] 
  (double (+
    (count-field field)
    (count-impossibles field)
    )))

(defn -prepareField [self field] field)

