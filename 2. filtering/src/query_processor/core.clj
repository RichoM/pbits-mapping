(ns query-processor.core
  (:require [clojure.string :as str]
            [clojure.data :as data]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [query-processor.util :as u]))

(set! *print-level* 15)
(set! *print-length* 100)

(defn add-columns [data]
  (map #(-> %
            (assoc :YEAR (read-string (first (str/split (:coverDate %) #"-"))))
            (assoc :URL (str "https://doi.org/" (:doi %))))
       data))

(comment

  (def data-7 (->> (u/read-csv-many "../1. query/csv" #"query_7_\d+\.csv")
                   (add-columns)
                   (remove #(>= (:YEAR %) 2022))))

  (count data-7) ;; 3759 al empezar


  (def data-7
    (->> data-7
         (filter #(contains? #{"ar"} (:subtype %)))))

  (count data-7) ;; 1040 después de filtrar por tipo de documento

  ;; IC1: Artículos que no tengan DOI
  (def data-7
    (->> data-7
         (remove (comp empty? :doi))))

  (count data-7) ;; 969
  (- 1040 969)

  ;; EC1: Artículos que contengan las palabras “simulator” o “simulation” o “virtual” en el título (para descartar artículos enfocados en simuladores o robots virtuales, el objetivo es entornos de programación de robots físicos).
  (def data-7
    (->> data-7
         (remove (comp #(re-find #"(?iu)\b(simulator|simulation|virtual)\b" %)
                       :title))))

  (count data-7) ;; 917
  (- 969 917)

  ;; EC2: Artículos que contengan “surgery” en el título (para descartar artículos relacionados con robótica aplicada en medicina).
  (def data-7
    (->> data-7
         (remove (comp #(re-find #"(?iu)\b(surgery|surgical|medical)\b" %)
                       :title))))

  (count data-7) ;; 907
  (- 917 907)

  ;; EC3: Artículos que contengan “cloud” en el título (para descartar artículos relacionados con “cloud robotics”).
  (def data-7
    (->> data-7
         (remove (comp #(re-find #"(?iu)\b(cloud)\b" %)
                       :title))))

  (count data-7) ;; 897
  (- 907 897)

  ;; EC4: Artículos que contengan (“neural network*” OR “machine learning”) en el título (para descartar artículos vinculados a algoritmos de inteligencia artificial).
  (def data-7
    (->> data-7
         (remove (comp #(re-find #"(?iu)\b(neural network.*|machine learning)\b" %)
                       :title))))

  (count data-7) ;; 853
  (- 897 853)

  (+ 65 22 11 187 352 81 5 72 149
     96)
  )