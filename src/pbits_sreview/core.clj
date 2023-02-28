(ns pbits-sreview.core
  (:require [oz.core :as oz]
            [oz.server :as server])
  (:gen-class))

(when-not (server/web-server-started?)
  (oz/start-server!))


(def papers
  [{:id 1 :doi "10.1109/TE.2012.2190071" :year 2012
    :ages [6 7]}
   {:id 2 :doi "10.1109/TLT.2011.28" :year 2012
    :ages [11 13]}
   {:id 3 :doi "10.1007/s00779-011-0404-2" :year 2012
    :ages [4 7]}
   {:id 4 :doi "10.5755/j01.eee.18.9.2825" :year 2012
    :ages [16 17]}
   {:id 5 :doi "10.1016/j.actaastro.2012.09.006" :year 2013
    :ages [13 17]}
   {:id 6 :doi "10.1080/15391523.2013.10782614" :year 2013
    :ages [4 6]}
   {:id 7 :doi "10.1080/08993408.2013.847165" :year 2013
    :ages [14 15]}
   {:id 8 :doi "10.1080/08993408.2013.847152" :year 2013
    :ages [9 13]}
   {:id 9 :doi "10.5755/j01.eee.20.1.6169" :year 2014
    :ages [18]}
   {:id 10 :doi "10.5772/58249" :year 2014
    :ages [18]}
   {:id 11 :doi "10.1016/j.compedu.2013.10.020" :year 2014
    :ages [5 6]}
   {:id 12 :doi "10.1007/s00779-014-0774-3" :year 2015
    :ages [6 12]}
   {:id 13 :doi "10.1016/j.cag.2015.04.008" :year 2015
    :ages [18]}
   {:id 14 :doi "10.1007/s10798-014-9287-7" :year 2015
    :ages [5 6]}
   {:id 15 :doi "10.1109/RITA.2015.2452692" :year 2015
    :ages [4 6]}
   {:id 16 :doi "10.1155/2016/1714350" :year 2016
    :ages [4 6]}
   {:id 17 :doi "10.1007/s10798-015-9304-5" :year 2016
    :ages [4 7]}
   {:id 18 :doi "10.1109/MRA.2016.2533002" :year 2016
    :ages [18]}
   {:id 19 :doi "10.1007/978-3-319-55553-9_7" :year 2017
    :ages [11 12]}
   {:id 20 :doi "10.1007/978-3-319-55553-9_15" :year 2017
    :ages [15 16]}
   {:id 21 :doi "10.1007/978-3-319-55553-9_2" :year 2017
    :ages [8 12]}
   {:id 22 :doi "10.1007/978-3-319-55553-9_13" :year 2017
    :ages [10 12]}
   {:id 23 :doi "10.1007/978-3-319-55553-9_17" :year 2017
    :ages [14 15]}
   {:id 24 :doi "10.1007/978-3-319-55553-9_22" :year 2017
    :ages [10 12]}
   {:id 25 :doi "10.1007/978-3-319-55553-9_14" :year 2017
    :ages [9 12]}
   {:id 26 :doi "10.1007/978-3-319-55553-9_6" :year 2017
    :ages [18]}
   {:id 27 :doi "10.1109/MRA.2016.2636372" :year 2017
    :ages nil}
   {:id 28 :doi "10.1145/3043950" :year 2017
    :ages [10 14]}
   {:id 29 :doi "10.1109/TE.2016.2622227" :year 2017
    :ages [10 12]}
   {:id 30 :doi "10.1145/3025013" :year 2017
    :ages [12 13]}
   {:id 31 :doi "10.1109/RITA.2017.2697739" :year 2017
    :ages [15 17]}
   {:id 32 :doi "10.1016/j.chb.2017.01.018" :year 2017
    :ages [5 6]}
   {:id 33 :doi "10.1016/j.compedu.2017.03.001" :year 2017
    :ages [10 11]}
   {:id 34 :doi "10.1109/TLT.2016.2627565" :year 2017
    :ages [17 24]}
   {:id 35 :doi "10.1166/asl.2017.10252" :year 2017
    :ages [3 5]}
   {:id 36 :doi "10.20965/jrm.2017.p0980" :year 2017
    :ages [5 13]}
   {:id 37 :doi "10.29333/ejmste/93483" :year 2018
    :ages [10 11]}
   {:id 38 :doi "10.1109/RITA.2018.2801898" :year 2018
    :ages [18]}
   {:id 39 :doi "10.1016/j.chb.2017.09.029" :year 2018
    :ages [16 18]}
   {:id 40 :doi "10.1515/itit-2017-0032" :year 2018
    :ages [12 16]}
   {:id 41 :doi "10.1007/s10798-017-9397-0" :year 2018
    :ages [3 6]}
   {:id 42 :doi "10.1049/trit.2018.0016" :year 2018
    :ages [14 18]}
   {:id 43 :doi "10.1145/3211332.3211335" :year 2018
    :ages nil}
   {:id 44 :doi "10.1002/cae.21966" :year 2018
    :ages [18]}
   {:id 45 :doi "10.1016/j.ijcci.2018.03.002" :year 2018
    :ages [14 18]}
   {:id 46 :doi "10.1016/j.ijcci.2018.03.004" :year 2018
    :ages [12 14]}
   {:id 47 :doi "10.14201/eks2019_20_a17" :year 2019
    :ages [3 6]}
   {:id 48 :doi "10.1109/ACCESS.2019.2895913" :year 2019
    :ages [18]}
   {:id 49 :doi "10.20368/1971-8829/1625" :year 2019
    :ages [18]}
   {:id 50 :doi "10.1145/3336126" :year 2019
    :ages [14 15]}
   {:id 51 :doi "10.3390/electronics8080899" :year 2019
    :ages [12 16]}
   {:id 52 :doi "10.1108/JET-12-2018-0069" :year 2019
    :ages [10 11]}
   {:id 53 :doi "10.1016/j.sysarc.2019.05.005" :year 2019
    :ages [16 18]}
   {:id 54 :doi "10.18178/ijmerr.8.5.764-770" :year 2019
    :ages [12 14]}
   {:id 55 :doi "10.3390/informatics6040043" :year 2019
    :ages [8 10]}
   {:id 56 :doi "10.1080/07380569.2019.1677436" :year 2019
    :ages [3 4]}
   {:id 57 :doi "10.1080/1475939X.2019.1670248" :year 2019
    :ages [6 10]}
   {:id 58 :doi "10.1109/RITA.2019.2950130" :year 2019
    :ages [6 11]}
   {:id 59 :doi "10.1007/s40692-019-00147-3" :year 2019
    :ages [5 7]}
   {:id 60 :doi "10.1109/ACCESS.2020.3035083" :year 2020
    :ages nil}
   {:id 61 :doi "10.1109/ACCESS.2020.2972410" :year 2020
    :ages nil}
   {:id 62 :doi "10.1002/cae.22184" :year 2020
    :ages [10 18]}
   {:id 63 :doi "10.1109/ACCESS.2020.3015533" :year 2020
    :ages [4 8]}
   {:id 64 :doi "10.3390/educsci10080202" :year 2020
    :ages [7 8]}
   {:id 65 :doi "10.3991/ijoe.v16i14.17069" :year 2020
    :ages [11 14]}
   {:id 66 :doi "10.3389/frobt.2020.00021" :year 2020
    :ages [9 18]}
   {:id 67 :doi "10.1080/15391523.2020.1713263" :year 2020
    :ages [8 10]}
   {:id 68 :doi "10.1016/j.scico.2020.102534" :year 2020
    :ages [8 17]}
   {:id 69 :doi "10.1109/TE.2021.3066891" :year 2021
    :ages [8 12]}
   {:id 70 :doi "10.1016/j.ijcci.2021.100388" :year 2021
    :ages [8 9]}
   {:id 71 :doi "10.46328/IJEMST.1205" :year 2021
    :ages [9 10]}
   {:id 72 :doi "10.1007/s10798-021-09677-3" :year 2021
    :ages [7 9]}
   {:id 73 :doi "10.1108/ITSE-04-2021-0074" :year 2021
    :ages [18]}
   {:id 74 :doi "10.29333/ejmste/10842" :year 2021
    :ages [10 11]}
   {:id 75 :doi "10.1109/TLT.2021.3058060" :year 2021
    :ages [4 10]}
   {:id 76 :doi "10.1007/s10798-019-09559-9" :year 2021
    :ages [9 10]}
   {:id 77 :doi "10.1109/RITA.2021.3089919" :year 2021
    :ages [6 18]}
   {:id 78 :doi "10.1016/j.compedu.2021.104222" :year 2021
    :ages [5 9]}
   {:id 79 :doi "10.3390/educsci11090518" :year 2021
    :ages [6 9]}
   {:id 80 :doi "10.3390/s21186243" :year 2021
    :ages [11 13]}
   {:id 81 :doi "10.1111/jcal.12570" :year 2021
    :ages [10 12]}
   {:id 82 :doi "10.3390/electronics10243056" :year 2021
    :ages [9 12]}
   {:id 83 :doi "10.1007/s10758-021-09508-3" :year 2021
    :ages [15 19]}
   {:id 84 :doi "10.1080/10494820.2019.1636090" :year 2022
    :ages [11 12]}
   {:id 85 :doi "10.1007/s13218-021-00752-4" :year 2022
    :ages [4 5]}
   {:id 86 :doi "10.1016/j.compedu.2022.104431" :year 2022
    :ages [8 9]}])

(defn generate-vega-doc []
  [:div {:style {:display "flex" :flex-wrap "wrap"}}
   [:vega-lite {:data {:values papers}
                :encoding {:x {:field :year
                               :type "ordinal"}
                           :y {:field "doi"
                               :aggregate "count"}
                           ;:color {:field :year}
                           }
                :layer [{:mark {:type :line :point true :tooltip true}}]}]])

(defn redraw! []
  (println "REDRAW!")
  (oz/view! (generate-vega-doc)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(comment

  (redraw!)

  )