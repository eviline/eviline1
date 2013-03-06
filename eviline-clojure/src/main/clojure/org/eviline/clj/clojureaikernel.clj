(ns org.eviline.clj.clojureaikernel
  (:gen-class 
    :name org.eviline.clj.ClojureAIKernel
    :extends org.eviline.ai.DefaultAIKernel
    :main false
    :init init
    :constructors {[org.eviline.fitness.Fitness] []}
    :post-init post-init
    :exposes-methods {setFitness superSetFitness}
    ))
(clojure.core/use 'clojure.core)
(import '(org.eviline.ai DefaultAIKernel Context Decision DecisionModifier QueueContext))

(defn -init [fitness] [[]])

(defn -post-init [this fitness] (.superSetFitness this fitness))

(defn -setFitness [this fitness] (throw (UnsupportedOperationException.)))
