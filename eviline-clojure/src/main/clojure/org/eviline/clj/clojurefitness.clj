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

(defn count-row [rev-index row]
  (* rev-index (count (filter #(and (not (nil? %)) (not (= X %))) row)))
  )

(defn count-field [field]
  (reduce + (map-indexed count-row (reverse (.getField field))))
  )

(defn unpaint-row-reversing [row]
  (reduce (fn [lhs b] (cons (if (and (= M b) (nil? (first lhs))) nil b) lhs)) '() row)
  )

(defn paint-impossibles [field]
  (let [
        empty-row (repeat (+ Field/WIDTH Field/BUFFER Field/BUFFER) nil)
        rev-painted (reduce (fn [prev-rows row-array]
                          (let [
                                ; The row above us
                                above (first prev-rows)
                                ; current row with all nills set to M unless nill above
                                max-painted (map-indexed (fn [index b] (if (nil? b) (if (nil? (nth above index)) nil M) b)) row-array)
                                min-painted (unpaint-row-reversing (unpaint-row-reversing max-painted))
                                ]
                            (cons min-painted prev-rows))) (list empty-row) (.getField field))
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

