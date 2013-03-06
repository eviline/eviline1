(ns org.eviline.clj.clojurefitnesstest)
(clojure.core/use 'clojure.core)
(use 'clojure.test)
(use 'clojure.test.junit)

(import '(org.eviline Field))
(import '(org.eviline.clj ClojureFitness))

(with-junit-output
  (run-tests 'org.eviline.clj.clojurefitnesstest))

(deftest scoring 
  (is (= (.score (ClojureFitness/newFitness) (new Field)) 0.0))
  )


