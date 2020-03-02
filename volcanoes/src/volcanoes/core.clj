(ns volcanoes.core
  (:require [clojure.data.csv :as csv]
            [clojure.java.io  :as io]))

(def csv-lines
  (with-open [csv (io/reader "/home/ben/Downloads/GVP_Volcano_List_Holocene.csv")]
    (doall
     (csv/read-csv csv))))

(defn transform-header [header]
  (if (= "Elevation (m)" header)
    :elevation-meters
    (-> header
        clojure.string/lower-case
        (clojure.string/replace #" " "-")
        keyword)))

(defn transform-header-row [header-line]
  (map transform-header header-line))

(def volcano-records
  (let [csv-lines (rest csv-lines)
        header-line (transform-header-row (first csv-lines))
        volcano-lines (rest csv-lines)]
    (map (fn [volcano-line]
           (zipmap header-line volcano-line))
         volcano-lines)))

(defn parse-numbers [volcano]
  (-> volcano
      (update :elevation-meters #(Integer/parseInt %))
      (update :longitude #(Double/parseDouble %))
      (update :latitude #(Double/parseDouble %))))

(def volcanoes-parsed
  (map parse-numbers volcano-records))

(def types (set (map :primary-volcano-type volcano-records)))

(comment
  ;; REPL-driven code
  (let [volcano (nth volcanoes-parsed 10)]
    (clojure.pprint/pprint volcano))
  ;; RDC II
  (let [volcano (filter #(= "221291" (:volcano-number %)) volcanoes-parsed)]
    (clojure.pprint/pprint volcano)))
  ;;)

