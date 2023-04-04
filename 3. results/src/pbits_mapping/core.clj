(ns pbits-mapping.core
  (:require [oz.core :as oz]
            [oz.server :as server]
            [markdown-to-hiccup.core :as m]
            [clojure.string :as str]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.data :as data])
  (:gen-class))

(when-not (server/web-server-started?)
  (oz/start-server!))

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            (map #(str/replace-first % #"^:" ""))
            (map keyword) ;; Drop if you want string keys instead
            repeat)
       (rest csv-data)))

(def indexed-by
  (memoize
   (fn [unique-key data]
     (->> data
          (group-by unique-key)
          (map (fn [[doi [row]]] [doi row]))
          (into {})))))

(def paper-data (->> (with-open [reader (io/reader "data.csv")]
                       (doall (csv-data->maps (csv/read-csv reader))))
                     (map (fn [paper]
                            (assoc paper :AUTHORS
                                   (let [author-names (:author_names paper)]
                                     (if (str/blank? author-names)
                                       [(:creator paper)]
                                       (str/split author-names #";"))))))
                     (map (fn [paper]
                            (let [authors (str/join ", " (:AUTHORS paper))
                                  year (:YEAR paper)
                                  title (:title paper)
                                  publication-name (:publicationName paper)
                                  page-range (:pageRange paper)
                                  volume (:volume paper)
                                  number (:issueIdentifier paper)
                                  display-date (:coverDisplayDate paper)
                                  doi (:doi paper)]
                              (assoc paper :CITATION
                                     (str/join ", "
                                               (remove str/blank?
                                                       [authors
                                                        (format "\"%s,\" in %s" title publication-name)
                                                        (when-not (str/blank? volume)
                                                          (str "vol. " volume))
                                                        (when-not (str/blank? number)
                                                          (str "no. " number))
                                                        (when-not (str/blank? page-range)
                                                          (str "pp. " page-range))
                                                        (when-not (str/blank? display-date)
                                                          display-date)
                                                        (when-not (str/blank? doi)
                                                          (str "doi: " doi))]))))))))

(def robots
  {:custom {:name "Robot personalizado" :type :custom}
   :nxt {:name "LEGO Mindstorms NXT" :type :lego}
   :rcx {:name "LEGO Mindstorms RCX" :type :lego}
   :ev3 {:name "LEGO Mindstorms EV3" :type :lego}
   :spheres {:name "SPHERES" :type :other ; ACAACA 
             }
   :vex {:name "VEX robot" :type :kit ; Es un kit tipo LEGO
         }
   :4wd {:name "Arduino 4WD Mobile Platform"
         :type :arduino} ; 
   :5la {:name "Lynxmotion 5LA Robotic Arm"
         :type :robotic-arm}
   :controllab {:name "Control Lab"
                :type :lego}
   :wedo {:name "LEGO Education WeDo"
          :type :lego}
   :kiwi {:name "KIWI robotics kit"
          :type :kit}
   :dani {:name "LabVIEW Robotics Starter Kit"
          :type :kit}
   :basic {:name "Basic Robotic System plaftorm from H&S Electronic Systems"
           :type :kit}
   :thymio {:name "Thymio"
            :type :toy}
   :arduino-uno {:name "Arduino UNO" :type :arduino}
   :arduino-nano {:name "Arduino Nano" :type :arduino}
   :arduino-mega {:name "Arduino MEGA 2560" :type :arduino}
   :arduino-duo {:name "Arduino Duo" :type :arduino}
   :arduino-lilypad {:name "Arduino LilyPad" :type :arduino}
   :beebot {:name "BeeBot" :type :toy}
   :nao {:name "NAO by Aldebaran robotics"
         :type :humanoid}
   :c-block {:name "C-Block robot"
             :type :custom ; ??? 
             }
   :mbot {:name "mBot v1.1"
          :type :arduino}
   :bqzum {:name "bq Zum" :type :arduino}
   :kibo {:name "KIBO" :type :kit}
   :galileo {:name "Intel Galileo" :type :sbc}
   :raspberry {:name "Raspberry Pi" :type :sbc}
   :pcDuino {:name "pcDuino" :type :sbc}
   :minnow {:name "MinnowBoard" :type :sbc}
   :curie {:name "Intel Curie" :type :sbc}
   :edison {:name "Intel Edison" :type :sbc}
   :bioloid {:name "Robotis Bioloid" :type :kit}
   :microbit {:name "BBC micro:bit" :type :kit}
   :crumble {:name "Crumble robot" :type :kit}
   :talkoo {:name "Talkoo kit" :type :custom}
   :bluebot {:name "Blue-Bot" ; Es como el beebot pero con app mobile
             :type :toy}
   :roamer {:name "Roamer" ; Como beebot pero con un teclado intercambiable
            :type :toy}
   :cubetto {:name "CUBETTO"
             :type :toy}
   :codeapillar {:name "Code a pillar"
                 :type :toy}
   :sphero {:name "Sphero robot"
            :type :toy}
   :microrobots {:name "Micro robots (by Citizen Watch LTD)"
                 :type :other}
   :stm32 {:name "STM32 microcontroller" ; mbed nucleo entra acá
           :type :microcontroller}
   :wemosd1mini {:name "Wemos D1 Mini"
                 :type :microcontroller}
   :esp8266 {:name "ESP8266"
             :type :microcontroller}
   :esp32 {:name "ESP32"
           :type :microcontroller}
   :toradex {:name "Toradex"
             :type :sbc ; Single Board Computers (acá también entrarían las Raspberry) 
             }
   :beaglebone {:name "BeagleBone"
                :type :sbc}
   :pc104 {:name "PC/104" :type :sbc}
   :cozmo {:name "Cozmo" :type :toy}
   :parrot {:name "Parrot Bebop" :type :drone}
   :irobot {:name "iRobot Create" :type :vacuum-cleaner}
   :doc {:name "Robot DOC" :type :toy}
   :korean {:name "Unknown" :type :toy ; Este no ponen el nombre en el paper
            }
   :robbo {:name "Robbo (ScratchDuino)" :type :arduino}
   :turtlebot {:name "TurtleBot" :type :toy}
   :geombot {:name "GeomBot" :type :arduino}})


;; :s4a, :snap y :scratch -> :scratch
;; :ev3 y :nxt-g   -> :lego
;; :nqc y :nxc  ->  :nqc
;; agrego :microblocks :xod :snek
(def tools
  {:nqc {:name "NQC/NXC"
         :textual? true
         :textual-dsl? true
         :textual-type :cpp}
   :robotc {:name "RobotC"
            :textual? true
            :textual-dsl? true
            :textual-type :cpp}
   :cpp {:name "C/C++"
         :textual? true
         :textual-type :cpp}
   :zr {:name "ZR graphical editor"
        :visual? true
        :visual-dsl? true
        :visual-type :blocks}
   :tern {:name "Tern"
          :tangible? true
          :visual? true
          :visual-dsl? true
          :visual-type :blocks}
   :cherp {:name "CHERP"
           :tangible? true
           :visual? true
           :visual-dsl? true
           :visual-type :icons}
   :lego {:name "LEGO Mindstorms"
          :visual? true
          :visual-dsl? true
          :visual-type :blocks}
   :mvpl {:name "Microsoft Visual Programming Language"
          :visual? true
          :visual-dsl? true
          :visual-type :diagram}
   :arduino-c {:name "Arduino C"
               :textual? true
               :textual-dsl? true
               :textual-type :cpp}
   :labview {:name "LabView"
             :visual? true
             :visual-dsl? true
             :visual-type :diagram}
   :robolab {:name "RoboLab"
             :visual? true
             :visual-dsl? true
             :visual-type :icons}
   :proteas {:name "PROTEAS"
             :tangible? true
             :visual? true
             :visual-dsl? true
             :visual-type :icons}
   :modebots {:name "MODEBOTS"
              :visual? true
              :visual-dsl? true
              :visual-type :icons}
   :titibots {:name "TITIBOTS"
              :visual? true
              :visual-dsl? true
              :visual-type :icons}
   :picaxe {:name "PICAXE Programming Editor"
            :textual? true
            :textual-dsl? false
            :textual-type :basic
            :visual? true
            :visual-dsl? true
            :visual-type :diagram}
   :appinventor {:name "AppInventor"
                 :visual? true
                 :visual-dsl? true
                 :visual-type :blocks}
   :scratch {:name "Scratch/Snap/S4A"
             :visual? true
             :visual-dsl? true
             :visual-type :blocks}
   :wedo {:name "Lego WeDo software"
          :visual? true
          :visual-dsl? true
          :visual-type :blocks}
   :aseba {:name "Aseba"
           :textual? true
           :textual-dsl? true
           :textual-type :aseba}
   :enchanting {:name "Enchanting"
                :visual? true
                :visual-dsl? true
                :visual-type :blocks}
   :modkit {:name "Modkit"
            :textual? true
            :textual-dsl? true
            :textual-type :cpp ; TODO(Richo): Confirm
            :visual? true
            :visual-dsl? true
            :visual-type :blocks}
   :beebot {:name "Bee-Bot (and similar)"
            :tangible? true ; ponele
            }
   :tangible {:name "Tangible (KIBO and similar)"
              :tangible? true}
   :choregraphe {:name "Choregraphe"
                 :visual? true
                 :visual-dsl? true
                 :visual-type :diagram}
   :mblock {:name "mBLock: Scratch for mBot"
            :visual? true
            :visual-dsl? true
            :visual-type :blocks}
   :bluej {:name "BlueJ"
           :textual? true
           :textual-dsl? false
           :textual-type :java
           :visual? true
           :visual-dsl? true
           :visual-type :blocks}
   :phogo {:name "Phogo"
           :textual? true
           :textual-dsl? true
           :textual-type :python}
   :viple {:name "VIPLE"
           :visual? true
           :visual-dsl? true
           :visual-type :diagram}
   :makecode {:name "MakeCode"
              :textual? true
              :textual-dsl? false
              :textual-type :js
              :visual? true
              :visual-dsl? true
              :visual-type :blocks}
   :crumble {:name "Crumble"
             :visual? true
             :visual-dsl? true
             :visual-type :blocks}
   :talkoo {:name "Talkoo toolkit"
            :visual? true
            :visual-dsl? true
            :visual-type :diagram}
   :blocklytalky {:name "BlocklyTalky"
                  :visual? true
                  :visual-dsl? true
                  :visual-type :blocks}
   :sphero-oop {:name "C# (con SpheroOOP)"
                :textual? true
                :textual-type :csharp}
   :sphero-edu {:name "Sphero Edu"
                :textual? true
                :textual-dsl? false
                :textual-type :js
                :visual? true
                :visual-dsl? true
                :visual-type :blocks}
   :vedils {:name "VEDILS authoring tool"
            :visual? true
            :visual-dsl? true
            :visual-type :blocks}
   :scratchx {:name "ScratchX"
              :visual? true
              :visual-dsl? true
              :visual-type :blocks}
   :pybokids {:name "Python (PyBoKids framework)"
              :textual? true
              :textual-type :python}
   :pynxc {:name "PyNXC"
           :textual? true
           :textual-dsl? true
           :textual-type :python}
   :kinderbot {:name "KinderBot (iPad app)"
               :visual? true
               :visual-dsl? true
               :visual-type :icons}
   :bipes {:name "BIPES"
           :visual? true
           :visual-dsl? true
           :visual-type :blocks}
   :learnblock {:name "LearnBlock"
                :textual? true
                :textual-dsl? false
                :textual-type :python
                :visual? true
                :visual-dsl? true
                :visual-type :blocks}
   :blockly {:name "Google Blockly"
             :visual? true
             :visual-dsl? true
             :visual-type :blocks}
   :eud-mars {:name "EUD-MARS"
              :visual? true
              :visual-dsl? true
              :visual-type :blocks}
   :ardublockly {:name "Ardublockly"
                 :visual? true
                 :visual-dsl? true
                 :visual-type :blocks}
   :python {:name "Python"
            :textual? true
            :textual-type :python}
   :thymio-vpl {:name "Thymio VPL"
                :visual? true
                :visual-dsl? true
                :visual-type :icons}
   :microblocks {:name "MicroBlocks"
                 :visual? true
                 :visual-dsl? true
                 :visual-type :blocks}
   :xod {:name "XOD"
         :visual? true
         :visual-dsl? true
         :visual-type :diagram}
   :snek {:name "Snek"
          :textual? true
          :textual-dsl? true
          :textual-type :python}})


(do ; Verify tools
  (doseq [[name {:keys [tangible?
                        textual? textual-dsl? textual-type
                        visual? visual-dsl? visual-type]}]
          tools]
    (when (and textual?
               (not visual?)
               visual-dsl?)
      (println "A purely textual language couldn't be a visual DSL!" name))
    (when (and visual?
               (not textual?)
               textual-dsl?)
      (println "A purely visual language couldn't be a textual DSL!" name))
    (when-not (and tangible? (not visual?))
      (when (and visual? (not textual?))
        (when-not (nil? textual-type)
          (println "Invalid textual type!" name "->" textual-type)))
      (if (and textual? (not visual?))
        (when-not (nil? visual-type)
          (println "Invalid visual type!" name "->" visual-type))
        (when-not (contains? #{:blocks :diagram :icons :form} visual-type)
          (println "Invalid visual type!" name "->" visual-type))))
    (when-not (or tangible? textual? visual?)
      (println "WTF!" name))))

;; saco :sphero-oop :pybokids :python :cpp :blockly :appinventor :vedils :bluej
(def tool-features
  {:arduino-c {:name "Arduino IDE (C/C++)"
               :concurrency? false
               :liveness? false
               :debugging? false
               :monitoring? false
               :autonomy? true
               :visual? false
               :textual? true
               :arduino? true}
   :titibots {:name "TITIBOTS"
              :concurrency? false
              :liveness? false
              :debugging? false
              :monitoring? false
              :autonomy? false
              :visual? true
              :textual? false
              :arduino? false}
   :modkit {:name "Modkit"
            :concurrency? true
            :liveness? false
            :debugging? false
            :monitoring? false
            :autonomy? true
            :visual? true
            :textual? true
            :arduino? true}
   :learnblock {:name "LearnBlock"
                :concurrency? true
                :liveness? false
                :debugging? false
                :monitoring? false
                :autonomy? false
                :visual? true
                :textual? true
                :arduino? false}
   :pynxc {:name "PyNXC"
           :concurrency? true
           :liveness? false
           :debugging? false
           :monitoring? false
           :autonomy? true
           :visual? false
           :textual? true
           :arduino? false}
   :talkoo {:name "Talkoo toolkit"
            :concurrency? true
            :liveness? true
            :debugging? false
            :monitoring? true
            :autonomy? false
            :visual? true
            :textual? false
            :arduino? false}
   :choregraphe {:name "Choregraphe"
                 :concurrency? true
                 :liveness? false
                 :debugging? false
                 :monitoring? true
                 :autonomy? true
                 :visual? true
                 :textual? false
                 :arduino? false}
   ;; NOTE(Richo): Excluido por ser C# (no específico de robótica)
   #_(:sphero-oop {:concurrency? true
                   :liveness? false
                   :debugging? true
                   :monitoring? false
                   :autonomy? false})
   ;; NOTE(Richo): Merged with s4a
   #_(:snap {:concurrency? true
             :liveness? true
             :debugging? false
             :monitoring? true
             :autonomy? false})
   :makecode {:name "MakeCode"
              :concurrency? true
              :liveness? false
              :debugging? false
              :monitoring? false
              :autonomy? true
              :visual? true
              :textual? true
              :arduino? false}
   :modebots {:name "MODEBOTS"
              :concurrency? false
              :liveness? false
              :debugging? false
              :monitoring? false
              :autonomy? false
              :visual? true
              :textual? false
              :arduino? false}
   ;; NOTE(Richo): Merged with s4a
   #_(:scratch {:concurrency? true
                :liveness? true
                :debugging? false
                :monitoring? true
                :autonomy? false})
   :ardublockly {:name "Ardublockly"
                 :concurrency? false
                 :liveness? false
                 :debugging? false
                 :monitoring? false
                 :autonomy? true
                 :visual? true
                 :textual? false
                 :arduino? true}
   :viple {:name "VIPLE"
           :concurrency? true
           :liveness? false
           :debugging? false
           :monitoring? false
           :autonomy? false
           :visual? true
           :textual? false
           :arduino? true}
   :crumble {:name "Crumble"
             :concurrency? false
             :liveness? false
             :debugging? false
             :monitoring? false
             :autonomy? true
             :visual? true
             :textual? false
             :arduino? false}
   :blocklytalky {:name "BlocklyTalky"
                  :concurrency? true
                  :liveness? false
                  :debugging? false
                  :monitoring? true
                  :autonomy? true
                  :visual? true
                  :textual? false
                  :arduino? false}
   :aseba {:name "Aseba"
           :concurrency? true
           :liveness? false
           :debugging? true
           :monitoring? true
           :autonomy? true
           :visual? false
           :textual? true
           :arduino? false}
   :scratchx {:name "ScratchX"
              :concurrency? true
              :liveness? true
              :debugging? false
              :monitoring? true
              :autonomy? true
              :visual? true
              :textual? false
              :arduino? false}
   :enchanting {:name "Enchanting"
                :concurrency? true
                :liveness? false
                :debugging? false
                :monitoring? true
                :autonomy? true
                :visual? true
                :textual? false
                :arduino? false}
   ;; NOTE(Richo): Lo saco por no ser específico de robótica
   #_(:appinventor {:name "AppInventor"
                    :concurrency? true
                    :liveness? true
                    :debugging? false
                    :monitoring? false
                    :autonomy? false
                    :visual? true
                    :textual? false})
   :phogo {:name "Phogo"
           :concurrency? true
           :liveness? true
           :debugging? true ; NOTE(Richo): Debugging sólo porque python lo permite...
           :monitoring? true
           :autonomy? false
           :visual? false
           :textual? true
           :arduino? true}
   :sphero-edu {:name "Sphero Edu"
                :concurrency? true
                :liveness? false
                :debugging? false
                :monitoring? true
                :autonomy? false
                :visual? true
                :textual? true
                :arduino? false}
   ; NOTE(Richo): Merged with nxt-g into just :lego
   #_(:ev3 {:concurrency? true
            :liveness? false
            :debugging? false
            :monitoring? true
            :autonomy? true})
   :thymio-vpl {:name "Thymio VPL"
                :concurrency? false
                :liveness? false
                :debugging? true
                :monitoring? true
                :autonomy? true
                :visual? true
                :textual? false
                :arduino? false}
   :proteas {:name "PROTEAS"
             :concurrency? false
             :liveness? false
             :debugging? false
             :monitoring? false
             :autonomy? true
             :visual? true
             :textual? false
             :arduino? false}
   ;; NOTE(Richo): En el caso de mBlock tenés la posibilidad de usarlo conectado (con un firmware que no pude probar) o bajar el sketch auto-generado a la placa. Es como Physical Etoys que tenés 2 modos. Acá voy a considerarlo como en modo autónomo, por lo que el resto de las features están en false 
   :mblock {:name "mBlock 3"
            :concurrency? false
            :liveness? false
            :debugging? false
            :monitoring? false
            :autonomy? true
            :visual? true
            :textual? false
            :arduino? true}
   :wedo {:name "LEGO WeDo"
          :concurrency? true
          :liveness? true
          :debugging? false
          :monitoring? true
          :autonomy? false
          :visual? true
          :textual? false
          :arduino? false}
   :kinderbot {:name "KinderBot (iPad app)"
               :concurrency? false
               :liveness? true
               :debugging? false
               :monitoring? false
               :autonomy? true
               :visual? true
               :textual? false
               :arduino? false}
   ; NOTE(Richo): Acá agrego también NXC
   :nqc {:name "NQC/NXC"
         :concurrency? true
         :liveness? false
         :debugging? false
         :monitoring? false
         :autonomy? true
         :visual? false
         :textual? true
         :arduino? false}
   ; NOTE(Richo): Lo saqué porque es sólo un framework en python, no es un lenguaje nuevo
   #_(:pybokids {:concurrency? true
                 :liveness? false
                 :debugging? false
                 :monitoring? false
                 :autonomy? false})
   :mvpl {:name "Microsoft VPL"
          :concurrency? true
          :liveness? false
          :debugging? false
          :monitoring? true
          :autonomy? true
          :visual? true
          :textual? false
          :arduino? true}
   :robolab {:name "RoboLab"
             :concurrency? true
             :liveness? false
             :debugging? false
             :monitoring? false
             :autonomy? true
             :visual? true
             :textual? false
             :arduino? false}
   ; NOTE(Richo): Merged with ev3 into just :lego
   #_(:nxt-g {:concurrency? true
              :liveness? false
              :debugging? false
              :monitoring? true
              :autonomy? true})
   :lego {:name "LEGO Mindstorms"
          :concurrency? true
          :liveness? false
          :debugging? false
          :monitoring? true
          :autonomy? true
          :visual? true
          :textual? false
          :arduino? false}
   ; NOTE(Richo): Lo descarto porque no es específica para robótica educativa
   #_(:vedils {:name "VEDILS authoring tool"
               :concurrency? true
               :liveness? false
               :debugging? false
               :monitoring? true
               :autonomy? false
               :visual? true
               :textual? false
               :arduino? false})
   :picaxe {:name "PICAXE Programming Editor"
            :concurrency? false
            :liveness? false
            :debugging? false
            :monitoring? false
            :autonomy? true
            :visual? true
            :textual? true
            :arduino? false}
   #_(:python {:concurrency? true
               :liveness? false
               :debugging? false
               :monitoring? false
               :autonomy? true})
   :tern {:name "Tern"
          :concurrency? false
          :liveness? false
          :debugging? false
          :monitoring? false
          :autonomy? true
          :visual? true
          :textual? false
          :arduino? false}
   :bipes {:name "BIPES"
           :concurrency? false
           :liveness? false
           :debugging? false
           :monitoring? true
           :autonomy? true
           :visual? true
           :textual? false
           :arduino? false}
   #_(:cpp {:concurrency? nil
            :liveness? nil
            :debugging? nil
            :monitoring? nil
            :autonomy? nil})
   :zr {:name "ZR graphical editor"
        :concurrency? false
        :liveness? false
        :debugging? false
        :monitoring? false
        :autonomy? true
        :visual? true
        :textual? false
        :arduino? false}
   ;; NOTE(Richo): Lo saco porque no es específico de robótica
   #_(:blockly  {:concurrency? false
                 :liveness? false
                 :debugging? false
                 :monitoring? false
                 :autonomy? true})
   :labview {:name "LabView"
             :concurrency? true
             :liveness? false
             :debugging? true ; NOTE(Richo): Según la wiki de labview (https://labviewwiki.org/wiki/Breakpoints)
             :monitoring? true
             :autonomy? true
             :visual? true
             :textual? false
             :arduino? true}
   :eud-mars {:name "EUD-MARS"
              :concurrency? true
              :liveness? false
              :debugging? false
              :monitoring? false
              :autonomy? false
              :visual? true
              :textual? false
              :arduino? false}
   :cherp {:name "CHERP"
           :concurrency? false
           :liveness? false
           :debugging? false
           :monitoring? false
           :autonomy? true
           :visual? true
           :textual? false
           :arduino? false}
   :robotc {:name "RobotC"
            :concurrency? true
            :liveness? false
            :debugging? false
            :monitoring? false
            :autonomy? true
            :visual? false
            :textual? true
            :arduino? true}
   ;; NOTE(Richo): No es robótica educativa
   #_(:bluej {:name "BlueJ"
              :concurrency? true
              :liveness? false
              :debugging? false
              :monitoring? false
              :autonomy? true
              :visual? true
              :textual? true
              :arduino? false})
   :scratch {:name "Scratch/Snap/S4A"
             :concurrency? true
             :liveness? true
             :debugging? false
             :monitoring? true
             :autonomy? false
             :visual? true
             :textual? false
             :arduino? true}
   ; NOTE(Richo): Los siguientes los agrego a pesar de que no hayan saltado en el mapeo
   :microblocks {:name "MicroBlocks"
                 :concurrency? true
                 :liveness? true
                 :debugging? false
                 :monitoring? true
                 :autonomy? true
                 :visual? true
                 :textual? false
                 :arduino? false}
   :xod {:name "XOD"
         :concurrency? true
         :liveness? false
         :debugging? false
         :monitoring? true
         :autonomy? true
         :visual? true
         :textual? false
         :arduino? true}
   :snek {:name "Snek"
          :concurrency? false
          :liveness? true
          :debugging? false
          :monitoring? true
          :autonomy? true
          :visual? false
          :textual? true
          :arduino? true}})

