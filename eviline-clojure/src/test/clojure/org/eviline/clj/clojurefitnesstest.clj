(ns org.eviline.clj.clojurefitnesstest)
(clojure.core/use 'clojure.core)
(use 'clojure.test)
(use 'clojure.test.junit)

(import '(org.eviline Field))
(import '(org.eviline.clj ClojureFitness))

(with-junit-output
  (run-tests 'org.eviline.clj.clojurefitnesstest))

(deftest scoring 
  (is (let [fitness (new ClojureFitness)]
        (= 0.0 (.score fitness (.prepareField fitness (new Field))))
        ))
  )

(time (.score (new ClojureFitness) (new Field)))
(time (.score (new ClojureFitness) (new Field)))
(time (.score (new ClojureFitness) (new Field)))
(time (.score (new ClojureFitness) (new Field)))
(time (.score (new ClojureFitness) (new Field)))
(time (.score (new ClojureFitness) (new Field)))
(time (.score (new ClojureFitness) (new Field)))
(time (.score (new ClojureFitness) (new Field)))
(time (.score (new ClojureFitness) (new Field)))
(time (.score (new ClojureFitness) (new Field)))
(time (.score (new ClojureFitness) (new Field)))
(time (.score (new ClojureFitness) (new Field)))
