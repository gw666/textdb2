(ns textdb.manip-test
  (:require [clojure.test :refer :all]
            [textdb.manip :refer :all]
  )
  (:gen-class)
)  


;=====================================================================
;
; NOTE, 3/25/20, 7:20pm: The '1 line no-CR.md' file has been taken
; out of the TESTSUITE dir; the code does the correct thing for *all*
; files that have one line and no CR at the end--that is, it adds a
; "\n" at the end. However, this file cannot be part of the TESTSUITE
; because of the following: 1) to prevent a code crash, a "\n" must be
; added to the end (see above), but any comparisons to the original
; slip file *must* fail because that same "\n", by definition, can
; never be present. So:
;
;  1) the code always does the right thing,
;   EVEN THOUGH
;  2) the '1 line no-CR.md' file is *not* part of the TESTSUITE
;
;=====================================================================


(def curr-txt-path-prefix
  "/Users/gr/tech/clojurestuff/cljprojects/textdb/test/DATA/"
)
    
(def curr-txt-path-ts (str curr-txt-path-prefix "TESTSUITE" "/"))

(def fnames-ts (txtfile-fname-s curr-txt-path-ts))

(def orig-textdb (slips-db curr-txt-path-ts fnames-ts))

(def expected-fnames-ts 
  [
  "202003061142 'knowing what nobody knows' is an advantage.md" 
  "202003141104 1 line w-CR.md" 
  "202003141105 3 lines.md"
  "202003141107 2 lines.md" 
  "202003141108 no lines.md" 
  ]
)
 
(def expected-id-ts
  [
     "202003061142" 
     "202003141104" 
     "202003141105" 
     "202003141107" 
     "202003141108" 
  ]
)

(def expected-fname-text-ts
  [
    ["202003061142 'knowing what nobody knows' is an advantage.md" "202003061142 'knowing what nobody knows' is an advantage.md\nBrian Glazer; a thousand people didn't think that making a movie about a mermaid in love would work. He felt that was 'knowing what nobody knows'.\n\nDid he see that as a market opportunity? A sign he was more creative than them? Was it another way of saying that he believed in the idea?\n\nDid he use this thought to persevere?\n\n\n\n"]
    ["202003141107 2 lines.md" "202003141107 2 lines.md\nthis is line 1\nline 2 ends with a newline\n"] 
    ["202003141105 3 lines.md" "202003141105 3 lines.md\nline 1\nline 2\nline 3, ends with newline\n"] 
    ["202003141104 1 line w-CR.md" "202003141104 1 line w-CR.md\nthis is one line of text, newline at end\n"] 
    ["202003141108 no lines.md" "202003141108 no lines.md\n\n"]
  ]
)

; [fname munged-text] pairs, as created by munge-thinking-box
(def munged-fname-text-ts (munge-thinking-box curr-txt-path-ts update-slip-map-v))

(def munged-text-only-ts (map #(second %1) munged-fname-text-ts))
(def first-lines-only-ts (map #(first (chop-text %1)) munged-text-only-ts))

(def eftt-texts (map #(second %1) expected-fname-text-ts))

(def chopped-eftt-texts (map #(second (chop-text %1)) eftt-texts))

(def eftt-fnames (map #(first %1) expected-fname-text-ts))


(def orig-texts (map #(:text %1) orig-textdb))

(def orig-fnames (map #(:fname %1) orig-textdb))

; ======================================

(deftest text-munging-checks

  ; Are <lines 2..n of the text> from eftt and the text from 
  ; orig-textdb the same?
  (is (= (set chopped-eftt-texts)
         (set orig-texts)
      )
  )
  
  ; The first line of the munged text should be the same as the
  ; filenames in eftt. Are they?
  (is (= (set first-lines-only-ts)
         (set eftt-fnames)
      )
  )
)

(deftest fname-tests-ts
  ; Are the ids in the list of filenames the same as the 
  ; ideas in manually created set?
  (is (= (set (id-s fnames-ts)) 
         (set expected-id-ts)
      )
  )

  ; Are the filenames (gotten from files in dir) same as
  ; the fnames in efftt?
  (is (= (set (txtfile-fname-s curr-txt-path-ts))
         (set expected-fnames-ts)
      ) 
  )
)


; -------------------------------------------
