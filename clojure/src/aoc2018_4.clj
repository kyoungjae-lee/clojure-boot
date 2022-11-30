(ns aoc2018_4
  (:require [java-time.api :as jt])
  (:require [clojure.string :as str])
  (:require [load-file :as lf])
  )


;; 파트 1
;; 입력:

;; [1518-11-01 00:00] Guard #10 begins shift
;; [1518-11-01 00:05] falls asleep
;; [1518-11-01 00:25] wakes up
;; [1518-11-01 00:30] falls asleep
;; [1518-11-01 00:55] wakes up
;; [1518-11-01 23:58] Guard #99 begins shift
;; [1518-11-02 00:40] falls asleep
;; [1518-11-02 00:50] wakes up
;; [1518-11-03 00:05] Guard #10 begins shift
;; [1518-11-03 00:24] falls asleep
;; [1518-11-03 00:29] wakes up
;; [1518-11-04 00:02] Guard #99 begins shift
;; [1518-11-04 00:36] falls asleep
;; [1518-11-04 00:46] wakes up
;; [1518-11-05 00:03] Guard #99 begins shift
;; [1518-11-05 00:45] falls asleep
;; [1518-11-05 00:55] wakes up

;; 키워드: 가드(Guard) 번호 자는 시간(falls asleep) 일어나는 시간(wakes up).
;; 각 가드들은 교대 근무를 시작하고 (begins shift) 졸았다가 일어났다를 반복함.
;; 위의 예시에서 10번 가드는 0시 5분에 잤다가 25분에 일어나고 또 0시 30분에 잠들었다가 0시 55분에 깨어남.
;; 가드들에 대해서 자고 깨는 시간 정보들이 입력으로 주어짐.

;; 파트 1은 “주어진 입력에 대해서 가장 오랜시간 잠들어있었던 가드의 ID와 그 가드가 가장 빈번하게 잠들어 있었던 분(minute)의 곱을 구하라”
;; 만약 20번 가드가 0시 10분~36분 다음날 0시 5분~11분 다다음날 0시 11분~13분 이렇게 잠들어 있었다면 “11분“이 가장 빈번하게 잠들어 있던 ‘분’. 그럼 답은 20 * 11 = 220.

(re-matches #"#(\d)\s@\s(\d)(\d):\s(\d)x(\d)" "#3 @ 55: 2x2")
(re-matches #"((19|20)\d\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])" "2022-12-31")
(re-matches #"\[(.*)\]\s(.*)" "[2022-12-31 00:00] ㅇㄴㄹㄴㅇㄹ")
(re-matches #"[a-zA-Z]{5}\s#(\d+)\s[a-zA-Z]{6}\s[a-zA-Z]{5}" "Guard #10 begins shift")
(re-matches #"([01]?[0-9]|2[0-3]):([0-5][0-9])" "23:58")
(re-matches #"\[(\d+)-(\d+)-(\d+) (\d+):(\d+)\]\s(.*)" "[1518-11-01 00:00] Guard #10 begins shift")

(def read-file (lf/load-read-file "resources/aoc2018-4.sample.txt"))

(defn parse-action [state-action]
  (reduce (fn [result action]
            (let [[_ _ _ _ _ minutes action-str] (re-matches #"\[(\d+)-(\d+)-(\d+) (\d+):(\d+)\]\s(.*)" action)
                  [_ guard-id] (re-matches #"Guard #(\d+) begins shift" action-str)]
              (cond
                (not= guard-id nil) (assoc result :guard-id (parse-long guard-id))
                (= action-str "falls asleep") (assoc result :sleep-time minutes)
                (= action-str "wakes up")
                (let [{:keys [guard-id sleep-time histories]} result]
                  (assoc result :histories (merge-with into {guard-id [[(parse-long sleep-time) (parse-long minutes)]]} histories))
                  )
                :else result)
              )
            )
          {:guard-id nil :sleep-time nil :histories {}}
          state-action)
  )


(defn calculate-duration-time [m]
  (let [[key values] m]
    {key (map (fn [[str-min end-min]] (range str-min end-min)) values)}
    )
  )

(defn calculate-duration-sleep-time [m]
  (let [[key values] m]
    (reduce
      (fn [{:keys [guard-id slept-time]} [str-time end-time]]
        {:guard-id guard-id :slept-time (conj slept-time (- end-time str-time))}
        )
      {:guard-id key :slept-time []}
      values)))

(defn get-most-sleep-guard [m]
  {:guard-id (get m :guard-id) :slept-time (apply max (get m :slept-time))})


(def get-most-slept-guard (->> read-file
                               (parse-action)
                               (:histories)
                               (map calculate-duration-sleep-time)
                               (into [])
                               (map get-most-sleep-guard)
                               (sort-by :slept-time >)
                               (first)
                               ))

(def get-range-freq-minute (->> read-file
                                (parse-action)
                                (:histories)
                                (map calculate-duration-time)
                                (into {})
                                (#(get % (get get-most-slept-guard :guard-id)))
                                (flatten)
                                (frequencies)
                                (map (fn [[key value]] {:minute key :freq-count value}))
                                (sort-by :freq-count >)
                                (first)
                                ))

(comment (->>
           (* (get get-most-slept-guard :guard-id) (get get-range-freq-minute :minute))
           ))

;; 파트 2
;; 주어진 분(minute)에 가장 많이 잠들어 있던 가드의 ID과 그 분(minute)을 곱한 값을 구하라.

;(defn flatten-values [m]
;  (let [[key values] m]
;    [key (frequencies (flatten values))]
;    )
;  )
;
;(defn find-max-minute [v]
;  (let [[first second] v]
;    [first (apply max-key val second)]
;    )
;  )
(def range-freq-minute (->> read-file
                            (parse-action)
                            (:histories)
                            (map calculate-duration-time)
                            (into {})
                            (map (fn [[id seq]] [id (frequencies (flatten seq))]))
                            ;(map flatten-values)
                            (map (fn [[id m]] [id (apply max-key val m)]))
                            ;(map find-max-minute)
                            (apply max-key (fn [[_ [_ freq]]] freq))
                            (apply (fn [id [minute _]] (* id minute)))
                            ))

(comment range-freq-minute)