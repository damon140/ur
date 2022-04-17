package com.damon140.ur;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class DrawnBoard {

    protected final static CounterPositions.Square[][] VERTICAL_BOARD = {
            {CounterPositions.Square.white_run_on_4, CounterPositions.Square.shared_1, CounterPositions.Square.black_run_on_4},
            {CounterPositions.Square.white_run_on_3, CounterPositions.Square.shared_2, CounterPositions.Square.black_run_on_3},
            {CounterPositions.Square.white_run_on_2, CounterPositions.Square.shared_3, CounterPositions.Square.black_run_on_2},
            {CounterPositions.Square.white_run_on_1, CounterPositions.Square.shared_4, CounterPositions.Square.black_run_on_1},
            {null, CounterPositions.Square.shared_5, null},
            {null, CounterPositions.Square.shared_6, null},
            {CounterPositions.Square.white_run_off_2, CounterPositions.Square.shared_7, CounterPositions.Square.black_run_off_2},
            {CounterPositions.Square.white_run_off_1, CounterPositions.Square.shared_8, CounterPositions.Square.black_run_off_1}
    };

    protected final static CounterPositions.Square[][] HORIZONTAL_BOARD;
    static {
        int nRows = DrawnBoard.VERTICAL_BOARD.length;
        int nCols = DrawnBoard.VERTICAL_BOARD[0].length;
        HORIZONTAL_BOARD = new CounterPositions.Square[nCols][nRows];

        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                DrawnBoard.HORIZONTAL_BOARD[j][i] = DrawnBoard.VERTICAL_BOARD[i][j];
            }
        }
    }

    final CounterPositions counterPositions;

    public DrawnBoard(CounterPositions counterPositions) {
        this.counterPositions = counterPositions;

    }

    public List<String> horizontalFullBoardStrings() {
        Deque<String> lines = new ArrayDeque<>(horizontalSmallBoardStrings());
        lines.addFirst(counterPositions.countersHorizontal(Team.white));
        lines.addLast(counterPositions.countersHorizontal(Team.black));
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

    public List<List<BoardPart>> board(CounterPositions.Square[][] xxx) {
        return Arrays.stream(xxx)
                .map(l -> Arrays.stream(l)
                        .map(square -> {
                            if (null == square) {
                                return BoardPart.space;
                            }
                            return BoardPart.from(square, counterPositions.getCounters().get(square));
                        })
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    public enum BoardPart {
        // FIXME: Damon, add new parts, * for roll again
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

        public static BoardPart from(CounterPositions.Square square, Team team) {
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
