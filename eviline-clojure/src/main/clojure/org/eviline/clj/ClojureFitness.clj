(ns org.eviline.clj.ClojureFitness
  (:gen-class
    :name org.eviline.clj.ClojureFitness
    :extends org.eviline.fitness.DefaultFitness
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

(defn surface-empty? [b]
  (or (= b M) (nil? b))
  )

(defn count-vertical-surfaces-row [row]
  (- (count (partition-by surface-empty? row)) 3)
  )

(defn count-vertical-surfaces-reducer [count row]
            (+ count (count-vertical-surfaces-row row)))

(defn count-vertical-surfaces [field]
  (reduce count-vertical-surfaces-reducer 0 field)
  )

(defn horizontal-surface-boundary [a b] (if (not= (surface-empty? a) (surface-empty? b)) 1 0))

(defn count-horiz-surfaces-row [above below]
  (apply + (map horizontal-surface-boundary above below))
  )

(defn count-horiz-surfaces-reducer [memo row]
            (let [count (+ (first memo) (count-horiz-surfaces-row (second memo) row))
                  ]
              (list count row)
              )
            )

(defn count-horiz-surfaces [field]
  (- (first (reduce count-horiz-surfaces-reducer (list 0 empty-row) field))
     (+ Field/WIDTH Field/BUFFER Field/BUFFER))
  )

(defn count-surfaces [field]
  (let [vertical-surfaces (count-vertical-surfaces (drop-last Field/BUFFER field))
        horizontal-surfaces (count-horiz-surfaces field)]
    (* 25 (Math/pow (+ vertical-surfaces horizontal-surfaces) 2))
    )
  )

(defn user-block? [b]
  (not (or (nil? b) (= X b) (= M b))))

(defn find-max-height [height rows]
  (cond
    (empty? rows) 0
    (some user-block? (first rows)) height
    :else (find-max-height (dec height) (rest rows))
    )
  )

(defn max-height [field]
  (let [height (find-max-height (+ Field/HEIGHT Field/BUFFER) field)
        ]
    (* height height height)
    )
  )

(defn score-block-array [field-array]
  (let [f (map #(list* %) field-array)
        ]
    (double (+
              (count-field f)
              (count-impossibles f)
              (count-surfaces f)
              (max-height f)
              ))
    )
  )

(defn -score [this field] 
  (score-block-array (.getField field)))

(defn -paintImpossibles [this field] 
  (.setField field (to-field-array (paint-impossibles (.getField field))))
  field)

; This fitness doesn't use unlikelies
(defn -paintUnlikelies [this arg]
  )

; This fitness doesn't use unlikelies
(defn -unpaintUnlikelies [this arg]
  )
