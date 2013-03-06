(ns org.eviline.ai.clojureaikernel
  (:gen-class 
    :name org.eviline.ai.ClojureAIKernel
    :methods [#^{:static true} [ newAIKernel [org.eviline.fitness.Fitness] org.eviline.ai.AIKernel]]))
(clojure.core/use 'clojure.core)
(import '(org.eviline.ai AIKernel Context Decision DecisionModifier QueueContext))

(defn -newAIKernel [fitness]
  (proxy [AIKernel] []
    (getFitness [] fitness)
    )
  )