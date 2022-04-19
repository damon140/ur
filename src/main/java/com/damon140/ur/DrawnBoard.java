package com.damon140.ur;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class DrawnBoard {

    protected final static Square[][] VERTICAL_BOARD = {
            {Square.white_run_on_4, Square.shared_1, Square.black_run_on_4},
            {Square.white_run_on_3, Square.shared_2, Square.black_run_on_3},
            {Square.white_run_on_2, Square.shared_3, Square.black_run_on_2},
            {Square.white_run_on_1, Square.shared_4, Square.black_run_on_1},
            {null, Square.shared_5, null},
            {null, Square.shared_6, null},
            {Square.white_run_off_2, Square.shared_7, Square.black_run_off_2},
            {Square.white_run_off_1, Square.shared_8, Square.black_run_off_1}
    };

    protected final static Square[][] HORIZONTAL_BOARD;
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

    final Counters counters;

    public DrawnBoard(Counters counters) {
        this.counters = counters;

    }

    public List<String> horizontalFullBoardStrings() {
        Deque<String> lines = new ArrayDeque<>(horizontalSmallBoardStrings());
        lines.addFirst(counters.countersHorizontal(Team.white));
        lines.addLast(counters.countersHorizontal(Team.black));
        return lines.stream().toList();
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
                            return BoardPart.from(square, counters.getCounters().get(square));
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

        // FIXME: Damon make private
        public final String ch;

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

    }

}
