(ns aoc2018-2)

;; 파트 1
;; 주어진 각각의 문자열에서, 같은 문자가 두번 혹은 세번씩 나타난다면 각각을 한번씩 센다.
;; 두번 나타난 문자가 있는 문자열의 수 * 세번 나타난 문자가 있는 문자열의 수를 반환하시오.
;; 예)
;; abcdef 어떤 문자도 두번 혹은 세번 나타나지 않음 -> (두번 나오는 문자열 수: 0, 세번 나오는 문자열 수: 0)
;; bababc 2개의 a, 3개의 b -> (두번 나오는 문자열 수: 1, 세번 나오는 문자열 수: 1)
;; abbcde 2개의 b -> (두번 나오는 문자열 수: 2, 세번 나오는 문자열 수: 1)
;; abcccd 3개의 c -> (두번 나오는 문자열 수: 2, 세번 나오는 문자열 수: 2)
;; aabcdd 2개의 a, 2개의 d 이지만, 한 문자열에서 같은 갯수는 한번만 카운트함 -> (두번 나오는 문자열 수: 3, 세번 나오는 문자열 수: 2)
;; abcdee 2개의 e -> (두번 나오는 문자열 수: 4, 세번 나오는 문자열 수: 2)
;; ababab 3개의 a, 3개의 b 지만 한 문자열에서 같은 갯수는 한번만 카운트함 -> (두번 나오는 문자열 수: 4, 세번 나오는 문자열 수: 3)
;; 답 : 4 * 3 = 12


;(vals (frequencies (seq (char-array "bababc"))))
(defn num-contains? [n coll]
  (if (some #(= % n) coll)
    1
    0))

(def contains-two? (partial num-contains? 2))
(def contains-three? (partial num-contains? 3))

(num-contains? 3 '(2 1 4 5 6 3))

(def multiply (->> "resources/aoc2018-2_1.sample.txt"
                   (slurp)
                   (clojure.string/split-lines)
                   (map frequencies)                        ; ({\a 1, \b 1, \c 1, \d 1, \e 1, \f 1} {\b 3, \a 2, \c 1}....)
                   (map vals)                               ; ((1 1 1 1 1 1) (3 2 1) ...)
                   (map (fn [freq-vals]
                          ((juxt contains-two? contains-three?) freq-vals)))
                   (apply map +)
                   (apply *)
                   ))

multiply

;; 파트 2
;; 여러개의 문자열 중, 같은 위치에 정확히 하나의 문자가 다른 문자열 쌍에서 같은 부분만을 리턴하시오.
;; 예)
;; abcde
;; fghij
;; klmno
;; pqrst
;; fguij
;; axcye
;; wvxyz

;; 주어진 예시에서 fguij와 fghij는 같은 위치 (2번째 인덱스)에 정확히 한 문자 (u와 h)가 다름. 따라서 같은 부분인 fgij를 리턴하면 됨.

(compare "abc" "abd")
(compare "aaf" "ahf")
(compare "abc" "abc")
(compare ["a" "a" "c"] ["a" "h" "c"])                       ; -7 (a -> h) 7 step
(= (map-indexed #(when (= 2 %2) [%1 "Hi"]) [1 1 2 2])
   '(nil nil [2 "Hi"] [3 "Hi"]))                            ;;=>true

(map-indexed vector ["a" "h" "c"])
(use 'clojure.data)
;(def set-a ["a" "b" "c" "d"])
;(def set-b ["a" "h" "c" "d"])
(def set-a "fghij")
(def set-b "fguij")
(diff set-a set-b)

(def input-string (->> "resources/aoc2018-2_2.sample.txt"
                       (slurp)
                       (clojure.string/split-lines)
                       ))

;; for -> when
input-string
(def combine-strings (for [str-list1 input-string
                           str-list2 input-string
                           :when (not= str-list1 str-list2)]
                       [str-list1 str-list2]))

combine-strings

(defn remove-keep-nil [target]
  (keep #(if-not (nil? %) %) target))

;; abcde fghij
;; => [[a f] [b g] [c h] [d i] [e j]
(defn compare-two-strings [strings]
  (for [string-vector strings]
    (let [[source target] string-vector
          has-nil-one-map (frequencies
                            (last (diff (char-array source) (char-array target))))]
        (if (= (get has-nil-one-map 'nil) 1)
          ;(remove-keep-nil (keys has-nil-one-map))
          (keys has-nil-one-map)
          ))))

(defn compare-two-strings2 [sting-vector]
  (for [[s1 s2] sting-vector]
    (->> (map vector s1 s2)
         (keep (fn [[c1 c2]] (when (= c1 c2) c1)))
         (apply str))
    )
  )

(comment
  (->> combine-strings
       (compare-two-strings2)
       (filter #(not= "" %))
       (filter #(= (- (count (first (first combine-strings))) 1) (count %)))
       (first)
       ))

;; (str \a nil \b) => ab
;;
;(str \f \g nil \i \j)
;(def key-set (keys {\f 1, \g 1, nil 1, \i 1, \j 1}))        ; (\f \g nil \i \j)
;(keep #(if-not (nil? %) %) key-set)                         ;(\f \g \i \j)
;(comment (apply str (first (remove-keep-nil (compare-two-strings combine-strings)))))

;(apply str (first (remove-keep-nil (compare-two-strings combine-strings))))

;; #################################
;; ###        Refactoring        ###
;; #################################
