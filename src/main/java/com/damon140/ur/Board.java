package com.damon140.ur;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
        String teamChar = BoardPart.from(team).ch();

        return teamChar.repeat(unstarted) + " ".repeat(padding) + teamChar.repeat(completed);
    }

    public List<String> horizontalSmallBoardStrings() {
        return board(HORIZONTAL_BOARD).stream()
                .map(l -> l.stream()
                        .map(BoardPart::ch)
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

    public static Square calculateNewSquare(Team team, Square square, int count) {
        if (square == Square.off_board_finished) {
            return Square.off_board_finished;
        }
        Square newSquare = square;
        for (int looper = 0; looper < count; looper++) {
            newSquare = calculateNewSquare(team, newSquare);
        }
        return newSquare;
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
        white, black, empty, space;

        public String ch() {
            switch (this) {
                case empty:
                    return ".";
                case space:
                    return " ";
            }
            ;
            return this.name().substring(0, 1);
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

    public enum Team {
        white, black;

        public Team other() {
            return Team.values()[(this.ordinal() + 1) % 2];
        }
    }

    public static class Dice {

        private final SecureRandom random;

        public Dice() throws NoSuchAlgorithmException {
            random = SecureRandom.getInstanceStrong();
        }

        public int roll() {
            return random.ints(4, 0, 2)
                    .sum();
        }

    }

}
