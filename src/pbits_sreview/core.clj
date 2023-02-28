(ns pbits-sreview.core
  (:require [oz.core :as oz]
            [oz.server :as server])
  (:gen-class))

(when-not (server/web-server-started?)
  (oz/start-server!))

(def tools ; TODO(Richo)
  {:nqc {:name "Not Quite C (NQC)" :type :textual}
   :robotc {:name "RobotC" :type :textual}
   :cpp {:name "C/C++" :type :textual}
   :zr {:name "ZR graphical editor" :type :blocks}
   :cherp {:name "CHERP" :type :tangible-visual}
   :nxt-g {:name "NXT-G" :type :blocks}
   :mvpl {:name "Microsoft Visual Programming Language" :type :nodes}
   :arduino-c {:name "Arduino C" :type :textual}
   :labview {:name "LabView" :type :nodes}
   :robolab {:name "RobotLab" :type :blocks}
   :proteas {:name "PROTEAS" :type :tangible-visual}
   :modebots {:name "MODEBOTS" :type :blocks}
   :titibots {:name "TITIBOTS" :type nil}
   :picaxe {:name "PICAXE Programming Editor" :type nil}
   :appinventor {:name "AppInventor" :type nil}
   :s4a {:name "Snap4Arduino / Scratch for Arduino (S4A)" :type nil}
   :wedo {:name "Lego WeDo software" :type nil}
   :scratch {:name "Scratch" :type nil}
   :snap {:name "Snap!" :type nil} ; TODO(Richo): Maybe merge with Scratch?
   :aseba {:name "Aseba" :type nil}
   :enchanting {:name "Enchanting" :type nil} ; TODO(Richo): No es Scratch para LEGO este? Verificar y mergear...
   :modkit {:name "Modkit" :type nil}
   :beebot {:name "Bee-Bot (and similar)" :type nil}
   :tangible {:name "Tangible (KIBO and similar)" :type nil}
   :choregraphe {:name "Choregraphe" :type nil}
   :mblock {:name "mBLock: Scratch for mBot" :type nil}
   :bluej {:name "BlueJ" :type nil}
   :phogo {:name "Phogo" :type nil}
   :viple {:name "VIPLE" :type nil}
   :makecode {:name "MakeCode" :type nil}
   :crumble {:name "Crumble" :type nil}
   :talkoo {:name "Talkoo toolkit" :type nil}
   :blocklytalky {:name "BlocklyTalky" :type nil}
   :sphero-oop {:name "C# (con SpheroOOP)" :type nil}
   :sphero-edu {:name "Sphero Edu" :type nil}
   :vedils {:name "VEDILS authoring tool" :type nil}
   :scratchx {:name "ScratchX" :type nil} ; TODO(Richo): Verificar si difiere de Scratch lo suficiente y mergear?
   :pybokids {:name "Python (PyBoKids framework)" :type nil}
   :kinderbot {:name "KinderBot (iPad app)" :type nil}
   :bipes {:name "BIPES" :type nil}
   :learnblock {:name "LearnBlock" :type nil}
   :blockly {:name "Google Blockly" :type nil}
   :ev3 {:name "Lego EV3 programming language" :type nil} ; TODO(Richo): Cómo se llama? NXT-G?? EV3-G?
   :eud-mars {:name "EUD-MARS" :type nil}
   :ardublockly {:name "Ardublockly" :type nil}
   :python {:name "Python" :type nil}
   :thymio-vpl {:name "Thymio VPL" :type nil}})

(count tools)


