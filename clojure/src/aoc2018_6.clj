(ns aoc2018_6
  (:require [load-file :as lf])
  (:require [clojure.string :as str])
  )

;; 파트 1
;; 입력 : 좌표의 쌍이 N개 주어짐

;; 1, 1
;; 1, 6
;; 8, 3
;; 3, 4
;; 5, 5
;; 8, 9

;; 각 점은 1 tick이 지날때 마다 상,하,좌,우로 증식함.


;;  .......... 0
;;  .A........ 1
;;  .......... 2
;;  ........C. 3
;;  ...D...... 4
;;  .....E.... 5
;;  .B........ 6
;;  .......... 7
;;  .......... 8
;;  ........F. 9

;;  .a........ 0
;;  aAa....... 1
;;  .a......c. 2
;;  ...d...cCc 3
;;  ..dDde..c. 4
;;  .b.deEe... 5
;;  bBb..e.... 6
;;  .b........ 7
;;  ........f. 8
;;  .......fFf 9

;;  aaa....... 0
;;  aAaa....c. 1
;;  aaad...ccc 2
;;  .adddeccCc 3
;;  .xdDdeeccc 4
;;  bbxdeEeec. 5
;;  bBbxeee... 6
;;  bbb..e..f. 7
;;  .b.....fff 8
;;  ......ffFf 9

;;  aaaa....c. 0
;;  aAaaa..ccc 1
;;  aaaddecccc 2
;;  aadddeccCc 3
;;  xxdDdeeccc 4
;;  bbxdeEeecc 5
;;  bBbxeeeex. 6
;;  bbbxeeefff 7
;;  bbb..effff 8
;;  .b...fffFf 9

;;  aaaaa..ccc 0
;;  aAaaaxcccc 1
;;  aaaddecccc 2
;;  aadddeccCc 3
;;  xxdDdeeccc 4
;;  bbxdeEeecc 5
;;  bBbxeeeexx 6
;;  bbbxeeefff 7
;;  bbb.eeffff 8
;;  bbb.ffffFf 9

;;  aaaaaacccc 0
;;  aAaaa.cccc 1
;;  aaaddecccc 2
;;  aadddeccCc 3
;;  ..dDdeeccc 4
;;  bb.deEeecc 5
;;  bBb.eeee.. 6
;;  bbb.eeefff 7
;;  bbb.eeffff 8
;;  bbb.ffffFf 9

;;  aaaaa.cccc 0
;;  aAaaa.cccc 1
;;  aaaddecccc 2
;;  aadddeccCc 3
;;  ..dDdeeccc 4
;;  bb.deEeecc 5
;;  bBb.eeee.. 6
;;  bbb.eeefff 7
;;  bbb.eeffff 8
;;  bbb.ffffFf 9


;; 여기서 . 으로 표기된 부분은 각 출발 지점으로부터 '같은 거리'에 있는 부분을 뜻함.
;; 맵 크기에는 제한이 없어 무한으로 뻗어나간다고 할 때, 가장 큰 유한한 면적의 크기를 반환 (part-1)

(def read-file (lf/load-read-file "resources/aoc2018-6_min.sample.txt"))
;(def read-file (lf/load-read-file "resources/aoc2018-6.sample.txt"))

;(keep-indexed (fn [idx v] [idx v]) '([1 1] [1 6] [8 3] [3 4] [5 5] [8 9]))

(def find-max-coordinate
  (->> read-file
       (map #(str/replace % #" " ""))
       (map #(str/split % #","))
       (mapcat (fn [[x y]] [(parse-long x) (parse-long y)]))
       (apply max)
       )
  )

find-max-coordinate

(defn init-coordinate [length]
  (for [x-coord (range 0 (+ length 1))
        y-coord (range 0 (+ length 1))]
    [[x-coord y-coord] "."]
    ))

(defn draw-coordinate [[[_ y] str]]
  (if (= find-max-coordinate y)
    (println str)
    (print str)
    )
  )
(init-coordinate find-max-coordinate)
;(map draw-coordinate '([[9 6] "."]
;                       [[9 7] "."]
;                       [[9 8] "."]
;                       [[9 9] "."]))

(comment (->> (init-coordinate find-max-coordinate)
              (map draw-coordinate)
              ))


(comment (->> read-file
              (map #(str/replace % #" " ""))
              (map #(str/split % #","))
              (map (fn [[x y]] [(parse-long x) (parse-long y)]))
              (reduce (fn [[x-list y-list] [x y]] [(conj x-list x) (conj y-list y)]) [])
              (map #(apply max %))
              ))


;; 파트 2
;; 안전(safe) 한 지역은 근원지'들'로부터의 맨하탄거리(Manhattan distance, 격자를 상하좌우로만 움직일때의 최단 거리)의 '합'이 N 미만인 지역임.

;;  .......... 0
;;  .A........ 1
;;  .......... 2
;;  ...###..C. 3
;;  ..#D###... 4
;;  ..###E#... 5
;;  .B.###.... 6
;;  .......... 7
;;  .......... 8
;;  ........F. 9

;; Distance to coordinate A: abs(4-1) + abs(3-1) =  5
;; Distance to coordinate B: abs(4-1) + abs(3-6) =  6
;; Distance to coordinate C: abs(4-8) + abs(3-3) =  4
;; Distance to coordinate D: abs(4-3) + abs(3-4) =  2
;; Distance to coordinate E: abs(4-5) + abs(3-5) =  3
;; Distance to coordinate F: abs(4-8) + abs(3-9) = 10
;; Total distance: 5 + 6 + 4 + 2 + 3 + 10 = 30

;; N이 10000 미만인 안전한 지역의 사이즈를 구하시오.
