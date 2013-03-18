(ns org.eviline.clj.ClojureAIKernelTest)
(clojure.core/use 'clojure.core)
(use 'clojure.test)
(use 'clojure.test.junit)

(use 'org.eviline.clj.ClojureAIKernel)

(import '(org.eviline Field Shape ShapeType))
(import '(org.eviline.clj ClojureAIKernel ClojureFitness))
(import '(org.eviline.ai Context))

(with-junit-output
  (run-tests 'org.eviline.clj.ClojureAIKernelTest))

(deftest instantiate-test
  (is (ClojureAIKernel.))
  )

(deftest grounded-locations-test
  (is (= 9 (count (grounded-locations (Field.) ShapeType/O))))
  (is (= 17 (count (grounded-locations (Field.) ShapeType/S))))
  )

(deftest bestfor-test
  (is (let [ai (ClojureAIKernel.)
            context (Context. ai nil (Field.) 1)
            decision (.bestFor ai context ShapeType/O)
            ]
        (println decision)
        decision))
  )

(deftest pextend-path-fully-test
  (is (let [field (Field.)
            path (extend-path field nil nil Shape/O_UP 10 0)
            all-paths (extend-path-fully field path)
            ]
        (println "Found " (count all-paths) " paths.")
        (println "sample path:" (first all-paths))
        (println "parl:" (path-to-player-action-reverse-list field (first all-paths)))
        all-paths)))
  
  