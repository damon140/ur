package com.damon140.ur;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Board {

    private static final int COUNTERS_PER_PLAYER = 7;

    private final Map<Square, Team> counters;

    private final Map<Team, Integer> completedCounters;

    private Team currentTeam = Team.white; // white starts

    public Board() throws NoSuchAlgorithmException {
        counters = new HashMap<>();
        completedCounters = new TreeMap<>();
        completedCounters.put(Team.black, 0);
        completedCounters.put(Team.white, 0);
    }

    // FIXME: test for this thing
    public Board(String game) {
        // parse board
        Deque<String> deque = Arrays.stream(game.split("\n")).collect(Collectors.toCollection(ArrayDeque::new));
        String whiteLine = deque.removeFirst();
        String blackLine = deque.removeLast();
        completedCounters = new TreeMap<>();
        completedCounters.put(Team.black, completedCountersFromString(blackLine));
        completedCounters.put(Team.white, completedCountersFromString(whiteLine));

        // TODO: tidy & shrink
        List<Square> topBoard = Arrays.stream(HORIZONTAL_BOARD[0]).toList();
        String topHozRow = deque.removeFirst();
        List<Square> midBoard = Arrays.stream(HORIZONTAL_BOARD[1]).toList();
        String midHozRow = deque.removeFirst();
        List<Square> botBoard = Arrays.stream(HORIZONTAL_BOARD[2]).toList();
        String botHozRow = deque.removeFirst();

        counters = new HashMap<>();
        extracted(topBoard, topHozRow, counters);
        extracted(midBoard, midHozRow, counters);
        extracted(botBoard, botHozRow, counters);
    }

    private void extracted(List<Square> maybeSparseBoard, String row, Map<Square, Team> target) {
        List<Square> boardRow = maybeSparseBoard.stream().filter(Objects::nonNull).toList();
        List<String> chars = row.chars().mapToObj(c -> Character.toString(c))
                .filter(c -> {
                    boolean equals = BoardPart.space.ch.equals(c);
                    return !equals;
                })
                .toList();
        Map<Square, String> topRow = zipToMap(boardRow, chars);
        topRow.entrySet().stream()
                .filter(e -> Team.isTeamChar(e.getValue()))
                .forEach(e -> {
                    target.put(e.getKey(), Team.fromCh(e.getValue()));
                });
    }

    public static <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
        return IntStream.range(0, keys.size()).boxed()
                .collect(Collectors.toMap(keys::get, values::get));
    }

    private int completedCountersFromString(String countersString) {
        ArrayDeque<String> deque = Arrays.stream(countersString.split(" "))
                .collect(Collectors.toCollection(ArrayDeque::new));

        if (1 == deque.size()) {
            // no completed counters as only unstarted counters
            return 0;
        }

        return deque.getLast().length();

    }

    public List<String> horizontalFullBoardStrings() {
        Deque<String> board = new ArrayDeque<>(horizontalSmallBoardStrings());
        board.addFirst(countersHorizontal(Team.white));
        board.addLast(countersHorizontal(Team.black));
        return board.stream().toList();
    }

    public String countersHorizontal(Team team) {
        int completed = completedCounters.get(team);
        int unstarted = COUNTERS_PER_PLAYER - (int) counters.values().stream().filter(t -> team == t).count() - completed;
        int padding = 1 + COUNTERS_PER_PLAYER - completed - unstarted;

        return team.ch.repeat(unstarted) + " ".repeat(padding) + team.ch.repeat(completed);
    }

    public List<String> horizontalSmallBoardStrings() {
        return board(HORIZONTAL_BOARD).stream()
                .map(l -> l.stream()
                        .map(b -> b.ch)
                        .collect(Collectors.joining("")))
                .toList();
    }

    public List<List<BoardPart>> verticalBoard() {
        return board(VERTICAL_BOARD);
    }

    private List<List<BoardPart>> board(Square[][] xxx) {
        return Arrays.stream(xxx)
                .map(l -> Arrays.stream(l)
                        .map(square -> {
                            if (null == square) {
                                return BoardPart.space;
                            }
                            return BoardPart.from(counters.get(square));
                        })
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public Set<String> state() {
        return new TreeSet<>(List.of(
                "counters: " + counters.toString(),
                "completedCounters: " + completedCounters.toString(),
                "currentTeam: " + currentTeam.toString()
        ));
    }


    public int completedCount(Team team) {
        return completedCounters.get(team);
    }

    public static Square calculateNewSquare(Team team, Square square) {
        return switch (square) {
            case black_run_on_4, white_run_on_4 -> Square.shared_1;
            case shared_8 -> team == Team.black ? Square.black_run_off_1 : Square.white_run_off_1;
            case black_run_off_2, white_run_off_2 -> Square.off_board_finished;
            case off_board_unstarted -> team == Team.black ? Square.black_run_on_1 : Square.white_run_on_1;
            default -> Square.values()[1 + square.ordinal()];
        };
    }

    // TODO: switch to new illegal_sqaure square instead of opt
    public static Optional<Square> calculateNewSquare(Team team, Square square, int count) {
        // TODO: simplify
        if (square == Square.off_board_finished) {
            return Optional.of(Square.off_board_finished);
        }
        Square newSquare = square;
        for (int looper = 0; looper < count; looper++) {
            if (Square.off_board_finished == newSquare) {
                return Optional.empty();
            }
            newSquare = calculateNewSquare(team, newSquare);
        }
        return Optional.of(newSquare);
    }

    public Map<Square, Team> getCounters() {
        return this.counters;
    }

    public Team currentTeam() {
        return this.currentTeam;
    }

    public void swapTeam() {
        this.currentTeam = this.currentTeam.other();
    }

    public boolean allCountersStarted(Team team) {
        return COUNTERS_PER_PLAYER == completedCounters.get(team);
    }

    public Map<Team, Integer> getCompletedCounters() {
        return completedCounters;
    }

    // FIXME: Damon, this is counting wrong
    public int unstartedCount(Team team) {
        long inProgressCounters = this.counters.entrySet()
                .stream()
                .filter(e -> team == e.getValue())
                .count();
        int finishedCounters = this.completedCounters.get(team);

        return COUNTERS_PER_PLAYER - finishedCounters - (int) inProgressCounters;
    }

    public boolean allStartedOrComplete(Team team) {
        int completed = this.completedCount(team);
        int inProgress = this.counters.size();
        return COUNTERS_PER_PLAYER == completed + inProgress;
    }

    public enum Square {
        off_board_unstarted,

        black_run_on_1,
        black_run_on_2,
        black_run_on_3,
        black_run_on_4,

        black_run_off_1,
        black_run_off_2,

        shared_1,
        shared_2,
        shared_3,
        shared_4,
        shared_5,
        shared_6,
        shared_7,
        shared_8,

        white_run_on_1,
        white_run_on_2,
        white_run_on_3,
        white_run_on_4,

        white_run_off_1,
        white_run_off_2,

        off_board_finished,
        ;

        public boolean dontRollAgain() {
            return !Set.of(black_run_on_4, white_run_on_4, shared_4, black_run_off_2, white_run_off_2).contains(this);
        }

        public boolean isSafeSquare() {
            return shared_4 == this;
        }
    }

    public enum BoardPart {
        white(Team.white.ch),
        black(Team.black.ch),
        empty("."),
        space(" ");

        private final String ch;

        BoardPart(String ch) {
            this.ch = ch;
        }

        public static BoardPart from(Team team) {
            if (null == team) {
                return empty;
            }
            if (Team.white == team) {
                return white;
            }
            return black;
        }

    }

    final static Square[][] VERTICAL_BOARD = {
            {Square.white_run_on_4, Square.shared_1, Square.black_run_on_4},
            {Square.white_run_on_3, Square.shared_2, Square.black_run_on_3},
            {Square.white_run_on_2, Square.shared_3, Square.black_run_on_2},
            {Square.white_run_on_1, Square.shared_4, Square.black_run_on_1},
            {null, Square.shared_5, null},
            {null, Square.shared_6, null},
            {Square.white_run_off_2, Square.shared_7, Square.black_run_off_2},
            {Square.white_run_off_1, Square.shared_8, Square.black_run_off_1}
    };

    static Square[][] HORIZONTAL_BOARD;

    static {
        int nRows = VERTICAL_BOARD.length;
        int nCols = VERTICAL_BOARD[0].length;
        HORIZONTAL_BOARD = new Square[nCols][nRows];

        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                HORIZONTAL_BOARD[j][i] = VERTICAL_BOARD[i][j];
            }
        }
    }

    // TODO: light & dark as less racist names??
    public enum Team {
        white, black;

        public final String ch;

        Team() {
            ch = this.name().substring(0, 1);
        }

        public Team other() {
            return Team.values()[(this.ordinal() + 1) % 2];
        }

        public static boolean isTeamChar(String value) {
            return value.equals(white.ch) || value.equals(black.ch);
        }

        public static Team fromCh(String value) {
            return white.ch.equals(value) ? white : black;
        }
    }

}
