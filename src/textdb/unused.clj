(ns textdb.unused)

(defn truthy
  "if val=nil or false, returns false; else returns true"
  [val]
  (not (or (nil? val) (false? val)))
  )
(defn export-to-file
  ; may be wrong approach
  "appends fname, contents to export-fname; if export-fname
   does not exist, it is created"
  [export-fname slip-map]

  (let [slip-fname   (smap-fname slip-map)
        slip-text    (smap-text  slip-map)
        ]
    (spit export-fname (str slip-fname "\n-----\n\n") :append true)
    (spit export-fname (str slip-text
                            "\n--------------------------------------------\n\n\n"
                            )
          :append true
          )
    )
  )
  
(defn pour   ;may be wrong approach
  "creates new file, based on parameters; always appends;
   does not depend on 'require, 'refer, 'use, elsewhere
   in code"
  [dir-path fname text-str]
  (let [full-fname (str dir-path fname)]
    (spit full-fname text-str :append true)
    )
  )
(defn parent-path
  "return complete path to dir-path's parent (assumes
   that dir-path ends with a '/')"
  [dir-path]
  ; example: "/a/b/c/d" returns "/a/b/c/"
  (let [results (re-find #"^(.*)/.*/$" dir-path)]
    ; append "/" to make it easy to append filename
    ; and have it be a valid absolute path
    (str (nth results 1) "/")
    )
  )

(defn smap-string
  "creates a formatted string for the specified slip-map"
  [before-str between-str after-str slip-map]
  (str before-str (smap-fname slip-map) between-str (trimr (smap-text slip-map)) after-str)
  )

(defn text-db-report
  "creates a title/contents report for all slip-maps in the text-db"
  [a-text-db before-str between-str after-str]

  (let [partial-fcn (partial smap-string before-str between-str after-str)
        single-reports-s (map partial-fcn a-text-db)]
    ; force lazy seq to be realized as a single string
    (apply str single-reports-s)
    )
  )

(defn usmv-test ;debugging only
  [mytext]

  (let [fname (slip-map :fname)
        slip-text (slip-map :text)
        text-out (if (includes? slip-text "\n")
                   slip-text
                   (str slip-text "\n"))
        ]
    (println "slip-text  -->" slip-text "<--")
    (println "slip-text type  -->" (type slip-text) "<--")
    (println "text-out -->" text-out "<--")
    (println "text-out type -->" (type text-out) "<--")
    )
  )

; =======================================================
; =======================================================
; testing code, from textdb.manip file
; =======================================================
; =======================================================

(defn test-CR-regex  ; temporarily of use
  [text-str]
  (let [first-ln (second (re-find #"^(.*?)\n" text-str))
        rest-text (second (re-find #"(?s)^.*?\n(.*)$" text-str))
        ]
    (println "text-str  -->" text-str "<--")
    (println "rest-text -->" rest-text "<--")
    )
  )



; -------------------------------------------
; begin testing code, using GW-thinking-box
; -------------------------------------------
(def textdb-source-dir "/Users/gr/Dropbox/THINKING-BOXES/GW-thinking-box/")

(def textdb-dest-dir "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/textdb-NEW/")

; 116 records--correct
(def textdb-fnames (txtfile-fname-s "/Users/gr/Dropbox/THINKING-BOXES/GW-thinking-box/"))

; ditto
(def origfulldb (slips-db textdb-source-dir textdb-fnames))

(def mfulldb (munge-thinking-box textdb-source-dir update-slip-map-v))

; (spit-new-textdb textdb-dest-dir mfulldb)

;  =====================================================================================
;  =====================================================================================
; -------------------------------------------
; doing munge of munged text (above), using DATA/textdb-NEW2
; -------------------------------------------

; **********************************************
; WARNING: this modifies AFTST to determine whether the munging of an
; already munged textb does not introduce any destructive;
; CHECK the resulting textdb, looking for malformed slip files
;
; WHEN DONE, restore the original version to the REPL
; **********************************************
#_
    (defn add-fname-to-slip-text
      "adds a dummy line to the twice-munged textdb"
      [fname slip-text]

      (str "=====>> T w I c E d-M u N g E d  T e X t <<=====" "\n" slip-text)
      )

(def textdb-source-dir "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/textdb-NEW/")

(def textdb-dest-dir "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/textdb-NEW2/")

(def textdb-fnames (txtfile-fname-s "/Users/gr/Dropbox/THINKING-BOXES/GW-thinking-box/"))

(def origfulldb (slips-db textdb-source-dir textdb-fnames))

(def mfulldb (munge-thinking-box textdb-source-dir update-slip-map-v))

; (spit-new-textdb textdb-dest-dir mfulldb)

; ------------------- end testing code ------------------------
;  =====================================================================================
;  =====================================================================================


; ===== to build a database using the TESTSUITE directory =====

(def source-path-ts "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/TESTSUITE/")

(def dest-path-ts "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/TESTSUITE-NEW/")

; this is a seq of txtfile name strings
(def slip-fnames-s (txtfile-fname-s source-path-ts))

(def mydb (slips-db source-path-ts slip-fnames-s))

; creates a seq of [fname modified-text] pairs
(def my-fname-text-pairs-ts (munge-thinking-box source-path-ts update-slip-map-v))

; creates a textdb that is the munged version of mydb
(spit-new-textdb dest-path-ts my-fname-text-pairs-ts)




; ===== to build a database using the master thinking-box directory =====

(def source-path-ts "/Users/gr/Dropbox/THINKING-BOXES/GW-thinking-box/")

(def dest-path-ts "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/textdb-NEW/")

; this is a seq of txtfile name strings
(def slip-fnames-s (txtfile-fname-s source-path-ts))

(def mydb (slips-db source-path-ts slip-fnames-s))

; creates a seq of [fname modified-text] pairs
(def my-fname-text-pairs-ts (munge-thinking-box source-path-ts update-slip-map-v))

; creates a textdb that is the munged version of mydb
; (spit-new-textdb dest-path-ts my-fname-text-pairs-ts)

; =====

(def before-str "\n================================\n")
(def between-str "\n--------------------------------\n")
(def after-str   "\n================================\n\n")

(comment
  ; to aid in debugging smap-string
  (def before-str "BEFORE\n")
  (def between-str "\nBETWEEN\n")
  (def after-str   "\nAFTER\n\n\n")
  )




; ===== end =====
