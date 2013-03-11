(ns org.eviline.clj.lazymap
  (:gen-class 
    :name org.eviline.clj.LazyMap
    :extends java.util.AbstractMap
    :main false
    :init init
    :constructors {[java.util.Map] []}
    :state state
    :methods [
              [lazyPut [Object Object] void]
              ]
    :exposes-methods {get superGet}
    ))
(clojure.core/use 'clojure.core)

(defn -init [backing] [[] [backing (atom {})]])

(defn get-backing [this] (first (.state this)))
(defn get-lazy-store [this] (second (.state this)))

(defn -lazyPut [this key delayed-val]
  (swap! (get-lazy-store this) assoc key delayed-val)
  )

(defn eager-get [this key to-eval]
  (swap! (get-lazy-store this) dissoc key)
  (.put (get-backing this) key (force to-eval))
  (.get (get-backing this) key)
  )

(defn -entrySet [this] 
  (.entrySet (get-backing this)))

(defn -get [this key]
  (let [to-eval (get @(get-lazy-store this) key)
        ]
    (if-not (nil? to-eval)
      (eager-get this key to-eval))
    (.get (get-backing this) key)
    )
  )

(defn -containsKey [this key] 
  (if (or (.containsKey (get-backing this) key) (get @(get-lazy-store this) key)) true false))

