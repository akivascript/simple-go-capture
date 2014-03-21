(ns simple-go-capture.core
  (:require [clojure.pprint :refer [pprint]]
            [clojure.set :refer [difference]])
  (:gen-class))

; Board functions
(defn create-board
  "Creates a square, empty board."
  [x]
  {:pre [(> x 8) (< x 20)]}
  (vec (repeat x (vec (repeat x :_)))))

(defn legal-point?
  "Returns true if a point is a valid location on the board, false otherwise."
  [board [row col]]
  (let [size (count board)]
    (and
      (>= col 0)
      (>= row 0)
      (< col size)
      (< row size))))

(defn occupied-by
  "Returns the type of stone (or :_)."
  [[row col] board]
  (get-in board [row col]))

(defn occupied-by?
  "Returns true if a point is occupied by a particular color (or :_),
  otherwise false."
  [color board [row col]]
  (if (= (occupied-by [row col] board) color)
    true
    false))

(defn show
  "Prints a nice grid of a board."
  [board]
  (pprint board))


; Group functions
(defn get-neighborhood
  "Returns a lazy sequence consisting of the four points surrounding the 
  given [row col]."
  [[row col] board]
  (let [n [[(dec row) col] 
           [row (dec col)] 
           [row (inc col)] 
           [(inc row) col]]]
    (filter #(legal-point? board %) n)))

(defn build-group
  "Returns a set of points representing a group of connected stones of the 
  same color."
  [color [row col] board]
  (let [point #{[row col]}
        group #{}]
    (loop [g group, nh point]
      (if (empty? nh)
        g
        (let [n (first nh)
              nbs (-> (set (->> (get-neighborhood n board)
                                (filter #(occupied-by? color board %))))
                      (difference g))]
          (if (nil? (next nh))
            (recur (into g [n]) (into #{} nbs))
            (recur (into g [n]) (into #{(next nh) nbs}))))))))

(defn is-group-alive?
  "Returns true if a group has at least one life-line, otherwise returns nil."
  [color group board]
  (declare is-alive?)
  (some true? (map #(is-alive? % board) group)))


; Stone functions
(defn is-alive?
  "Returns true if the board point has at least one life line, otherwise
  returns nil."
  [[row col] board]
  (or (= :_ (occupied-by [row col] board))
      (let [nh (get-neighborhood [row col] board)]
        (some true? (map #(occupied-by? :_ board %) nh)))))

(defn place-stone
  "Places a stone on the board."
  [color [row col] board]
  (declare get-other-color)
  (if (and (occupied-by? :_ board [row col])
           (not (occupied-by? (get-other-color color) board [row col])))
    (assoc-in board [row col] color)
    board))

(defn move
  "Processes a player's turn: checks for captures, and returns a 2D vector 
  of the updated board."
  ; Note: There's some duplication in here that should be factored out where it's
  ; checking for capture of the active player's stone and then checking for capture
  ; of the opposing player's stones (if any).
  [color [row col] board]
  (declare alert-capture)
  {:pre [(legal-point? board [row col])]}
  (let [new-board (place-stone color [row col] board)
        group (build-group color [row col] new-board)]
    (if-not (is-group-alive? color group new-board)
      (alert-capture [row col] new-board)
      (let [oc (get-other-color color)
            nh (set (->> (get-neighborhood [row col] new-board)
                         (filter #(occupied-by? oc new-board %))))]
        (loop [nh nh]
          (if-not (empty? nh)
            (let [g (build-group oc (first nh) new-board)]
              (if-not (is-group-alive? color g new-board)
                (alert-capture [row col] new-board))
              (recur (next nh)))))))
    new-board))


; General functions
(defn get-other-color
  "Returns the other player's color."
  [color]
  (cond
    (= color :w) :b
    (= color :b) :w
    :default :_))

(defn alert-capture
  "Alerts the player that a stone or group of
  stones has been captured by the last-played move."
  [[row col] board]
  (let [color (occupied-by [row col] board)]
    (println "With" color "playing at [" row col "], a capture has occurred!")))


; Play demos
(defn one-stone-demo
  "Demonstrates the application showing a single
  stone being captured."
  []
  (show
    (->> (create-board 9)
         (move :b [4 4])
         (move :w [4 3])
         (move :w [3 4])
         (move :w [4 5])
         (move :w [5 4]))))

(defn two-stone-demo
  "Demonstrates the application showing two stones
  being captured."
  []
  (show
    (->> (create-board 19)
         (move :b [4 4])
         (move :b [4 5])
         (move :w [3 4])
         (move :w [3 5])
         (move :w [4 3])
         (move :w [4 6])
         (move :w [5 4])
         (move :w [5 5]))))

(defn one-eye-demo
  "Demonstrates that stones with at least one inside
  lifeline will not be captured even if surrounded."
  []
  (show
    (->> (create-board 9)
         (move :b [4 4])
         (move :w [3 4])
         (move :b [4 6])
         (move :w [3 5])
         (move :w [3 6])
         (move :w [4 7])
         (move :w [5 6])
         (move :w [5 5])
         (move :w [5 4])
         (move :w [4 3]))))


; Main!
(defn -main
  "Executes and displays one or more capture demonstrations."
  [& args]

  ; The demo as presented in the original challenge
  (show (two-stone-demo)))
