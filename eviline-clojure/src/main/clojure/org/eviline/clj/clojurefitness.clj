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



(defn paint-impossibles-rows-inplace [field]
  (let [rev-painted (reduce (fn [prev-rows row-array]
                          (let [
                                ; The row above us
                                prev-row (last prev-rows)
                                above (if (nil? prev-row) (repeat (alength row-array) nil) prev-row)
                                ; current row with all nills set to M unless nill above
                                max-painted (map-indexed (fn [index b] (if (nil? b) (if (nil? (nth above index)) nil M) b)) row-array)
                                right-painted (reduce (fn [lhs b] (conj lhs (if (and (= M b) (nil? (peek lhs))) nil b))) '() max-painted)
                                left-painted (reduce (fn [lhs b] (conj lhs (if (and (= M b) (nil? (peek lhs))) nil b))) '() right-painted)
                                ]
                            (conj prev-rows left-painted))) '() (.getField field))
        painted (reverse rev-painted)
        ]
    (areduce (.getField field) i m nil (aset (.getField field) i (into-array Block (nth painted i))))))


(defn count-impossibles-row [rev-index row]
  (* 200 (count (filter #(= M %) row)))
  )

(defn count-impossibles [field] 
  (reduce + (map-indexed count-impossibles-row (.getField field)))
  )

(defn prepare-field-inplace [field]
  (paint-impossibles-rows-inplace field)
  field
  )

(defn -score [self field] 
  (double (+
    (count-field field)
    (count-impossibles field)
    )))

(defn -prepareField [self field] (prepare-field-inplace (.copy field)))

