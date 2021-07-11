(ns textdb.scratchpad)

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
  [slipmap]
  (let [atext   (slipmap :text)
        fname   (slipmap :fname)
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

