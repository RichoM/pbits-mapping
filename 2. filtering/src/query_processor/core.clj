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

(comment ;; La primera búsqueda fue hasta el 2021 incluido

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

(comment ;; La segunda búsqueda se hizo el 21/9/2023 e incluye tanto 2022 como 2023 (hasta la fecha)

  (def data-8 (->> (u/read-csv-many "../1. query/csv" #"query_8_\d+\.csv")
                   (add-columns)))

  (count data-8) ;; 1260 al empezar


  (def data-8
    (->> data-8
         (filter #(contains? #{"ar"} (:subtype %)))))

  (count data-8) ;; 614 después de filtrar por tipo de documento

  ;; IC1: Artículos que no tengan DOI
  (def data-8
    (->> data-8
         (remove (comp empty? :doi))))

  (count data-8) ;; 607
  (- 614 607) ;; Se excluyeron 7 artículos

  ;; EC1: Artículos que contengan las palabras “simulator” o “simulation” o “virtual” en el título (para descartar artículos enfocados en simuladores o robots virtuales, el objetivo es entornos de programación de robots físicos).
  (def data-8
    (->> data-8
         (remove (comp #(re-find #"(?iu)\b(simulator|simulation|virtual)\b" %)
                       :title))))

  (count data-8) ;; 587
  (- 607 587) ;; Se excluyeron 20 artículos

  ;; EC2: Artículos que contengan “surgery” en el título (para descartar artículos relacionados con robótica aplicada en medicina).
  (def data-8
    (->> data-8
         (remove (comp #(re-find #"(?iu)\b(surgery|surgical|medical)\b" %)
                       :title))))

  (count data-8) ;; 580
  (- 587 580) ;; Se excluyeron 7 artículos

  ;; EC3: Artículos que contengan “cloud” en el título (para descartar artículos relacionados con “cloud robotics”).
  (def data-8
    (->> data-8
         (remove (comp #(re-find #"(?iu)\b(cloud)\b" %)
                       :title))))

  (count data-8) ;; 576
  (- 580 576) ;; Se excluyeron 4 artículos

  ;; EC4: Artículos que contengan (“neural network*” OR “machine learning”) en el título (para descartar artículos vinculados a algoritmos de inteligencia artificial).
  (def data-8
    (->> data-8
         (remove (comp #(re-find #"(?iu)\b(neural network.*|machine learning)\b" %)
                       :title))))

  (count data-8) ;; 558
  (- 576 558) ;; Se excluyeron 18 artículos

  (u/write-csv-file data-8 "../1. query/csv/query_8_all.csv"
                    (->> (str/split ":doi,:title,:description,:subtype,:coverDate,:publicationName,:authkeywords,:eid,:pii,:pubmed_id,:subtypeDescription,:creator,:afid,:affilname,:affiliation_city,:affiliation_country,:author_count,:author_names,:author_ids,:author_afids,:coverDisplayDate,:issn,:source_id,:eIssn,:aggregationType,:volume,:issueIdentifier,:article_number,:pageRange,:openaccess,:fund_acr,:fund_no,:fund_sponsor,:citedby_count,:YEAR,:URL"
                                    #",")
                         (map #(str/replace-first % #"^:" ""))
                         (map keyword)))
  
  )