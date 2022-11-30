(ns aoc2018-1)

;; 파트 1
;; 주어진 입력의 모든 숫자를 더하시오.
;; 예) +10 -2 -5 +1 이 입력일 경우 4를 출력

;; 파트 2
;; 주어진 입력의 숫자를 더할 때 마다 나오는 숫자 중, 처음으로 두번 나오는 숫자를 리턴하시오.
;; 예) +3, +3, +4, -2, -4 는 10이 처음으로 두번 나오는 숫자임.
;; 0 -> 3 (+3) -> 6 (+3) -> 10(+4) -> 8(-2) -> 4(-4) -> 7(+3) -> 10(+3) -> ...
;(map parse-long str-lines)

(def numbers (->> "resources/aoc2018-1.sample.txt"
                  (slurp)
                  (clojure.string/split-lines)
                  (map parse-long)))

;(defn find-num [numbers]
;  (loop [nums numbers
;         sum 0
;         results #{}]
;    (let [nums (if (empty? nums) numbers nums)
;          sum (+ sum (first nums))]
;      (if (results sum)
;        sum
;        (recur
;          (rest nums)
;          sum
;          (conj results sum))))))
;
;(find-num numbers)

;; Refectoring
;(defn find-second-number [numbers]
;  (prn numbers)
;  (reduce
;    (fn [{sum     :sum
;          results :results} number]
;      (let [sum (+ sum number)]
;        (prn "sum : " sum "result : " results)
;        (if (results sum)
;          (reduced sum)
;          {:sum     sum
;           :results (conj results sum)})))
;    {:sum 0 :results #{}}
;    (cycle numbers)))
;
;(find-second-number numbers)

; func
;기존 하나로 된 함수를 아래 두개의 함수로 바꾸기
;1. reduce를 이용해서 같은 값이 반복될 때, 같은 값을 리턴하는 함수
;2. 값을 더하는 함수

;(defn add-number [numbers]
;  (reduce (fn [{sum     :sum
;                results :results}
;               input]
;            {:sum     (+ sum input)
;             :results (find-same-number results (+ sum input))})
;          {:sum 0 :results #{}} numbers))

;(add-number numbers)

; reductions
(defn find-same-number [numbers]
  (reduce
    (fn [results input]
      (prn results input)
      (if (results input)
        (reduced input)
        (conj results input)))
    #{0}
    numbers))

(def numbers (->> "resources/aoc2018-1.sample.txt"
                  (slurp)
                  (clojure.string/split-lines)
                  (map parse-long)
                  (reductions +)
                  (find-same-number)))

numbers
