Demonstration Clojure app which alerts a player when a group of stones has been captured by a 
move in the game of Go. It's a 'simple' capture because it doesn't understand any of the special 
cases. If a group has at least one lifeline, it is considered alive. Also, it does not remove 
stones in case of a capture.

## Installation

This requires [Leiningen](http://www.leiningen.org/). One installed,

	lein run 

within the project directory will run (two-stone-demo). 

## Usage

There are three demo scenarios, (one-stone-demo), (two-stone-demo), and (one-eye-demo). The first
two have proper captures where as the latter one, although the black stones are surrounded, there
is no capture due to a shared lifeline between them.

To run another demo, simply edit the (-main) function src/simple_go_capture/core.clj to run 
a different demo.

Creating new demos should be straightforward: borrowing from one of the existing demos, add 
moves until the desired layout of stones is created. As already demonstrated, stones can be
laid down in any order and a capture notice will appear regardless of when the finally surrounding
stone is played.

## Miscellaneous

More info on Go can be found [here](http://en.wikipedia.org/wiki/Game_of_Go).

## License

Copyright © 2014 Rehacktive.com

Distributed under the Eclipse Public License either version 1.0.