(def papers
  [{:id 1 :doi "10.1109/TE.2012.2190071" :year 2012
    :ages [6 7]
    :tools [:tangible]}
   {:id 2 :doi "10.1109/TLT.2011.28" :year 2012
    :ages [11 13]
    :tools [:nqc]}
   {:id 3 :doi "10.1007/s00779-011-0404-2" :year 2012
    :ages [4 7]
    :tools [:tangible]}
   {:id 4 :doi "10.5755/j01.eee.18.9.2825" :year 2012
    :ages [16 17]
    :tools [:robotc]}
   {:id 5 :doi "10.1016/j.actaastro.2012.09.006" :year 2013
    :ages [13 17]
    :tools [:cpp :zr]}
   {:id 6 :doi "10.1080/15391523.2013.10782614" :year 2013
    :ages [4 6]
    :tools [:cherp]}
   {:id 7 :doi "10.1080/08993408.2013.847165" :year 2013
    :ages [14 15]
    :tools [:robotc]}
   {:id 8 :doi "10.1080/08993408.2013.847152" :year 2013
    :ages [9 13]
    :tools [:nxt-g]}
   {:id 9 :doi "10.5755/j01.eee.20.1.6169" :year 2014
    :ages [18]
    :tools [:nxt-g :mvpl :arduino-c]}
   {:id 10 :doi "10.5772/58249" :year 2014
    :ages [18]
    :tools [:labview :robolab :nxt-g :robotc :nqc]}
   {:id 11 :doi "10.1016/j.compedu.2013.10.020" :year 2014
    :ages [5 6]
    :tools [:cherp]}
   {:id 12 :doi "10.1007/s00779-014-0774-3" :year 2015
    :ages [6 12]
    :tools [:proteas]}
   {:id 13 :doi "10.1016/j.cag.2015.04.008" :year 2015
    :ages [18]
    :tools [:arduino-c]}
   {:id 14 :doi "10.1007/s10798-014-9287-7" :year 2015
    :ages [5 6]
    :tools [:cherp]}
   {:id 15 :doi "10.1109/RITA.2015.2452692" :year 2015
    :ages [4 6]
    :tools [:modebots]}
   {:id 16 :doi "10.1155/2016/1714350" :year 2016
    :ages [4 6]
    :tools [:titibots]}
   {:id 17 :doi "10.1007/s10798-015-9304-5" :year 2016
    :ages [4 7]
    :tools [:cherp]}
   {:id 18 :doi "10.1109/MRA.2016.2533002" :year 2016
    :ages [18]
    :tools [:labview]}
   {:id 19 :doi "10.1007/978-3-319-55553-9_7" :year 2017
    :ages [11 12]
    :tools [:picaxe]}
   {:id 20 :doi "10.1007/978-3-319-55553-9_15" :year 2017
    :ages [15 16]
    :tools [:appinventor]}
   {:id 21 :doi "10.1007/978-3-319-55553-9_2" :year 2017
    :ages [8 12]
    :tools [:s4a]}
   {:id 22 :doi "10.1007/978-3-319-55553-9_13" :year 2017
    :ages [10 12]
    :tools [:wedo]}
   {:id 23 :doi "10.1007/978-3-319-55553-9_17" :year 2017
    :ages [14 15]
    :tools [:nxt-g]}
   {:id 24 :doi "10.1007/978-3-319-55553-9_22" :year 2017
    :ages [10 12]
    :tools [:wedo]}
   {:id 25 :doi "10.1007/978-3-319-55553-9_14" :year 2017
    :ages [9 12]
    :tools [:scratch]}
   {:id 26 :doi "10.1007/978-3-319-55553-9_6" :year 2017
    :ages [18]
    :tools [:aseba]}
   {:id 27 :doi "10.1109/MRA.2016.2636372" :year 2017
    :ages nil
    :tools [:aseba]}
   {:id 28 :doi "10.1145/3043950" :year 2017
    :ages [10 14]
    :tools [:scratch :snap]}
   {:id 29 :doi "10.1109/TE.2016.2622227" :year 2017
    :ages [10 12]
    :tools [:tangible]}
   {:id 30 :doi "10.1145/3025013" :year 2017
    :ages [12 13]
    :tools [:enchanting :modkit]}
   {:id 31 :doi "10.1109/RITA.2017.2697739" :year 2017
    :ages [15 17]
    :tools [:nxt-g]}
   {:id 32 :doi "10.1016/j.chb.2017.01.018" :year 2017
    :ages [5 6]
    :tools [:beebot]}
   {:id 33 :doi "10.1016/j.compedu.2017.03.001" :year 2017
    :ages [10 11]
    :tools [:choregraphe]}
   {:id 34 :doi "10.1109/TLT.2016.2627565" :year 2017
    :ages [17 24]
    :tools [:arduino-c]}
   {:id 35 :doi "10.1166/asl.2017.10252" :year 2017
    :ages [3 5]
    :tools [:tangible]}
   {:id 36 :doi "10.20965/jrm.2017.p0980" :year 2017
    :ages [5 13]
    :tools [:beebot]}
   {:id 37 :doi "10.29333/ejmste/93483" :year 2018
    :ages [10 11]
    :tools [:mblock]}
   {:id 38 :doi "10.1109/RITA.2018.2801898" :year 2018
    :ages [18]
    :tools [:bluej]}
   {:id 39 :doi "10.1016/j.chb.2017.09.029" :year 2018
    :ages [16 18]
    :tools [:phogo]}
   {:id 40 :doi "10.1515/itit-2017-0032" :year 2018
    :ages [12 16]
    :tools [:s4a]}
   {:id 41 :doi "10.1007/s10798-017-9397-0" :year 2018
    :ages [3 6]
    :tools [:tangible]}
   {:id 42 :doi "10.1049/trit.2018.0016" :year 2018
    :ages [14 18]
    :tools [:viple]}
   {:id 43 :doi "10.1145/3211332.3211335" :year 2018
    :ages nil
    :tools [:makecode]}
   {:id 44 :doi "10.1002/cae.21966" :year 2018
    :ages [18]
    :tools [:crumble :arduino-c]}
   {:id 45 :doi "10.1016/j.ijcci.2018.03.002" :year 2018
    :ages [14 18]
    :tools [:talkoo]}
   {:id 46 :doi "10.1016/j.ijcci.2018.03.004" :year 2018
    :ages [12 14]
    :tools [:blocklytalky]}
   {:id 47 :doi "10.14201/eks2019_20_a17" :year 2019
    :ages [3 6]
    :tools [:beebot :tangible]}
   {:id 48 :doi "10.1109/ACCESS.2019.2895913" :year 2019
    :ages [18]
    :tools [:sphero-oop :sphero-edu :vedils]}
   {:id 49 :doi "10.20368/1971-8829/1625" :year 2019
    :ages [18]
    :tools [:cpp]}
   {:id 50 :doi "10.1145/3336126" :year 2019
    :ages [14 15]
    :tools [:appinventor :scratchx]}
   {:id 51 :doi "10.3390/electronics8080899" :year 2019
    :ages [12 16]
    :tools [:pybokids]}
   {:id 52 :doi "10.1108/JET-12-2018-0069" :year 2019
    :ages [10 11]
    :tools [:kinderbot]}
   {:id 53 :doi "10.1016/j.sysarc.2019.05.005" :year 2019
    :ages [16 18]
    :tools [:makecode]}
   {:id 54 :doi "10.18178/ijmerr.8.5.764-770" :year 2019
    :ages [12 14]
    :tools [:scratch]}
   {:id 55 :doi "10.3390/informatics6040043" :year 2019
    :ages [8 10]
    :tools [:wedo]}
   {:id 56 :doi "10.1080/07380569.2019.1677436" :year 2019
    :ages [3 4]
    :tools [:beebot]}
   {:id 57 :doi "10.1080/1475939X.2019.1670248" :year 2019
    :ages [6 10]
    :tools [:choregraphe :python]}
   {:id 58 :doi "10.1109/RITA.2019.2950130" :year 2019
    :ages [6 11]
    :tools [:crumble :arduino-c]}
   {:id 59 :doi "10.1007/s40692-019-00147-3" :year 2019
    :ages [5 7]
    :tools [:tangible]}
   {:id 60 :doi "10.1109/ACCESS.2020.3035083" :year 2020
    :ages nil
    :tools [:bipes]}
   {:id 61 :doi "10.1109/ACCESS.2020.2972410" :year 2020
    :ages nil
    :tools [:learnblock]}
   {:id 62 :doi "10.1002/cae.22184" :year 2020
    :ages [10 18]
    :tools [:arduino-c]}
   {:id 63 :doi "10.1109/ACCESS.2020.3015533" :year 2020
    :ages [4 8]
    :tools [:blockly]}
   {:id 64 :doi "10.3390/educsci10080202" :year 2020
    :ages [7 8]
    :tools [:beebot]}
   {:id 65 :doi "10.3991/ijoe.v16i14.17069" :year 2020
    :ages [11 14]
    :tools [:makecode]}
   {:id 66 :doi "10.3389/frobt.2020.00021" :year 2020
    :ages [9 18]
    :tools [:ev3]}
   {:id 67 :doi "10.1080/15391523.2020.1713263" :year 2020
    :ages [8 10]
    :tools [:nxt-g :ev3]}
   {:id 68 :doi "10.1016/j.scico.2020.102534" :year 2020
    :ages [8 17]
    :tools [:eud-mars]}
   {:id 69 :doi "10.1109/TE.2021.3066891" :year 2021
    :ages [8 12]
    :tools [:crumble]}
   {:id 70 :doi "10.1016/j.ijcci.2021.100388" :year 2021
    :ages [8 9]
    :tools [:beebot]}
   {:id 71 :doi "10.46328/IJEMST.1205" :year 2021
    :ages [9 10]
    :tools [:sphero-edu]}
   {:id 72 :doi "10.1007/s10798-021-09677-3" :year 2021
    :ages [7 9]
    :tools [:scratch]}
   {:id 73 :doi "10.1108/ITSE-04-2021-0074" :year 2021
    :ages [18]
    :tools [:arduino-c]}
   {:id 74 :doi "10.29333/ejmste/10842" :year 2021
    :ages [10 11]
    :tools [:s4a]}
   {:id 75 :doi "10.1109/TLT.2021.3058060" :year 2021
    :ages [4 10]
    :tools [:beebot]}
   {:id 76 :doi "10.1007/s10798-019-09559-9" :year 2021
    :ages [9 10]
    :tools [:nxt-g]}
   {:id 77 :doi "10.1109/RITA.2021.3089919" :year 2021
    :ages [6 18]
    :tools [:arduino-c]}
   {:id 78 :doi "10.1016/j.compedu.2021.104222" :year 2021
    :ages [5 9]
    :tools [:tangible]}
   {:id 79 :doi "10.3390/educsci11090518" :year 2021
    :ages [6 9]
    :tools [:beebot]}
   {:id 80 :doi "10.3390/s21186243" :year 2021
    :ages [11 13]
    :tools [:makecode]}
   {:id 81 :doi "10.1111/jcal.12570" :year 2021
    :ages [10 12]
    :tools [:blockly]}
   {:id 82 :doi "10.3390/electronics10243056" :year 2021
    :ages [9 12]
    :tools [:ardublockly]}
   {:id 83 :doi "10.1007/s10758-021-09508-3" :year 2021
    :ages [15 19]
    :tools [:blockly :python]}
   {:id 84 :doi "10.1080/10494820.2019.1636090" :year 2022
    :ages [11 12]
    :tools [:scratch]}
   {:id 85 :doi "10.1007/s13218-021-00752-4" :year 2022
    :ages [4 5]
    :tools [:beebot]}
   {:id 86 :doi "10.1016/j.compedu.2022.104431" :year 2022
    :ages [8 9]
    :tools [:thymio-vpl]
    }])

(do ; Verify paper tools
  (doseq [paper papers]
    (if (empty? (:tools paper))
      (println "Paper" (:id paper) "has NO tools!")
      (doseq [tool (:tools paper)]
        (when-not (contains? tools tool)
          (println "Paper" (:id paper) "has invalid tool" tool))))))

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