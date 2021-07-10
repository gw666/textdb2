# Introduction to textDB

Notes on understanding 'textdb' project (name based on manipulating a "database" of all the Markdown files within a specified directory)

Use: to query, manipulate, and output combinations of entries (each text file being called a 'slip' as in 'slip of paper') in a directory that contains the records of what I call a 'thinking box' (my name for a zettelkasten).

My full thinking-box is named 'GW-thinking-box'. The path to the small one I use for testing is contained in the var currtexts-dir.


Here is an example of a slip:

=== file named '201905260944 ego & relationship problems.md'  ===
=== begin file ===
201905260944 ego & relationship problems.txt

What is the role of the ego in relationship problems? How do you recognize that the problem is yours and not ours?
=== end file ===

Note that the first line of the file is the filename itself, followed by a blank line.


## Terminology

* slip: (aka 'zettel'; see https://zettelkasten.de/) a text file containing one unit of thought

* fname: the full filename of a slip (not including the path to the slip's enclosing directory)
  NOTE: (str currtexts-dir fname) is the extended filename (i.e., the <path to enclosing directory> + <filename of .md text file>
  
* id: a string that is the unique identifier of any slip; it
  is automatically created when the slip is. Currently, the id is *always*
  the first 12 characters of the fname, but different kinds of slips may be
  defined in the future.
  
* '-s' (as a suffix, in a function name): a sequence of
  *something* (e.g., fname-s returns a seq of all the filenames
  [of type '.md' ] in a directory of files)
  
* '->' (in a function name): what's on the left of '->' is the
  input, what's on the right is the output (e.g., string-s,
  meaning 'given a sequence of fileobj(s), return a sequence of string(s))
  
* slip-map (both a noun and a function): In this package, a slip
  is represented as a map, with its key = the slip's id and
  its value, with key :fname-text being a vector of [filename 
  text-of-file]. This map is also called a slip-map 
  (i.e, it is a map containing all the data
  belonging to a slip)
  
  EXAMPLE: Here is the map representing the slip-id=201910211245 (see above). The map is in the form of
  
    {:id <string-value>,
    :fname-text "<string containing filename><string containing contents of the slip>"}
  
================== the resulting map =====================

  {:id 201910211245, :fname-text ["201910211245 QUO on noticing & how to start.txt", "Tony Fadell https://www.ted.com/talks/tony_fadell_the_first_secret_of_design_is_noticing/transcript?referrer=playlist-secrets_to_understanding_life"]}
  
================== end slip-map function definition =====================


* slips-db: This is the main data structure of this project. It is 
  a sequence of maps, each map representing all the data that belongs    one slip; see 'slip-map', above.
  

## Conventions

* The contents of a thinking-box directory should be nothing but text files, ".md" preferred, otherwise ".txt"

* The text of a slip always contains the filename of the file that contains it.

* File pathnames *always* end with '/'. This is so you can say
     <filename with fullp path> = <directory that file is in, including '/'> + <filename>


# Workflow 1: for modifying a thinking-box (sorta) in place

1. Make a copy of the thinking box (change space to underline): "GW-thinking-box_copy"
2. Mark the original with date and time: "GW-thinking-box--ARCHIVE_3/13/20_3:50pm"
3. Do any manipulations **on the copy**
4. If all goes well, make the copy the original--i.e., remove "_copy from directory's name
5. **KEEP** all your archives


## SUMMARY ##

* As of 2/22/20, my use of this project is to search my thinking-box in complex ways and output the results to a text file that can be stored and printed out. This makes it easier for the author of the thinking-box to concentrate on meaningful groupings of slips, and in doing so, increasing the quality of their work.

* Individual slips are represented by a map

* Most of what the programmer will do is manipulating the slip-map and exporting selected slips to a text file in a readable format.