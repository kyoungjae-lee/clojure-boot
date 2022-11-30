(ns aoc2018_5
  (:require [clojure.string :as str])
  )
;; 파트 1
;; 입력: dabAcCaCBAcCcaDA

;; 같은 종류의 소문자와 대문자는 서로 ‘반응‘하여 사라짐. aABb -> ‘’
;; 사라진 자리는 진공이 되기 때문에 다른 문자들이 붙게 되고, 또 그 문자들끼리 반응할 수 있음.  abBA-> aA -> ‘’
;; 바로 옆에 붙어있어야만 서로 반응함. abAB -> abAB (반응 없음)
;; 대문자-대문자, 소문자-소문자는 서로 반응하지 않음. aabAAB-> aabAAB (반응 없음)
;; 예시 dabAcCaCBAcCcaDA => dabCBAcaDA

;; 주어진 input 에서 최종으로 남는 문자열을 리턴하시오.

(def my-stack [1 2 3])
(peek my-stack)
;=> 3
(pop my-stack)
;=> [1 2]
(conj my-stack 4)
;=> [1 2 3 4]
(+ (peek my-stack) (peek (pop my-stack)))
;=> 5
(some #(Character/isUpperCase %) (str \A))
(some #(Character/isLowerCase %) "a")

(def input-str "dabAcCaCBAcCcaDA")

; vec 한글자씩 넣고
; 마지막 글자와 새로 넣을 글자와 비교
; 반응조건이면 기존 vec 마지막거 제거
; 반응조건이 아니면 vec 마지막에 추가

(defn letter-upper-checker [letter]
  (some #(Character/isUpperCase %) (str letter)))

(defn letter-lower-checker [letter]
  (some #(Character/isLowerCase %) (str letter)))

(= (clojure.string/upper-case \a) (clojure.string/upper-case \B))

(defn reaction-letter-checker [letter1 letter2]
  (when (and (= (clojure.string/upper-case letter1) (clojure.string/upper-case letter2))
             (or
               (and (letter-lower-checker letter1) (letter-upper-checker letter2))
               (and (letter-upper-checker letter1) (letter-lower-checker letter2))
               ))
    true
    ))

(defn compare-letter [input-vector]
  (reduce
    (fn [result input-letter]
      (if (empty? result)
        (conj result input-letter)
        (let [last-letter (peek result)]
          (if (reaction-letter-checker last-letter input-letter)
            (pop result)
            (conj result input-letter)))))
    []
    input-vector)
  )


(comment (->> input-str
              (vec)
              (compare-letter)
              (apply str)
              ))

;; 파트 2
;; 주어진 문자열에서 한 유닛 (대문자와 소문자)을 전부 없앤 후 반응시켰을 때, 가장 짧은 문자열의 길이를 리턴하시오.
;; 예를 들어 dabAcCaCBAcCcaDA 에서 a/A를 없애고 모두 반응시키면 dbCBcD가 되고 길이는 6인데 비해,
;; 같은 문자열에서 c/C를 없애고 모두 반응시키면 daDA가 남고 길이가 4이므로 4가 가장 짧은 길이가 됨.

(defn distinct-letter [input-str]
  (->> input-str
       (#(str/lower-case %))
       (distinct)
       ))

;(comment (->> input-str
;              (#(str/replace % "d" ""))
;              (#(str/replace % "D" ""))
;              ))

(defn remove-letter [input-str target-letter]
  (let [lower-target-letter (str/lower-case target-letter)
        upper-target-letter (str/upper-case target-letter)]
    (->> input-str
         (#(str/replace % lower-target-letter ""))
         (#(str/replace % upper-target-letter ""))
         )
    ))

(comment (->> input-str
              (distinct-letter)
              (map #(remove-letter input-str %))
              (map compare-letter)
              (map #(apply str %))
              (sort-by count)
              (first)
              ))
