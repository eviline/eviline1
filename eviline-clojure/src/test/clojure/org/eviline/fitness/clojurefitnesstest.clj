(ns org.eviline.fitness.clojurefitnesstest)
(clojure.core/use 'clojure.core)
(use 'clojure.test)
(use 'clojure.test.junit)

(import '(org.eviline Field))
(import '(org.eviline.fitness ClojureFitness))

(with-junit-output
  (run-tests 'org.eviline.fitness.clojurefitnesstest))

(deftest scoring 
  (is (= (.score (ClojureFitness/newFitness) (new Field)) 0.0))
  )


