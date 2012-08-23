(ns ^{:doc "An implementation of vector clocks, heavily inspired by Basho's Riak Core."}
  clojurewerkz.vclock.core
  (:refer-clojure :exclude [find merge])
  (:use [clojurewerkz.support.core :only [map->pairs pairs->map]]))


;;
;; Implementation
;;

(defn timestamp-in-seconds
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

(defn entry
  "Instantiates a new vclock entry"
  [^long counter ^long timestamp]
  (VClockEntry. counter timestamp))

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


(defn prune
  "Prunes (possibly minimizes) vclocks and returns a new version. Used to limit vector clock growth.

   See http://wiki.basho.com/Vector-Clocks.html for more information about vector clocks.

   Accepted options:

   :small-vclock (default: 10): vclock with the number of entries less than or equal to this value are not pruned
   :big-vclock (default: 20): vclocks with more entries than this value will be pruned down to this many latest entries
   :young-vclock (default: 20 seconds)
   :old-vclock (default: 24 hours): vlock entries older than this value (in seconds) will be pruned"
  [vclock & {:keys [small-vclock big-vclock young-vclock old-vclock] :or {small-vclock 10
                                                                          big-vclock 20
                                                                          ;; 20 seconds
                                                                          young-vclock 20
                                                                          ;; 24 hours
                                                                          old_vclock (* 24 60 60)
                                                                          } :as options}]
  (let [ps (map->pairs vclock)
        xs (sort-by (fn [[k ^VClockEntry v]] (.timestamp v)) ps)
        ys (cond
             (> (count xs) big-vclock)    (take-last big-vclock xs)
             (<= (count xs) small-vclock) xs             
             :else                        (let [now (timestamp-in-seconds)]
                                            (filter (fn [[k ^VClockEntry v]]
                                                      (< old-vclock (- now (.timestamp v))))
                                                    xs)))]
    (pairs->map ys)))
