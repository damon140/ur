package com.damon140.ur;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.damon140.ur.Square.*;

public class DrawnBoard {

    public static final String COUNTER_START_SEPARATOR = "|";
    public static final String COUNTER_START_SEPARATOR_PATTERN = Pattern.quote(COUNTER_START_SEPARATOR);

    public static PlayArea parsePlayAreaFromHorizontal(String game) throws NoSuchAlgorithmException {
        Deque<String> deque = Arrays.stream(game.split("\n")).collect(Collectors.toCollection(ArrayDeque::new));

        PlayArea c = new PlayArea();
        parseAndBuildCompletedCounters(deque.removeFirst(), c, Team.white);
        parseAndBuildCompletedCounters(deque.removeLast(), c, Team.black);
        addRowToCounters(0, deque.removeFirst(), c);
        addRowToCounters(1, deque.removeFirst(), c);
        addRowToCounters(2, deque.removeFirst(), c);

        return c;
    }

    private static void addRowToCounters(int boardIndex, String row, PlayArea c) {
        List<Square> boardRow = Arrays.stream(DrawnBoard.HORIZONTAL_BOARD[boardIndex])
                .filter(Objects::nonNull)
                .toList();

        List<String> chars = row.chars().mapToObj(Character::toString)
                .filter(c1 -> !BoardPart.space.isChar(c1))
                .toList();

        IntStream.range(0, boardRow.size()).boxed()
                .filter(i -> Team.isTeamChar(chars.get(i)))
                .forEach(i -> {
                    Square square = boardRow.get(i);
                    Team team = Team.fromCh(chars.get(i));
                    c.move(off_board_unstarted, square, team);
                });
    }


    private static void parseAndBuildCompletedCounters(String blackLine, PlayArea c, Team white) {
        int result = 0;
        ArrayDeque<String> deque = Arrays.stream(blackLine.replaceAll(" ", "").split(COUNTER_START_SEPARATOR_PATTERN))
                .collect(Collectors.toCollection(ArrayDeque::new));

        // no completed counters are not started
        if (1 != deque.size()) {
            result = deque.getLast().length();
        }

        IntStream.range(0, result)
            .forEach((x) -> c.move(off_board_unstarted, off_board_finished, white));
    }


    protected final static Square[][] VERTICAL_BOARD = {
            {white_run_on_4, shared_1, black_run_on_4},
            {white_run_on_3, shared_2, black_run_on_3},
            {white_run_on_2, shared_3, black_run_on_2},
            {white_run_on_1, shared_4, black_run_on_1},
            {null, shared_5, null},
            {null, shared_6, null},
            {white_run_off_2, shared_7, black_run_off_2},
            {white_run_off_1, shared_8, black_run_off_1}
    };

    private final static Square[][] HORIZONTAL_BOARD;
    static {
        int nRows = DrawnBoard.VERTICAL_BOARD.length;
        int nCols = DrawnBoard.VERTICAL_BOARD[0].length;
        HORIZONTAL_BOARD = new Square[nCols][nRows];

        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                DrawnBoard.HORIZONTAL_BOARD[j][i] = DrawnBoard.VERTICAL_BOARD[i][j];
            }
        }
    }

    final PlayArea playArea;

    public DrawnBoard(PlayArea playArea) {
        this.playArea = playArea;
    }

    public List<String> horizontalFullBoardStrings() {
        Deque<String> lines = new ArrayDeque<>(horizontalSmallBoardStrings());
        lines.addFirst(countersHorizontal(Team.white));
        lines.addLast(countersHorizontal(Team.black));
        return lines.stream().toList();
    }

    public String countersHorizontal(Team team) {
        int completed = playArea.completedCount(team);
        int unstarted = PlayArea.COUNTERS_PER_PLAYER - playArea.inPlayCount(team) - completed;
        int padding = 1 + PlayArea.COUNTERS_PER_PLAYER - completed - unstarted;

        String teamCh = team.ch;
        return teamCh.repeat(unstarted) + " ".repeat(padding-1) + DrawnBoard.COUNTER_START_SEPARATOR + teamCh.repeat(completed);
    }

    public List<String> horizontalSmallBoardStrings() {
        return board(HORIZONTAL_BOARD).stream()
                .map(l -> l.stream()
                        .map(b -> b.ch)
                        .collect(Collectors.joining("")))
                .toList();
    }

    public List<List<BoardPart>> verticalBoard() {
        return board(DrawnBoard.VERTICAL_BOARD);
    }

    public List<List<BoardPart>> board(Square[][] xxx) {
        return Arrays.stream(xxx)
                .map(l -> Arrays.stream(l)
                        .map(square -> {
                            if (null == square) {
                                return BoardPart.space;
                            }
                            return BoardPart.from(square, playArea.get(square));
                        })
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public enum BoardPart {
        white(Team.white.ch),
        black(Team.black.ch),
        star("*"),
        empty("."),
        space(" ");

        private final String ch;

        BoardPart(String ch) {
            this.ch = ch;
        }

        public static BoardPart from(Square square, Team team) {
            // need teams first so that we draw a w in precedence to a * or .
            if (Team.white == team) {
                return white;
            }
            if (Team.black == team) {
                return black;
            }
            if (square.rollAgain()) {
                return star;
            }

            return empty;
        }

        public boolean isChar(String c) {
            return this.ch.equals(c);
        }
    }

}
