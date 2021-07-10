(ns textdb.manip
  (:require
      [clojure.string :refer [ends-with? includes? split trimr]]
      [clojure.java.io :only [file]]
  )
  (:gen-class)
)

(def track-this-file-into-github-repository "feel free to delete this as needed")

; *******************************************************
; *                                                     *
; * NOTE: This project is about making reports from     *
; * different subsets of the primary text-db. However,  *
; * *any* seq of slip-maps is a kind of "text database" *
; * and such "minor" text-dbs will be manipulated (by,  *
; * for example, text-db-report)                        *
; *                                                     *
; *******************************************************
  
; *******************************************************
; *                                                     *
; * these are the only functions that require the path  *
; * to the directory containing the text files that     *
; * we want to manipulate                               *
; *                                                     *
; * -- allobjs-seq: returns all File objects         *
; * -- txtfile-fname-seq: returns all text filenames      *
; * -- slip-map: returns a given text file's contents   *
; *      as a map                                       *
; * -- slips-db: returns a seq of slip-map entries, one *
; *      for each text file in the specified directory  *
; *                                                     *
; *******************************************************

; *******************************************************
; *                                                     *
; * Decoding parameter names:                           *
; *                                                     *
; * --allobjs: all the File objects in a directory      *
; *    (plus one File object for the dir itself)        *
; * --dir-path: pathname to dir holding all the txt     *
; *    files comprising the textdb                      *
; * --fileobjs: File objects representing files         *
; * --fname: a string representing a file name          *
; * --seqmap: abbreviation of 'slip-map;                  *
; * --seqtrings-seq: a seq of (usually) filename strings    *
; * --txtfile: any file ending in '.txt' or 'md'        *
; *                                                     *
; *******************************************************

(defn allobjs-seq
  "returns a seq of *all* File objects for the directory itself
    and all the files contained in it"
  [dir-path]

  ; gets java.io.File objects for a given path, including directories
  ; and items in *subdirectories* (also Apple ".DS_Store" files)
  ; -- item 0 represents the directory itself; item 1 is first file, etc.

  (file-seq                         ; 2. Return the directory's contents
    (clojure.java.io/file dir-path) ; 1. Get the fileobject of the directory
  ))

