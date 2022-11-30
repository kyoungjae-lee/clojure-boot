(ns aoc2018-3)


;; 파트 1
;; 다음과 같은 입력이 주어짐.

;; #1 @ 1,3: 4x4
;; #2 @ 3,1: 4x4
;; #3 @ 5,5: 2x2

;; # 뒤에 오는 숫자는 ID, @ 뒤에 오는 숫자 쌍 (a, b)는 시작 좌표, : 뒤에 오는 (c x d)는 격자를 나타냄.
;; 입력의 정보대로 격자 공간을 채우면 아래와 같이 됨.

;;      ........
;;      ...2222.
;;      ...2222.
;;      .11XX22.
;;      .11XX22.
;;      .111133.
;;      .111133.
;;      ........

;; 여기서 XX는 ID 1, 2, 3의 영역이 두번 이상 겹치는 지역.
;; 겹치는 지역의 갯수를 출력하시오. (위의 예시에서는 4)

(filter #(Character/isDigit %) "#1 @ 1,3: 4x4")
;(map parse-long (map ))
(re-find #"\d+" "abc12345def")
(re-find #"\d+" "#1 @ 1,3: 4x4")

(def astr "This contains  blanks \n \t \r and other whitespace")
(->> astr
     (#(clojure.string/split % #"\s"))
     ;(remove clojure.string/blank?)
     ;(clojure.string/join " ")
     )
;(clojure.string/index-of (str remove-space) "#")
;(clojure.string/index-of (str remove-space) "@")
;(clojure.string/index-of (str remove-space) ":")

; #1 @ 1,3: 4x4
;[1 3] [1 4] [1 5] [1 6]
;[2 3] [2 4] [2 5] [2 6]
;[3 3] [3 4] [3 5] [3 6]
;[4 3] [4 4] [4 5] [4 6]
; #2 @ 3,1: 4x4
;[3 1] [3 2] [3 3] [3 4]
;[4 1] [4 2] [4 3] [4 4]
;[5 1] [5 2] [5 3] [5 4]
;[6 1] [6 2] [6 3] [6 4]
; #3 @ 5,5: 2x2
;[5 5] [5 6]
;[6 5] [6 6]

;(clojure.string/replace "#1 @ 1,3: 4x4" " " "" )
;(zipmap [:id :x :y :w :h] (map parse-long (clojure.string/split (clojure.string/replace input " " "") #"[^0-9]"))))
;(zipmap [:id :x :y :w :h] (map parse-long (map str (char-array (.replaceAll input "[^0-9]" ""))))))
;(filter #(not= "" %) (clojure.string/split (clojure.string/replace "#1@1,3:4x4" " " "") #"[^0-9]"))
;(zipmap [:id :x :y :w :h] (map parse-long (clojure.string/split (clojure.string/replace "#1@1,3:4x4" " " "") #"[^0-9]")))
(defn mapping-position [input]
  (->> input
       (re-matches #"#(\d)\s@\s(\d),(\d):\s(\d)x(\d)")
       (map parse-long)
       (apply (fn [_ id x y w h] (zipmap [:id :x :y :w :h] [id x y w h])))
       ))

(defn make-range [input-map]
  (let [{:keys [x y w h]} input-map]
    (for [x-range (range x (+ x w))
          y-range (range y (+ y h))]
      [x-range y-range]))
  )


(comment (->> "resources/aoc2018-3_1.sample.txt"
              (slurp)
              (clojure.string/split-lines)
              (map mapping-position)
              (mapcat make-range)
              (frequencies)
              (vals)
              (filter #(> % 1))
              (count)
              ))


;; 파트 2
;; 입력대로 모든 격자를 채우고 나면, 정확히 한 ID에 해당하는 영역이 다른 어떤 영역과도 겹치지 않음
;; 위의 예시에서는 ID 3 이 ID 1, 2와 겹치지 않음. 3을 출력.
;; 겹치지 않는 영역을 가진 ID를 출력하시오. (문제에서 답이 하나만 나옴을 보장함)


;({:id 1, :x 1, :y 3, :w 4, :h 4, :range #{[1 3] [1 4] [1 5]... }}
; {:id 2, :x 3, :y 1, :w 4, :h 4, :range #{[3 1] [3 2] [3 3]... }}
; {:id 3, :x 5, :y 5, :w 2, :h 2, :range #{[5 5] [5 6] [6 5] [6 6]}})






;(defn find-intersection [input-map source-id source-range-set]
;  (for [target input-map
;        :let [target-id (get target :id)
;              target-range-set (get target :range)
;              intersection-count (count (clojure.set/intersection source-range-set target-range-set))]
;        :when (not= source-id target-id)]
;    intersection-count))
;
;(defn index-by-intersection-list [input-map]
;  (for [source input-map
;        :let [source-id (get source :id)
;              source-range-set (get source :range)]]
;    [source-id (find-intersection input-map source-id source-range-set)]
;    )
;  )


;(= ((0 0) (0 4) (4 0)) )
;(count (clojure.set/intersection #{[6 6] [6 5] [5 6] [5 5]} #{[4 3] [3 3] [5 4] [6 3] [3 4] [4 2] [5 3] [4 1] [5 2] [6 4] [5 1] [6 1] [3 1] [4 4] [6 2] [3 2]}))
;(doseq [[k v] {1 (4 0) 2 (4 0) 3 (0 0)}]
;  (prn k (reduce + v)))
;(defn intersection-sum [map]
;  (for [idx (keys map)]
;    (if (= (reduce + (get map idx)) 0)
;      idx)
;    ))
(defn append-range [m]
  (assoc m :range (set (make-range m))))

(defn filtering-duplicated [m]
  (let [[key value] m]
    (when (> value 1) key))
  )

(defn find-not-intersection [{:keys [id range]}]
  (when (= (count (clojure.set/intersection range freq-set)) 0)
    id)
  )

(def freq-set (->> "resources/aoc2018-3_1.sample.txt"
                   (slurp)
                   (clojure.string/split-lines)
                   (map mapping-position)
                   (mapcat make-range)
                   (frequencies)
                   (keep filtering-duplicated)
                   (into #{})
                   ))
freq-set

(comment (->> "resources/aoc2018-3_1.sample.txt"
              (slurp)
              (clojure.string/split-lines)
              (map mapping-position)
              (map append-range)
              (keep find-not-intersection)
              ;(apply str)
              )
         )
