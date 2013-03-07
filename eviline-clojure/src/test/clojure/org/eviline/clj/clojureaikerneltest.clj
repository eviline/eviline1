(ns org.eviline.clj.clojureaikerneltest)
(clojure.core/use 'clojure.core)
(use 'clojure.test)
(use 'clojure.test.junit)

(use 'org.eviline.clj.clojureaikernel)

(import '(org.eviline Field Shape ShapeType))
(import '(org.eviline.clj ClojureAIKernel ClojureFitness))
(import '(org.eviline.ai Context))

(with-junit-output
  (run-tests 'org.eviline.clj.clojureaikerneltest))

(deftest instantiate-test
  (is (ClojureAIKernel. (ClojureFitness.)))
  )

(deftest grounded-locations-test
  (is (= 9 (count (grounded-locations (Field.) ShapeType/O))))
  (is (= 17 (count (grounded-locations (Field.) ShapeType/S))))
  )

(deftest bestfor-test
  (is (let [ai (ClojureAIKernel. (ClojureFitness.))
            context (Context. ai nil (Field.) 1)
            decision (.bestFor ai context ShapeType/O)
            ]
        (println decision)
        decision))
  )