(def arduino-robots
  (conj (set (map first (filter (fn [[key val]]
                                  (= :arduino (:type val)))
                                robots)))
        :arduino))

(defn age-set
  ([] #{})
  ([begin] #{begin})
  ([begin end] (set (range begin (inc end)))))


(def papers
  (mapv #(assoc % :age-set (apply age-set (:ages %)))
        [{:id 1 :doi "10.5755/j01.eee.18.9.2825" :year 2012
          :ages [16 17]
          :tools [:robotc]
          :robots [:nxt]}
         {:id 2 :doi "10.1007/s00779-011-0404-2" :year 2012
          :ages [4 7]
          :tools [:tern]
          :robots [:rcx]}
         {:id 3 :doi "10.1109/TLT.2011.28" :year 2012
          :ages [11 13]
          :tools [:nqc]
          :robots [:nxt]}
         {:id 4 :doi "10.1109/TE.2012.2190071" :year 2012
          :ages [6 7]
          :tools [:tangible]
          :robots [:custom]}
         {:id 5 :doi "10.1080/08993408.2013.847152" :year 2013
          :ages [9 13]
          :tools [:lego]
          :robots [:nxt]}
         {:id 6 :doi "10.1080/08993408.2013.847165" :year 2013
          :ages [14 15]
          :tools [:robotc]
          :robots [:vex]}
         {:id 7 :doi "10.1007/s13384-013-0094-z" :year 2013
          :ages [8 9]
          :tools [:lego]
          :robots [:nxt]}
         {:id 8 :doi "10.1109/TLA.2013.6502866" :year 2013
          :ages [18]
          :tools [:pynxc]
          :robots [:nxt]}
         {:id 9 :doi "10.1080/15391523.2013.10782614" :year 2013
          :ages [4 6]
          :tools [:cherp]
          :robots [:lego]}
         {:id 10 :doi "10.1016/j.actaastro.2012.09.006" :year 2013
          :ages [13 17]
          :tools [:cpp :zr]
          :robots [:spheres]}
         {:id 11 :doi "10.1016/j.compedu.2013.10.020" :year 2014
          :ages [5 6]
          :tools [:cherp]
          :robots [:lego]}
         {:id 12 :doi "10.5772/58249" :year 2014
          :ages [18]
          :tools [:labview :robolab :lego :robotc :nqc]
          :robots [:controllab :rcx :nxt :ev3]}
         {:id 13 :doi "10.5755/j01.eee.20.1.6169" :year 2014
          :ages [18]
          :tools [:lego :mvpl :arduino-c]
          :robots [:nxt :4wd :5la]}
         {:id 14 :doi "10.3233/TAD-140404" :year 2014
          :ages [12 13]
          :tools [:robolab]
          :robots [:rcx]}
         {:id 15 :doi "10.1007/s10798-014-9287-7" :year 2015
          :ages [5 6]
          :tools [:cherp]
          :robots [:wedo]}
         {:id 16 :doi "10.1109/RITA.2015.2452692" :year 2015
          :ages [4 6]
          :tools [:modebots]
          :robots [:nxt]}
         {:id 17 :doi "10.1016/j.cag.2015.04.008" :year 2015
          :ages [18]
          :tools [:arduino-c]
          :robots [:arduino]}
         {:id 18 :doi "10.17485/ijst/2015/v8i26/80723" :year 2015
          :ages [8 13]
          :tools [:scratch]
          :robots [:korean]}
         {:id 19 :doi "10.1007/s00779-014-0774-3" :year 2015
          :ages [6 12]
          :tools [:proteas]
          :robots [:nxt]}
         {:id 20 :doi "10.1109/MRA.2016.2533002" :year 2016
          :ages [18]
          :tools [:labview]
          :robots [:dani]}
         {:id 21 :doi "10.1007/s10798-015-9304-5" :year 2016
          :ages [4 7]
          :tools [:cherp]
          :robots [:kiwi]}
         {:id 22 :doi "10.1155/2016/1714350" :year 2016
          :ages [4 6]
          :tools [:titibots]
          :robots [:nxt]}
         {:id 23 :doi "10.20965/jrm.2017.p0980" :year 2017
          :ages [5 13]
          :tools [:beebot]
          :robots [:custom]}
         {:id 24 :doi "10.1166/asl.2017.10252" :year 2017
          :ages [3 5]
          :tools [:tangible]
          :robots [:c-block]}
         {:id 25 :doi "10.1109/TLT.2016.2627565" :year 2017
          :ages [17 24]
          :tools [:arduino-c]
          :robots [:arduino-mega]}
         {:id 26 :doi "10.1016/j.compedu.2017.03.001" :year 2017
          :ages [10 11]
          :tools [:choregraphe]
          :robots [:nao]}
         {:id 27 :doi "10.1016/j.chb.2017.01.018" :year 2017
          :ages [5 6]
          :tools [:beebot]
          :robots [:beebot]}
         {:id 28 :doi "10.1109/RITA.2017.2697739" :year 2017
          :ages [15 17]
          :tools [:lego]
          :robots [:nxt]}
         {:id 29 :doi "10.1145/3025013" :year 2017
          :ages [12 13]
          :tools [:enchanting :modkit]
          :robots [:nxt :arduino-lilypad]}
         {:id 30 :doi "10.1109/TE.2016.2622227" :year 2017
          :ages [10 12]
          :tools [:tangible]
          :robots [:custom]}
         {:id 31 :doi "10.1145/3043950" :year 2017
          :ages [10 14]
          :tools [:scratch]
          :robots [:arduino-nano]}
         {:id 32 :doi "10.1109/MRA.2016.2636372" :year 2017
          :ages nil
          :tools [:aseba :thymio-vpl :blockly]
          :robots [:thymio]}
         {:id 33 :doi "10.13187/ejced.2017.3.390" :year 2017
          :ages [18]
          :tools [:lego :nqc]
          :robots [:nxt]}
         {:id 34 :doi "10.1007/978-3-319-55553-9_15" :year 2017
          :ages [15 16]
          :tools [:appinventor]
          :robots [:nxt]}
         {:id 35 :doi "10.1007/978-3-319-55553-9_6" :year 2017
          :ages [18]
          :tools [:aseba]
          :robots [:thymio]}
         {:id 36 :doi "10.1007/978-3-319-55553-9_14" :year 2017
          :ages [9 12]
          :tools [:scratch]
          :robots [:wedo]}
         {:id 37 :doi "10.1007/978-3-319-55553-9_7" :year 2017
          :ages [11 12]
          :tools [:picaxe]
          :robots [:basic]}
         {:id 38 :doi "10.1007/978-3-319-55553-9_17" :year 2017
          :ages [14 15]
          :tools [:lego]
          :robots [:nxt]}
         {:id 39 :doi "10.1007/978-3-319-55553-9_22" :year 2017
          :ages [10 12]
          :tools [:wedo]
          :robots [:wedo]}
         {:id 40 :doi "10.1007/978-3-319-55553-9_13" :year 2017
          :ages [10 12]
          :tools [:wedo]
          :robots [:wedo]}
         {:id 41 :doi "10.1007/978-3-319-55553-9_2" :year 2017
          :ages [8 12]
          :tools [:scratch]
          :robots [:arduino]}
         {:id 42 :doi "10.1016/j.ijcci.2018.03.004" :year 2018
          :ages [12 14]
          :tools [:blocklytalky]
          :robots [:raspberry]}
         {:id 43 :doi "10.1002/cae.21966" :year 2018
          :ages [18]
          :tools [:crumble :arduino-c]
          :robots [:crumble :arduino]}
         {:id 44 :doi "10.1016/j.ijcci.2018.03.002" :year 2018
          :ages [14 18]
          :tools [:talkoo]
          :robots [:talkoo]}
         {:id 45 :doi "10.1007/s10639-017-9677-z" :year 2018
          :ages [9 11]
          :tools [:beebot]
          :robots [:roamer]}
         {:id 46 :doi "10.1145/3211332.3211335" :year 2018
          :ages nil
          :tools [:makecode]
          :robots [:microbit]}
         {:id 47 :doi "10.1049/trit.2018.0016" :year 2018
          :ages [14 18]
          :tools [:viple]
          :robots [:ev3 :galileo :raspberry :pcDuino :minnow :curie :edison :bioloid :arduino-uno :arduino-mega :arduino-duo]}
         {:id 48 :doi "10.1007/s10798-017-9397-0" :year 2018
          :ages [3 6]
          :tools [:tangible]
          :robots [:kibo]}
         {:id 49 :doi "10.1515/itit-2017-0032" :year 2018
          :ages [12 16]
          :tools [:scratch]
          :robots [:arduino]}
         {:id 50 :doi "10.1016/j.chb.2017.09.029" :year 2018
          :ages [16 18]
          :tools [:phogo]
          :robots [:bqzum :esp8266 :esp32]}
         {:id 51 :doi "10.1109/RITA.2018.2801898" :year 2018
          :ages [18]
          :tools [:bluej]
          :robots [:ev3]}
         {:id 52 :doi "10.29333/ejmste/93483" :year 2018
          :ages [10 11]
          :tools [:mblock]
          :robots [:mbot]}
         {:id 53 :doi "10.18355/XL.2018.11.01.10" :year 2018
          :ages [6 10]
          :tools [:scratch]
          :robots [:robbo]}
         {:id 54 :doi "10.1007/s11423-019-09648-5" :year 2019
          :ages [11 12]
          :tools [:mblock]
          :robots [:mbot]}
         {:id 55 :doi "10.1007/s40692-019-00147-3" :year 2019
          :ages [5 7]
          :tools [:tangible]
          :robots [:kibo]}
         {:id 56 :doi "10.1109/RITA.2019.2950130" :year 2019
          :ages [6 11]
          :tools [:crumble :arduino-c]
          :robots [:crumble :arduino]}
         {:id 57 :doi "10.1080/1475939X.2019.1670248" :year 2019
          :ages [6 10]
          :tools [:choregraphe :python]
          :robots [:nao]}
         {:id 58 :doi "10.1080/07380569.2019.1677436" :year 2019
          :ages [3 4]
          :tools [:beebot]
          :robots [:beebot]}
         {:id 59 :doi "10.3390/informatics6040043" :year 2019
          :ages [8 10]
          :tools [:wedo]
          :robots [:wedo]}
         {:id 60 :doi "10.1007/s40299-019-00438-4" :year 2019
          :ages [5 6]
          :tools [:tangible]
          :robots [:turtlebot]}
         {:id 61 :doi "10.3390/educsci9030224" :year 2019
          :ages [18]
          :tools [:appinventor :cpp :python]
          :robots [:custom]}
         {:id 62 :doi "10.18178/ijmerr.8.5.764-770" :year 2019
          :ages [12 14]
          :tools [:scratch]
          :robots [:microrobots :nxt]}
         {:id 63 :doi "10.1016/j.sysarc.2019.05.005" :year 2019
          :ages [16 18]
          :tools [:makecode]
          :robots [:microbit]}
         {:id 64 :doi "10.1108/JET-12-2018-0069" :year 2019
          :ages [10 11]
          :tools [:kinderbot]
          :robots [:ev3]}
         {:id 65 :doi "10.3390/electronics8080899" :year 2019
          :ages [12 16]
          :tools [:pybokids]
          :robots [:mbot]}
         {:id 66 :doi "10.1145/3336126" :year 2019
          :ages [14 15]
          :tools [:appinventor :scratchx]
          :robots [:nxt]}
         {:id 67 :doi "10.20368/1971-8829/1625" :year 2019
          :ages [18]
          :tools [:cpp]
          :robots [:ev3 :mbot]}
         {:id 68 :doi "10.17275/per.19.8.6.2" :year 2019
          :ages [11 12]
          :tools [:scratch]
          :robots [:arduino-uno ; No especifican exactamente qué versión de Arduino, 
                                ; pero la UNO es la más común así que asumo que es esta
                   ]}
         {:id 69 :doi "10.14201/eks2019_20_a17" :year 2019
          :ages [3 6]
          :tools [:beebot :tangible]
          :robots [:kibo :beebot :bluebot :roamer :cubetto :codeapillar]}
         {:id 70 :doi "10.1109/ACCESS.2019.2895913" :year 2019
          :ages [18]
          :tools [:sphero-oop :sphero-edu :vedils]
          :robots [:sphero]}
         {:id 71 :doi "10.3390/educsci10120387" :year 2020
          :ages [6 10]
          :tools [:scratch]
          :robots [:geombot]}
         {:id 72 :doi "10.1016/j.tsc.2020.100714" :year 2020
          :ages [11 12]
          :tools [:lego]
          :robots [:ev3]}
         {:id 73 :doi "10.1016/j.scico.2020.102534" :year 2020
          :ages [8 17]
          :tools [:eud-mars]
          :robots [:lego :vex :parrot :nao :irobot]}
         {:id 74 :doi "10.1080/20473869.2019.1565725" :year 2020
          :ages [8 9]
          :tools [:lego]
          :robots [:ev3]}
         {:id 75 :doi "10.1080/15391523.2020.1713263" :year 2020
          :ages [8 10]
          :tools [:lego]
          :robots [:nxt :ev3]}
         {:id 76 :doi "10.3389/frobt.2020.00021" :year 2020
          :ages [9 18]
          :tools [:lego]
          :robots [:ev3]}
         {:id 77 :doi "10.3389/fpsyg.2019.02813" :year 2020
          :ages [5 7]
          :tools [:beebot]
          :robots [:beebot]}
         {:id 78 :doi "10.3991/ijoe.v16i14.17069" :year 2020
          :ages [11 14]
          :tools [:makecode]
          :robots [:ev3]}
         {:id 79 :doi "10.1109/ACCESS.2020.3035083" :year 2020
          :ages nil
          :tools [:bipes]
          :robots [:stm32 :wemosd1mini :esp8266 :esp32 :microbit
                   :toradex :pc104 :beaglebone :raspberry]}
         {:id 80 :doi "10.1109/ACCESS.2020.3015533" :year 2020
          :ages [4 8]
          :tools [:blockly]
          :robots [:cozmo]}
         {:id 81 :doi "10.3390/educsci10080202" :year 2020
          :ages [7 8]
          :tools [:beebot]
          :robots [:beebot]}
         {:id 82 :doi "10.1109/ACCESS.2020.2972410" :year 2020
          :ages nil
          :tools [:learnblock]
          :robots []}
         {:id 83 :doi "10.1002/cae.22184" :year 2020
          :ages [10 18]
          :tools [:arduino-c]
          :robots [:arduino]}
         {:id 84 :doi "10.3390/electronics10243056" :year 2021
          :ages [9 12]
          :tools [:ardublockly]
          :robots [:arduino-mega]}
         {:id 85 :doi "10.1007/s10758-021-09508-3" :year 2021
          :ages [15 19]
          :tools [:blockly :python]
          :robots [:arduino-mega :raspberry]}
         {:id 86 :doi "10.1111/jcal.12570" :year 2021
          :ages [10 12]
          :tools [:blockly]
          :robots [:arduino]}
         {:id 87 :doi "10.3390/s21186243" :year 2021
          :ages [11 13]
          :tools [:makecode]
          :robots [:microbit]}
         {:id 88 :doi "10.3390/educsci11090518" :year 2021
          :ages [6 9]
          :tools [:beebot]
          :robots [:doc]}
         {:id 89 :doi "10.1016/j.compedu.2021.104222" :year 2021
          :ages [5 9]
          :tools [:tangible]
          :robots [:kibo]}
         {:id 90 :doi "10.1109/RITA.2021.3089919" :year 2021
          :ages [6 18]
          :tools [:arduino-c]
          :robots [:arduino-uno]}
         {:id 91 :doi "10.1111/bjet.13079" :year 2021
          :ages [14 15]
          :tools [:arduino-c]
          :robots [:arduino-lilypad]}
         {:id 92 :doi "10.1007/s10798-019-09559-9" :year 2021
          :ages [9 10]
          :tools [:lego]
          :robots [:nxt]}
         {:id 93 :doi "10.1109/TLT.2021.3058060" :year 2021
          :ages [4 10]
          :tools [:beebot]
          :robots [:beebot]}
         {:id 94 :doi "10.46328/IJEMST.1205" :year 2021
          :ages [9 10]
          :tools [:sphero-edu]
          :robots [:sphero]}
         {:id 95 :doi "10.29333/ejmste/10842" :year 2021
          :ages [10 11]
          :tools [:scratch]
          :robots [:arduino]}
         {:id 96 :doi "10.1080/09669760.2021.1892599" :year 2021
          :ages [5 6]
          :tools [:beebot]
          :robots [:bluebot]}
         ]))

(do ; Verify papers
  (when-not (= (set (conj (keys robots) :arduino :lego))
               (set (mapcat :robots papers)))
    (println "Inconsistency in the robots!"))
  (when-not (= (set (keys tools))
               (conj (set (mapcat :tools papers)) :microblocks :snek :xod))
    (println "Inconsistency in the tools!"))
  (doseq [paper papers]
    (if (empty? (:tools paper))
      (println "Paper" (:id paper) "has NO tools!")
      (doseq [tool (:tools paper)]
        (when-not (contains? tools tool)
          (println "Paper" (:id paper) "has invalid tool" tool))))
    (if (and (not= 82 (:id paper))
             (empty? (:robots paper)))
      (println "Paper" (:id paper) "has NO robots!")
      (let [robots (conj (set (keys robots))
                         :arduino :lego)]
        (doseq [robot (:robots paper)]
          (when-not (contains? robots robot)
            (println "Paper" (:id paper) "has invalid robot" robot)))))))

(def total-papers-by-year
  {2012 207
   2013 183
   2014 237
   2015 300
   2016 309
   2017 379
   2018 415
   2019 557
   2020 537
   2021 635})

(defn tool-types-by-age []
  (mapcat (fn [age]
            (let [valid-papers (filter (fn [{:keys [age-set]}]
                                         (contains? age-set age))
                                       papers)
                  tangible? (comp :tangible? tools)
                  visual? (comp :visual? tools)
                  textual? (comp :textual? tools)
                  age-str (if (= 18 age) "18+" (str age))]
              [{:age age-str
                :type "Tangible (+BeeBot)"
                :count (count (filter (fn [paper]
                                        (some tangible? (:tools paper)))
                                      valid-papers))}
               {:age age-str
                :type "Visual"
                :count (count (filter (fn [paper]
                                        (some visual? (:tools paper)))
                                      valid-papers))}
               {:age age-str
                :type "Textual"
                :count (count (filter (fn [paper]
                                        (some textual? (:tools paper)))
                                      valid-papers))}]))
          (range 3 19)))

(defn papers-by-age []
  (map (fn [age]
         {:age (if (= 18 age) "18+" (str age))
          :count (count (filter (fn [{:keys [age-set]}]
                                  (contains? age-set age))
                                papers))})
       (range 3 19)))

(defn tool-types []
  (map (fn [{:keys [name tangible? visual? textual?]}]
         {:name name
          :type (cond
                  (and visual? textual?) :hybrid
                  visual? :visual
                  textual? :textual
                  tangible? :tangible)})
       (vals tools)
       #_(mapcat #(map tools (:tools %)) papers)))

(defn papers-by-robot-type []
  (frequencies (mapcat (fn [{paper-robots :robots}]
                         (map (fn [key]
                                (if-let [robot (robots key)]
                                  (:type robot)
                                  key))
                              paper-robots))
                       papers)))

(defn row [& content]
  [:div {:style {:display "flex" :flex-wrap "wrap"}}
   content])

(defn table [& content]
  (let [attrs (first content)
        base-attrs {:style {:border "solid"
                            :border-collapse "collapse"}}]
    (if (map? attrs)
      [:table (merge-with merge base-attrs attrs) (next content)]
      [:table base-attrs content])))

(defn th [& content]
  (let [attrs (first content)
        base-attrs {:style {:border-right "1px solid black"
                            :border-bottom "1px solid black"
                            :border-left "1px solid black"
                            :border-top "1px solid black"
                            :border-collapse "collapse"}}]
    (if (map? attrs)
      [:th (merge-with merge base-attrs attrs) (next content)]
      [:th base-attrs content])))

(defn td [& content]
  (let [attrs (first content)
        base-attrs {:style {:border-right "1px solid black"
                            ;:border-bottom "1px solid black"
                            :border-left "1px solid black"
                            ;:border-top "1px solid black"
                            :border-collapse "collapse"}}]
    (if (map? attrs)
      [:td (merge-with merge base-attrs attrs) (next content)]
      [:td base-attrs content])))


(defn markdown [& texts]
  (m/component (m/md->hiccup (str/join "\r\n" texts))))

(defn percent [n]
  (str (format "%.1f" (* 100.0 n)) "%"))

(defn generate-vega-doc []
  [:div
   [:h2 {:style {:margin-top 0}}
    "Resultados"]
   [:h4 "¿Cuántos artículos por año?"]
   (row
    [:vega-lite {:data {:values (let [years (set (map :year papers))
                                      begin (apply min years)
                                      end (apply max years)
                                      relevant-papers-by-year (->> papers
                                                                   (group-by :year)
                                                                   (map (fn [[k v]] [k (count v)]))
                                                                   (into {}))]
                                  (map (fn [year]
                                         {:year year :type "Relevantes" :value (relevant-papers-by-year year)})
                                       (range begin (inc end))))}
                 :encoding {:x {:field :year
                                :title "Año"
                                :type "ordinal"}
                            :y {:field "value"
                                :title "Cantidad de artículos"
                                ;:scale {:type "log"}
                                :type "quantitative"}
                            ;:color {:field :type}
                            }
                 :layer [{:mark {:type :line :point true :tooltip true}}]}]
    [:vega-lite {:data {:values (let [years (set (map :year papers))
                                      begin (apply min years)
                                      end (apply max years)
                                      relevant-papers-by-year (->> papers
                                                                   (group-by :year)
                                                                   (map (fn [[k v]] [k (count v)]))
                                                                   (into {}))]
                                  (map (fn [year]
                                         {:year year ;:type "Proporción"
                                          :value (/ (relevant-papers-by-year year)
                                                    (total-papers-by-year year)
                                                    1.0)})
                                       (range begin (inc end))))}
                 :encoding {:x {:field :year
                                :title "Año"
                                :type "ordinal"}
                            :y {:field "value"
                                :title "Artículos relevantes sobre el total"
                                :axis {:format "%"}
                                :type "quantitative"}
                            ;:color {:field :type}
                            }
                 :layer [{:mark {:type :line :point true :tooltip true}}]}]
    [:vega-lite {:data {:values (let [years (set (map :year papers))
                                      begin (apply min years)
                                      end (apply max years)
                                      relevant-papers-by-year (->> papers
                                                                   (group-by :year)
                                                                   (map (fn [[k v]] [k (count v)]))
                                                                   (into {}))]
                                  (mapcat (fn [year]
                                            [{:year year :type "Total" :value (total-papers-by-year year)}
                                             {:year year :type "Relevantes" :value (relevant-papers-by-year year)}])
                                          (range begin (inc end)))
                                  #_(map (fn [year]
                                           {:year year ;:type "Proporción"
                                            :value (/ (relevant-papers-by-year year)
                                                      (total-papers-by-year year)
                                                      1.0)})
                                         (range begin (inc end))))}
                 :encoding {:x {:field :year
                                :title "Año"
                                :type "ordinal"}
                            :y {:field "value"
                                :title "Cantidad de artículos"
                                :scale {:type "log"}
                                :type "quantitative"}
                            :color {:field :type}}
                 :layer [{:mark {:type :line :point true :tooltip true}}]}]
    [:vega-lite {:data {:values (let [years (set (map :year papers))
                                      begin (apply min years)
                                      end (apply max years)
                                      relevant-papers-by-year (->> papers
                                                                   (group-by :year)
                                                                   (map (fn [[k v]] [k (count v)]))
                                                                   (into {}))]
                                  (map (fn [year]
                                         {:year year
                                          :relevant (relevant-papers-by-year year)
                                          :total (total-papers-by-year year)})
                                       (range begin (inc end))))}
                 :encoding {:x {:field :year
                                :title "Año"
                                :type "ordinal"}}
                 :layer [{:mark {:type :line :tooltip true
                                 :color "#1F77B4"}
                          :encoding {:y {:field :relevant
                                         :title "Artículos relevantes"
                                         :type "quantitative"
                                         :axis {:titleColor "#1F77B4"}}}}
                         {:mark {:type :line :tooltip true
                                 :color "#FF7F0E"}
                          :encoding {:y {:field :total
                                         :title "Artículos totales"
                                         :type "quantitative"
                                         :axis {:titleColor "#FF7F0E"}}}}]
                 :resolve {:scale {:y "independent"}}}])

   [:h4 "¿Cuántos artículos por edad de alumno?"]
   (row
    [:vega-lite {:data {:values (papers-by-age)}
                 :transform [{:joinaggregate [{:op "mean"
                                               :field :count
                                               :as "average-count"}]}]
                 :layer [{:mark {:type :bar :point true :tooltip true}
                          :encoding {:x {:field :age
                                         :title "Edad"
                                         :type "ordinal"
                                         :sort (conj (mapv str (range 3 18))
                                                     "18+")}
                                     :y {:field :count
                                         :title "Cantidad de artículos"
                                         :type "quantitative"}}}
                         {:mark {:type :rule :color "red"}
                          :encoding {:y {:aggregate "average"
                                         :field "average-count"
                                         :type "quantitative"}}}]}])

   [:h4 "¿Qué tipos de herramientas se usan a cada edad?"]
   (row
    [:vega-lite {:data {:values (tool-types-by-age)}
                 :encoding {:x {:field :age
                                :title "Edad"
                                :type "ordinal"
                                :sort (conj (mapv str (range 3 18))
                                            "18+")}
                            :y {:field :count
                                :title "Proporción de artículos"
                                :axis {:format "%"}
                                :stack "normalize"
                                :type "quantitative"}
                            :color {:field :type
                                    :title "Tipo de herramienta"}}
                 :layer [{:mark {:type :bar :point true :tooltip true}}]}])

   [:h4 "¿Qué características tienen las herramientas relevadas?"]
   (row [:vega-lite {:data {:values (let [data (->> (tool-types)
                                                    (remove #(= :tangible (:type %)))
                                                    (group-by :type)
                                                    (map (fn [[type tools]]
                                                           [type (count tools)]))
                                                    (into {}))
                                          total (reduce + (vals data))]
                                      (map (fn [[type count]]
                                             {:type (case type
                                                      :hybrid "Híbrida (visual / textual)"
                                                      :visual "Visual"
                                                      :textual "Textual")
                                              :count count
                                              :text (percent (/ count total))})
                                           data))}
                     :title "Todas las herramientas (excluyendo tangibles)"
                     :encoding {:theta {:field :count
                                        :type "quantitative"
                                        :stack "normalize"}
                                :order {:field :count
                                        :type "quantitative"
                                        :sort "descending"}
                                :color {:field :type
                                        :title "Tipo de herramienta"
                                        :sort {:field :count
                                               :order "descending"}}
                                :text {:field :text :type "nominal"}}
                     :layer [{:mark {:type :arc :innerRadius 50 :point true :tooltip true}}
                             {:mark {:type :text :radius 75 :fill "black"}}]}]
        #_[:vega-lite {:data {:values (let [data (->> (vals tools)
                                                      (map #(assoc % :dsl?
                                                                   (boolean
                                                                    (or (:visual-dsl? %)
                                                                        (:textual-dsl? %)))))
                                                      (group-by :dsl?)
                                                      (map (fn [[dsl? tools]]
                                                             [dsl? (count tools)]))
                                                      (into {}))
                                            total (reduce + (vals data))]
                                        (map (fn [[dsl? count]]
                                               {:type (if dsl?
                                                        "Dominio específico"
                                                        "Propósito general")
                                                :count count
                                                :text (percent (/ count total))})
                                             data))}
                       :title "Todas las herramientas"
                       :encoding {:theta {:field :count
                                          :type "quantitative"
                                          :stack "normalize"}
                                  :order {:field :count
                                          :type "quantitative"
                                          :sort "descending"}
                                  :color {:field :type
                                          :title "Tipo de herramienta"
                                          :sort {:field :count
                                                 :order "descending"}}
                                  :text {:field :text :type "nominal"}}
                       :layer [{:mark {:type :arc :innerRadius 50 :point true :tooltip true}}
                               {:mark {:type :text :radius 75 :fill "black"}}]}])

   [:h4 "¿Qué tipos de lenguajes visuales soportan los entornos relevados?"]
   (row [:vega-lite {:data {:values (let [data (->> (vals tools)
                                                    (filter :visual?)
                                                    (group-by :visual-type)
                                                    (map (fn [[type tools]]
                                                           [type (count tools)]))
                                                    (into {}))
                                          total (reduce + (vals data))]
                                      (map (fn [[type count]]
                                             {:type (case type
                                                      :blocks "Bloques"
                                                      :diagram "Diagrama"
                                                      :icons "Iconográfico"
                                                      :form "Formulario")
                                              :count count
                                              :text (percent (/ count total))})
                                           data))}
                     :title "Lenguajes visuales"
                     :encoding {:theta {:field :count
                                        :type "quantitative"
                                        :stack "normalize"}
                                :order {:field :count
                                        :type "quantitative"
                                        :sort "descending"}
                                :color {:field :type
                                        :title "Tipo de herramienta"
                                        :sort {:field :count
                                               :order "descending"}}
                                :text {:field :text :type "nominal"}}
                     :layer [{:mark {:type :arc :innerRadius 50 :point true :tooltip true}}
                             {:mark {:type :text :radius 75 :fill "black"}}]}])

   [:h4 "¿Qué tipos de lenguajes textuales soportan los entornos relevados?"]
   (row [:vega-lite {:data {:values (let [data (->> (vals tools)
                                                    (filter :textual?)
                                                    (map #(assoc % :textual-type
                                                                 (case (:textual-type %)
                                                                   :aseba "Aseba"
                                                                   :basic "BASIC"
                                                                   :python "Python"
                                                                   :cpp "C/C++"
                                                                   :csharp "C#"
                                                                   :java "Java"
                                                                   :js "Javascript")))
                                                    (group-by :textual-type)
                                                    (map (fn [[type tools]]
                                                           [type (count tools)]))
                                                    (into {}))
                                          total (reduce + (vals data))]
                                      (map (fn [[type count]]
                                             {:type type
                                              :count count
                                              :sort (if (= "Otros" type)
                                                      (* -1 count)
                                                      count)
                                              :text (percent (/ count total))})
                                           data))}
                     :title "Lenguajes textuales"
                     :encoding {:theta {:field :count
                                        :type "quantitative"
                                        :stack "normalize"}
                                :order {:field :sort
                                        :type "quantitative"
                                        :sort "descending"}
                                :color {:field :type
                                        :title "Lenguaje"
                                        :sort {:field :sort
                                               :order "descending"}}
                                :text {:field :text :type "nominal"}}
                     :layer [{:mark {:type :arc :innerRadius 50 :point true :tooltip true}}
                             {:mark {:type :text :radius 75 :fill "black"}}]}]

        [:vega-lite {:data {:values (let [data (->> (vals tools)
                                                    (filter :textual?)
                                                    (map #(assoc % :textual-type
                                                                 (condp contains? (:textual-type %)
                                                                   #{:cpp :java :csharp :js} "Estilo C"
                                                                   #{:python} "Python"
                                                                   #{:aseba :basic} "Otros"
                                                                   (:textual-type %))))
                                                    (group-by :textual-type)
                                                    (map (fn [[type tools]]
                                                           [type (count tools)]))
                                                    (into {}))
                                          total (reduce + (vals data))]
                                      (map (fn [[type count]]
                                             {:type type
                                              :count count
                                              :sort count
                                              :text (percent (/ count total))})
                                           data))}
                     :title "Lenguajes textuales"
                     :encoding {:theta {:field :count
                                        :type "quantitative"
                                        :stack "normalize"}
                                :order {:field :sort
                                        :type "quantitative"
                                        :sort "descending"}
                                :color {:field :type
                                        :title "Tipo de sintaxis"
                                        :sort {:field :sort
                                               :order "descending"}}
                                :text {:field :text :type "nominal"}}
                     :layer [{:mark {:type :arc :innerRadius 50 :point true :tooltip true}}
                             {:mark {:type :text :radius 75 :fill "black"}}]}])

   [:h4 "¿Qué plataformas de hardware se utilizaron en los estudios relevados?"]
   (row [:vega-lite {:data {:values (let [data (->> (vals robots)
                                                    (map #(assoc % :type
                                                                 (condp contains? (:type %)
                                                                   #{:vex :robotic-arm :drone
                                                                     :vacuum-cleaner :humanoid} :other
                                                                   #{:arduino :microcontroller} :arduino
                                                                   #{:kit :lego} :kit
                                                                   (:type %))))
                                                    (group-by :type)
                                                    (map (fn [[type tools]]
                                                           [type (count tools)]))
                                                    (into {}))
                                          total (reduce + (vals data))]
                                      (map (fn [[type count]]
                                             {:type (case type
                                                      :toy "Juguete robot"
                                                      :sbc "Single-Board Computer"
                                                      :arduino "Placa Arduino (y similares)"
                                                      :kit "Kit de robótica (LEGO y otros)"
                                                      :custom "Robot personalizado"
                                                      :other "Otros robots")
                                              :count count
                                              :sort (if (= type :other) -1 count)
                                              :text (percent (/ count total))})
                                           data))}
                     :title "Tipos de hardware"
                     :encoding {:theta {:field :count
                                        :type "quantitative"
                                        :stack "normalize"}
                                :order {:field :sort
                                        :type "quantitative"
                                        :sort "descending"}
                                :color {:field :type
                                        :title "Tipo de hardware"
                                        :sort {:field :sort
                                               :order "descending"}}
                                :text {:field :text :type "nominal"}}
                     :layer [{:mark {:type :arc :innerRadius 50 :point true :tooltip true}}
                             {:mark {:type :text :radius 75 :fill "black"}}]}]

        [:vega-lite {:data {:values (->> (reduce-kv (fn [m k v]
                                                      (let [k' (condp contains? k
                                                                 #{:vex :robotic-arm :drone
                                                                   :vacuum-cleaner :humanoid} :other
                                                                 #{:arduino :microcontroller} :arduino
                                                                 #{:kit :lego} :kit
                                                                 k)]
                                                        (if-let [v' (m k')]
                                                          (assoc m k' (+ v' v))
                                                          (assoc m k' v))))
                                                    {}
                                                    (papers-by-robot-type))
                                         (map (fn [[key count]]
                                                {:type (case key
                                                         :toy "Juguete robot"
                                                         :sbc "Single-Board Computer"
                                                         :arduino "Placa Arduino"
                                                         :kit "Kit de robótica"
                                                         :custom "Robot personalizado"
                                                         :other "Otros robots")
                                                 :count count})))}
                     :width 300
                     :encoding {:x {:field :type
                                    :title "Tipo de hardware"
                                    :type "ordinal"
                                    :axis {:labelAngle -30}
                                    :sort {:field :count :order "descending"}}
                                :y {:field :count
                                    :title "Cantidad de artículos"
                                    :type "quantitative"}}
                     :layer [{:mark {:type :bar :point true :tooltip true}}]}])

   [:h4 "¿Cómo se comparan los entornos relevados con Physical Bits?"]
   (row [:vega-lite (let [feature->text {:autonomy? "Autonomía"
                                         :concurrency? "Concurrencia"
                                         :debugging? "Depuración"
                                         :liveness? "Interactividad"
                                         :monitoring? "Monitoreo"}]
                      {:data {:values (let [features [:autonomy? :concurrency? :monitoring?
                                                      :liveness? :debugging?]
                                            total (count tool-features)]
                                        (for [i (range (count features)),
                                              j (range (count features))
                                              :when (>= i j)]
                                          (let [x (nth features i)
                                                y (nth features j)
                                                count (count (filter #(and (x %) (y %))
                                                                     (vals tool-features)))]
                                            {:x (feature->text x)
                                             :y (feature->text y)
                                             :keys (map first (filter #(and (x (second %))
                                                                            (y (second %)))
                                                                      tool-features))
                                             :percent (* 100.0 (/ count total))
                                             :text (percent (/ count total))
                                             :count count})))}
                       :width 300
                       :height 300
                       :encoding {:x {:field :x :type "ordinal"
                                      :title nil
                                      :sort (map feature->text
                                                 [:autonomy? :concurrency? :monitoring?
                                                  :liveness? :debugging?])}
                                  :y {:field :y :type "ordinal"
                                      :title nil
                                      :sort (map feature->text
                                                 (rseq [:autonomy? :concurrency? :monitoring?
                                                        :liveness? :debugging?]))}}
                       :layer [{:mark "rect"
                                :encoding {:color {:field :percent
                                                   :type "quantitative"
                                                   :title "%"
                                                   :legend {:direction "vertical"
                                                            :gradientLength 120}}}}
                               {:mark "text"
                                :encoding {:text {:field :text
                                                  :type "nominal"}
                                           :color {:condition {:test "datum['percent'] < 30"
                                                               :value "black"}
                                                   :value "white"}}}]})])

   (table {:style {:text-align "center" :border "solid" :border-collapse "collapse"}}
          [:tr {:style {:border-bottom "1px solid grey"}}
           (th "")
           (th "Herramienta")
           (th "A")
           (th "C")
           (th "M")
           (th "I")
           (th "D")
           ;(th "V")
           ;(th "T")
           (th "Tipo de lenguaje")]
          (map-indexed (fn [idx {:keys [name features type score]}]
                         (let [yes [:span {:style {:color "green"}} "✔️"]
                               no [:span {:style {:color "red"}} "❌"]
                               bool-symbol #(if % yes no)
                               {:keys [autonomy? concurrency? monitoring?
                                       liveness? debugging? arduino?]} features
                               td-style {:style {:background-color (if arduino? "bisque" "white")
                                                 :border-bottom "1px dashed grey"}}]
                           [:tr #_{:style {:border-bottom "1px dashed grey"}}
                            (td td-style (inc idx))
                            (td (assoc-in td-style [:style :text-align] "left")
                                name)
                            (td td-style (bool-symbol autonomy?))
                            (td td-style (bool-symbol concurrency?))
                            (td td-style (bool-symbol monitoring?))
                            (td td-style (bool-symbol liveness?))
                            (td td-style (bool-symbol debugging?))
                            #_(td td-style (bool-symbol visual?))
                            #_(td td-style (bool-symbol textual?))
                            (td td-style type)]))
                       (->> tool-features #_(assoc tool-features
                                                   :pbits {:name "Physical Bits"
                                                           :autonomy? true
                                                           :concurrency? true
                                                           :liveness? true
                                                           :debugging? true
                                                           :monitoring? true
                                                           :visual? true
                                                           :textual? true
                                                           :arduino? true})
                            (map (fn [[key {:keys [name] :as features}]]
                                   {:key key
                                    :name name
                                    :features features
                                    :score (reduce + (map #(if % 1 0)
                                                          (vals (select-keys features
                                                                             [:autonomy? :concurrency? :monitoring?
                                                                              :liveness? :debugging?]))))
                                    :type (if (= key :pbits)
                                            "Híbrido (Bloques - Otro)"
                                            (let [{:keys [visual? textual?
                                                          visual-type textual-type]}
                                                  (tools key)

                                                  textual-type (case textual-type
                                                                 :aseba "Aseba"
                                                                 :basic "BASIC"
                                                                 :python "Python"
                                                                 :cpp "C/C++"
                                                                 :csharp "C#"
                                                                 :java "Java"
                                                                 :js "Javascript"
                                                                 "Otro")
                                                  visual-type (case visual-type
                                                                :blocks "Bloques"
                                                                :diagram "Diagrama"
                                                                :icons "Iconográfico"
                                                                :form "Formulario"
                                                                "Otro")]
                                              (case (mapv boolean [visual? textual?])
                                                [true true] (str "Híbrido (" visual-type " - " textual-type ")")
                                                [true false] (str "Visual (" visual-type ")")
                                                [false true] (str "Textual (" textual-type ")")
                                                [false false] "Otro")))}))
                            #_(filter (comp :arduino? :features))
                            #_(remove (comp (set (keys (filter (comp :tangible? second) tools)))
                                            :key))
                            (sort-by :score)
                            (reverse))))

   [:p]

   (table {:style {:text-align "center" :border "solid" :border-collapse "collapse"}}
          [:tr {:style {:border-bottom "1px solid grey"}}
           (th "")
           (th "Herramientas")
           (th "A")
           (th "C")
           (th "M")
           (th "I")
           (th "D")]
          (map-indexed (fn [idx {:keys [tools features]}]
                         (let [yes [:span {:style {:color "green"}} "✔️"]
                               no [:span {:style {:color "red"}} "❌"]
                               bool-symbol #(if % yes no)
                               {:keys [autonomy? concurrency? monitoring?
                                       liveness? debugging? arduino?]} features
                               td-style {:style {:border-bottom "1px dashed grey"}}]
                           [:tr #_{:style {:border-bottom "1px dashed grey"}}
                            (td td-style (inc idx))
                            (td (assoc-in td-style [:style :text-align] "left")
                                (str/join ", " tools))
                            (td td-style (bool-symbol autonomy?))
                            (td td-style (bool-symbol concurrency?))
                            (td td-style (bool-symbol monitoring?))
                            (td td-style (bool-symbol liveness?))
                            (td td-style (bool-symbol debugging?))]))
                       (->> tool-features #_(assoc tool-features
                                                   :pbits {:name "Physical Bits"
                                                           :autonomy? true
                                                           :concurrency? true
                                                           :liveness? true
                                                           :debugging? true
                                                           :monitoring? true
                                                           :visual? true
                                                           :textual? true
                                                           :arduino? true})
                            (map (fn [[_ {:keys [name] :as features}]]
                                   {:name name
                                    :features (select-keys features [:autonomy? :concurrency? :monitoring?
                                                                     :liveness? :debugging?])}))
                            (group-by :features)
                            (map (fn [[features tools]]
                                   {:features features
                                    :score (reduce + (map #(if % 1 0) (vals features)))
                                    :count (count tools)
                                    :tools (map :name tools)}))
                            (sort-by :count)
                            (sort-by :score)
                            (reverse))))

   [:h4 "Artículos"]
   [:ol (map (fn [paper] [:li (:CITATION paper)])
             paper-data)]])


(defn redraw! []
  (println "REDRAW!")
  (oz/view! (generate-vega-doc)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  (redraw!))

(comment

  (redraw!)
  (oz/export! (generate-vega-doc) "index.html")
  
  )
