(ns query-processor.util
  (:require [clojure.pprint :refer [pprint]]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [clj-http.client :as http]))

(defn str->key [column-name]
  (let [trimmed (str/trim column-name)]
    (if (empty? trimmed)
      :_
      (keyword (str/replace trimmed #"\s" "_")))))

(defn csv->maps [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map #(str/replace-first % #"^:" ""))
            (map keyword) ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(defn read-csv-file [file]
  (-> file
      io/reader
      csv/read-csv
      csv->maps))

(defn write-csv-file [data file-name columns]
  (with-open [writer (io/writer file-name)]
    (csv/write-csv writer
                   (concat [columns]
                           (map (fn [row]
                                  (mapv row columns))
                                data)))))

(defn read-csv-many
  ([file-pattern] (read-csv-many "." file-pattern))
  ([root file-pattern]
   (mapcat read-csv-file
           (sort-by (memfn getName)
                    (filter #(re-matches file-pattern (.getName %))
                            (seq (.listFiles (io/file root))))))))

(defn seek
  ([pred coll]
   (reduce #(when (pred %2) (reduced %2)) nil coll))
  ([pred coll default-value]
   (or (reduce #(when (pred %2) (reduced %2)) nil coll)
       default-value)))