(defn fileobjs-seq
    "IN: seq of File objects
     OUT: filtered seq of only those File objects that are
          files (File objs of directories removed)"
  [allobjs-seq]
  (filter #(.isFile %) allobjs-seq))

; at this point, we stop working with File objects, begin
; working with STRINGS that represent these File objects

(defn filenames-seq
    "Returns the .getName property of a sequence of files, as seq"
    [fileobjs-seq]
    (map #(.getName %) fileobjs-seq)
)

(defn txtfile-fnames-seq
  "filters out all strings that do not end with .md"
  [filenames-seq]
  (filter #(ends-with? % ".md") filenames-seq))

(defn all-fnames-seq
  "returns seq of all fnames in dir (as strings)"
  [dir-path]
    (-> dir-path
        (allobjs-seq ,,,)
        (fileobjs-seq ,,,)  ; returns a lazy-seqeq
        (filenames-seq ,,,)
        (txtfile-fnames-seq ,,,)))



(defn fname-id
  "derives slip's id from its filename"
  [fname]
; --------------------------------------- 
  (re-find #"^\d{12}" fname)  
)
(defn id-seq
  "returns a seq of id's for each filename in fname-seq"
  [fname-seq]
  (map fname-id fname-seq)
)

; *********************************************
; *                                           *
; * NOTE: requires dir-path to work correctly *
; *                                           *
; *********************************************
(defn slip-map   ; aka 'smap'
  "all the data of one slip, as a single map,
   key = id, value = [fname contents-of-seqlip]"
  [dir-path fname]
  ; --------------------------------------- 
  (let [id     (fname-id fname)
          ;;line 0 = id; line 1-N = the file's contents
        text   (slurp (str dir-path fname))
        text-out (if (empty? text) "\n" text)
       ]
;    (println "id/text: " id "[" text-out "]")
;    (println "Is text empty?: " (empty? text) "\n")
    (hash-map :id id, :fname fname, :text text-out) 
  )
)

(defn smap-fname
  "get filename from slip-map"
  [slip-map]
  ; --------------------------------------- 
  (:fname slip-map)
)
(defn smap-text
  "get file text from slip-map"
  [slip-map]
  ; --------------------------------------- 
  (:text slip-map)
)

(defn slips-db
  "creates a seq containing one map for each slip in the specified directory"
  [dir-path]
  (let [fname-seq (txtfile-fnames-seq dir-path)]
    (map (partial slip-map dir-path) fname-seq)))

(defn find-by-id
  "given an id value, return the map of the slip that has that value;
   returns nil if nothing found"
  [slips-db id]
; NOTE: returns map, not *seq* containing the map
  (first (filter #(= id (% :id)) slips-db))
)
(defn find-by-fname
  "given an id value, return the map of the slip that has that value;
   returns nil if nothing found"
  [slips-db fname]
; NOTE: returns map, not *seq* containing the map
  (first (filter #(= fname (% :fname)) slips-db))
)
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
  [dir-path fname text-seqtr]
  (let [full-fname (str dir-path fname)]
    (spit full-fname text-seqtr :append true)
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
(defn smap-seqtring
  "creates a formatted string for the specified slip-map"
  [before-seqtr between-seqtr after-seqtr slip-map]
  (str before-seqtr (smap-fname slip-map) between-seqtr (trimr (smap-text slip-map)) after-seqtr)
)

(defn text-db-report
  "creates a title/contents report for all slip-maps in the text-db"
  [a-text-db before-seqtr between-seqtr after-seqtr]
  
  (let [partial-fcn (partial smap-seqtring before-seqtr between-seqtr after-seqtr)
        single-reports-seq (map partial-fcn a-text-db)]
    ; force lazy seq to be realized as a single string
    (apply str single-reports-seq)))

(defn same-ids?
  "returns true iff two strings begin with same slip id"
  [str1 str2]
  (= (fname-id str1) (fname-id str2)))


(defn chop-text
  "split text into vector of <first line> <rest of text>"
  [text-seqtr]
  
  ; NOTE: fcn returns error if test-seqtr is empty...
  ;       at least one "\n"; if there is none, this fcn 
  ;       appends a "\n" to the input string
  
  (let [first-ln (second (re-find #"^(.*?)\n" text-seqtr))
        rest-text (second (re-find #"(?s)^.*?\n(.*)$" text-seqtr))
        text-out (if (includes? rest-text "\n")
                         rest-text
                         (str rest-text "\n"))]
    (vector first-ln text-out)))

(defn test-CR-regex  ; temporarily of use
  [text-seqtr]
  (let [rest-text (second (re-find #"(?s)^.*?\n(.*)$" text-seqtr))
       ]
  (println "text-seqtr  -->" text-seqtr "<--")
  (println "rest-text -->" rest-text "<--")  
  )
)

(defn add-fname-to-seqlip-text
  "ensures that the body of text always begins w/ the current fname"
  [fname slip-text]
  
  (let [chopped-text (chop-text slip-text)
        line-1 (nth chopped-text 0)
        rest-text   (nth chopped-text 1)
       ]
    (if (same-ids? line-1 fname)
      (str fname "\n" rest-text)  ; if fname is already on line 1
      (str fname "\n" slip-text)  ; if it is *not* on line 1
    )
  )
)

(defn update-seqlip-map-v
  "creates [fname text] from slip-map, adding fname as needed to the text"
  [slip-map]
  
  (let [fname (slip-map :fname)
        slip-text (slip-map :text)
        text-out (if (includes? slip-text "\n")
                   slip-text
                   (str slip-text "\n"))
       ]
    ; the function could be factored out to enable arbitrary changes
    ; to the slip text
    (vector fname (add-fname-to-seqlip-text fname text-out))  
  )  
)


#_(defn usmv-test ;debugging only
  [mytext slip-map]
  
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
  ))

(defn munge-thinking-box
  "use modification-fcn on all slips to create seq of [fname modified-text]"
  [orig-tbox-p modification-fcn]
  
  (let [orig-textdb (slips-db orig-tbox-p)]
       
;    (println "fnames\n" all-seqlips-fname-seq "\n\n")
;    (println "orig-textdb\n" orig-textdb "\n\n")
    ; result of map is a lazy seq of [filename text] for each slip-map
    ; modification-fcn outputs [fname newtext] for each slip-map in orig-textdb
    (mapv modification-fcn orig-textdb)
  )
)

(defn spit-fname-text-pair
  "Given [fname text] of one slip, reconstitute the file in directory dir-path"
  [dir-path fname-text-pair]
  (let [slip-fname (nth fname-text-pair 0)
        slip-text  (nth fname-text-pair 1)
        full-fname (str dir-path slip-fname "/")
       ]
    (spit full-fname slip-text)
  )  
)

(defn spit-new-textdb
   "Create, in dest-dir, one slip text-file for each [fname text] pair in fname-text-pair-seq"
   [dest-dir fname-text-pair-seq]
   (println "size of fname-text-pair-seq is" (count fname-text-pair-seq))
   (mapv #(spit-fname-text-pair dest-dir %1) fname-text-pair-seq))

 ; ----------- code setup
(comment
(def srcp "/Users/gr/Dropbox/THINKING-BOXES/GW-thinking-box/")
(def mydb (slips-db srcp))  ;says srcp must be an integer
  
(defn fname-in-seqlip-text?
  "Is the text file's name = to the first line of its contents?"
  [my-seqmap]
  (=
   (my-seqmap :fname)
   (first (split (my-seqmap :text) #"\n" 2))))

(defn fnames-added-to-text?
  "Returns false if any slip does not prepend the filename to the textdb"
  [my-db]
  (every?
   [true?]
   (mapv (partial fname-in-seqlip-text?) my-db)))

; ===== to build a database using the master thinking-box directory =====

(def srcpath "/Users/gr/Dropbox/THINKING-BOXES/GW-thinking-box/")
(def destpath "/Users/gr/Dropbox/THINKING-BOXES/new/")

(defn munge-db
  "munge the textdb, as given by srcpath and destpath, re-creating the text db in the destpath directory (which should be empty)"
  [srcpath destpath]
  (let [my-fname-text-pairs-ts (munge-thinking-box srcpath update-seqlip-map-v)]
    (spit-new-textdb destpath my-fname-text-pairs-ts)))
 
; MANUAL STEPS TO PERFORM AFTER RUNNING MUNGE-DB:
; diff the two directories to confirm correctness
; copy 'media' folder to 'new'
; rename 'new' as 'GW-thinking-box'
; compress 'curr', rename that zip file with today's date
; delete 'curr' directory (because zip copy exists)
;

; tool to confirm that munge-db has produced the desired result
; we're assuming that the munged data is still in the "new" directory
; ===== end =====
; 


(defn fname-begins-seqlip-text?
    "Returns true if the same, name of file if file's line 0 is not filename"
    [smap]
    (let [atext   (smap :text)
          fname   (smap :fname)
          line0   (get (chop-text atext) 0)]
      (if (= fname line0) true fname)))

; NOTE: must confirm munge-db works before continuing
(defn textdb-errors
  "checks for first-line errors in textdb contents"
  [db-path]
  (let [my-db   (slips-db db-path)]
    (mapv fname-begins-seqlip-text? my-db)))

; START HERE FOR TEXTDB-ERRORS
; chop-text has execn errors if no text or [text but no CR]
; save original defn for chop-text
; modify, streamline it
; see if munge-db still works okay
; create tests for chop-text edge cases
; resume work on textdb-errors

) ;end comment