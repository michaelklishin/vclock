(ns clojurewerkz.vclock.core-test
  (:require [clojurewerkz.vclock.core :as vclock])
  (:use clojure.test)
  (:import clojurewerkz.vclock.core.VClockEntry))


(deftest test-descends
  (testing "all vlocks descend from the empty vclock"
    (let [vc (vclock/fresh)]
      (is (vclock/descends? vc (vclock/fresh))))))

(deftest test-example1
  (let [a0 (vclock/fresh)
        b0 (vclock/fresh)
        a1 (vclock/increment a0 :a)
        b1 (vclock/increment b0 :b)
        a2 (vclock/increment a1 :a)
        b2 (vclock/increment b1 :b)
        c0 (vclock/merge a2 b1)
        c1 (vclock/increment c0 :c)]
    (is (empty? a0))
    (is (empty? b0))
    (is (vclock/descends? a1 a0))
    (is (vclock/descends? b1 b0))
    (is (not (vclock/descends? a1 b1)))
    (is (vclock/descends? a2 a1))
    (is (vclock/descends? b2 b1))
    (is (not (vclock/descends? a2 b2)))
    (is (vclock/descends? c1 a2))
    (is (vclock/descends? c1 b1))
    (is (not (vclock/descends? b1 c1)))
    (is (not (vclock/descends? b1 a1)))))


(deftest test-merging
  (testing "less on the left case"
    (let [a (vclock/fresh 5 (VClockEntry. 5 5))
          b (vclock/fresh 6 (VClockEntry. 6 6)
                          7 (VClockEntry. 7 7))
          c (vclock/merge a b)]
      (is (= c (vclock/fresh 5 (VClockEntry. 5 5)
                             6 (VClockEntry. 6 6)
                             7 (VClockEntry. 7 7))))))
  (testing "less on the right case"
    (let [b (vclock/fresh 5 (VClockEntry. 5 5))
          a (vclock/fresh 6 (VClockEntry. 6 6)
                          7 (VClockEntry. 7 7))
          c (vclock/merge a b)]
      (is (= c (vclock/fresh 5 (VClockEntry. 5 5)
                             6 (VClockEntry. 6 6)
                             7 (VClockEntry. 7 7))))))
  (testing "equal ids"
    (let [a (vclock/fresh :a (VClockEntry. 5 5))
          b (vclock/fresh :a (VClockEntry. 6 6)
                          :b (VClockEntry. 7 7))
          c (vclock/merge a b)]
      (is (= c (vclock/fresh :a (VClockEntry. 6 6)
                             :b (VClockEntry. 7 7)))))))


(deftest test-pruning
  (testing "vclocks greater than big-vclock are pruned"
    (let [ts       (- (vclock/timestamp-in-seconds) 500)
          a        (VClockEntry. 8 (+ 5 ts))
          b        (VClockEntry. 6 (+ 10 ts))
          c        (VClockEntry. 2 ts)
          d        (VClockEntry. 3 (+ ts 100))
          vc       (sorted-map :a a :b b :c c :d d)
          expected (sorted-map :d d :b b)]
      (is (= expected (vclock/prune vc :small-vclock 1 :big-vclock 2)))))
  (testing "vclocks lesser than small-vclock are not pruned"
    (let [ts       (- (vclock/timestamp-in-seconds) 500)
          a        (VClockEntry. 8 (+ 5 ts))
          b        (VClockEntry. 6 (+ 10 ts))
          c        (VClockEntry. 2 ts)
          d        (VClockEntry. 3 (+ ts 100))
          vc       (sorted-map :a a :b b :c c :d d)]
      (is (= vc (vclock/prune vc :small-vclock 10 :big-vclock 20)))))
  (testing "vclocks of size between small-vclock and big-vclock are pruned based on their age"
    (let [ts       (- (vclock/timestamp-in-seconds) 200)
          a        (vclock/entry 8 (+ 5 ts))
          b        (vclock/entry 6 (+ 10 ts))
          c        (vclock/entry 2 ts)
          d        (vclock/entry 3 (+ ts 100))
          vc       (sorted-map :a a :b b :c c :d d)]
      (is (= vc (vclock/prune vc :small-vclock 1 :big-vclock 10 :old-vclock 99))))))
