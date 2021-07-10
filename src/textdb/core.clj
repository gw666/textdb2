(ns textdb.core
  (:require [textdb.manip :as manip]
;            [clj-commons-exec :as exec]
            [clojure.pprint :as p]
  )
  (:gen-class)
)
  
(comment
  (def today "7/10"))  

(defn -main []

  (println "hello")
  
  (def currtexts-dir
    "/Users/gw/Documents/github/textdb/test/TESTDATA/test1/")
    
; To access the file named foo.md, you must concatenate the absolute path to the file
; (e.g., currtexts-dir, above) and the name of the file. For the above example,
; the absolute path is "/Users/gw/Documents/github/textdb/test/TESTDATA/test1/foo.md"
  
  (def slips-db1 (manip/slips-db currtexts-dir))

  (println "slips-db1:\n\n")
  (println slips-db1)
  (println "\n\n")
  
  (println "\n---testing highest functions")
  (println (manip/slip-map currtexts-dir "202102210815 emotional salience explains mental contrasting.md"))
  (println "\n\n")
  
  (comment
  (println "--doing a Unix 'ls'")
  (println (exec/sh "pwd"))
  (println (exec/sh "ls"))
  )

  (println "found using find-by-id:\n")
  (println (manip/find-by-id slips-db1 "202103081141"))

  (println "\nPROGRAM ENDED SUCCESSFULLY"))

