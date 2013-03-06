(ns org.eviline.clj.clojureaikerneltest)
(clojure.core/use 'clojure.core)
(use 'clojure.test)
(use 'clojure.test.junit)

(import '(org.eviline.clj ClojureAIKernel ClojureFitness))

(with-junit-output
  (run-tests 'org.eviline.clj.clojureaikerneltest))

(deftest instantiate
  (is (ClojureAIKernel. (ClojureFitness.)))
  )
