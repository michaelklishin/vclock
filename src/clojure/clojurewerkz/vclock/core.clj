(ns ^{:doc "An implementation of vector clocks, heavily inspired by Basho's Riak Core."}
  clojurewerkz.vclock.core
  (:refer-clojure :exclude [find merge]))


;;
;; Implementation
;;

(defn- timestamp-in-seconds
  []
  (Math/round (double (/ (.getTime (java.util.Date.)) 1000))))


;;
;; API
;;

(defrecord VClockEntry [^long counter ^long timestamp])

(defn fresh
  "Returns a fresh VClock"
  [& kv]
  (apply sorted-map kv))

(defn increment
  "Increments vlock by adding a new entry"
  ([m node]
     (increment m node (timestamp-in-seconds)))
  ([m node ^long tstamp]
     (if-let [entry (get m node)]
       (assoc m node (VClockEntry. (inc (.counter entry)) tstamp))
       (assoc m node (VClockEntry. 1 tstamp)))))

(defn descends?
  "Returns true if +one+ is a direct descendant of +other+, false otherwise.

   A vlock is its own descendant. Any vlock is a descendant of an empty vclock."
  [one other]
  (if (empty? other)
    true
    (reduce (fn [prev [k v2]]
              (and prev
                   (if-let [v1 (get one k)]
                     (>= (.counter v1) (.counter v2))
                     false)))
            true other)))

(defn- merge-helper
  [^VClockEntry a ^VClockEntry b]
  (if (> (.counter a) (.counter b)) a b))

(defn merge
  "Merge multiple VClocks into one"
  [& vclocks]
  (apply merge-with (cons merge-helper vclocks)))
