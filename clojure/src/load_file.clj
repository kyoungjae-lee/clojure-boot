(ns load-file)

(defn load-read-file [filename]
  (->> filename
       (slurp)
       (clojure.string/split-lines)
       ))
