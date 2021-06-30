(ns textdb.core
  (:require [textdb.manip :as manip]
            [clj-commons-exec :as exec]
            [clojure.pprint :as p]
  )
  (:gen-class)
)
  
  
(defn -main []

  (println "hello")
  
  (def currtexts-dir
    "/Users/gw/Documents/github/textdb/test/TESTDATA/test1/")
    
  
  (def slips-db1 (manip/slips-db currtexts-dir))

  
  (println "\n---testing highest functions")
  (println (manip/slip-map currtexts-dir "202102210815 emotional salience explains mental contrasting.md"))
  (println "\n\n")
  
  (comment
  (println "--doing a Unix 'ls'")
  (println (exec/sh "pwd"))
  (println (exec/sh "ls"))
  )

  (println "found using find-by-id")
  (println (manip/find-by-id slips-db1 "202103081141"))
)

