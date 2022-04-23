package com.damon140.ur;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.damon140.ur.Square.*;

public class HorizontalDrawnBoard {

    public static final String COUNTER_START_SEPARATOR = "|";
    public static final String COUNTER_START_SEPARATOR_PATTERN = Pattern.quote(COUNTER_START_SEPARATOR);

    public static PlayArea parsePlayAreaFromHorizontal(String game) throws NoSuchAlgorithmException {
        Deque<String> deque = Arrays.stream(game.split("\n")).collect(Collectors.toCollection(ArrayDeque::new));
        String whiteLine = deque.removeFirst();
        String blackLine = deque.removeLast();

        Counters c = new Counters();

        parseAndBuildCompletedCounteres(blackLine, c, Team.black);
        parseAndBuildCompletedCounteres(whiteLine, c, Team.white);

        // TODO: tidy & shrink
        List<Square> topBoard = Arrays.stream(DrawnBoard.HORIZONTAL_BOARD[0]).toList();
        String topHozRow = deque.removeFirst();

        List<Square> midBoard = Arrays.stream(DrawnBoard.HORIZONTAL_BOARD[1]).toList();
        String midHozRow = deque.removeFirst();

        List<Square> botBoard = Arrays.stream(DrawnBoard.HORIZONTAL_BOARD[2]).toList();
        String botHozRow = deque.removeFirst();

        extracted(topBoard, topHozRow, c);
        extracted(midBoard, midHozRow, c);
        extracted(botBoard, botHozRow, c);

        return c;
    }

    private static void extracted(List<Square> maybeSparseBoard, String row, Counters counters) {
        List<Square> boardRow = maybeSparseBoard.stream().filter(Objects::nonNull).toList();
        List<String> chars = row.chars().mapToObj(Character::toString)
                .filter(c -> {
                    boolean equals = DrawnBoard.BoardPart.space.isChar(c);
                    return !equals;
                })
                .toList();
        Map<Square, String> topRow = zipToMap(boardRow, chars);
        topRow.entrySet().stream()
                .filter(e -> Team.isTeamChar(e.getValue()))
                .forEach(e -> {
                    Square square = e.getKey();
                    Team team = Team.fromCh(e.getValue());
                    counters.move(off_board_unstarted, square, team);
                });
    }

    public static <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
        return IntStream.range(0, keys.size()).boxed()
                .collect(Collectors.toMap(keys::get, values::get));
    }


    private static void parseAndBuildCompletedCounteres(String blackLine, Counters c, Team white) {
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
        int nRows = HorizontalDrawnBoard.VERTICAL_BOARD.length;
        int nCols = HorizontalDrawnBoard.VERTICAL_BOARD[0].length;
        HORIZONTAL_BOARD = new Square[nCols][nRows];

        for (int i = 0; i < nRows; i++) {
            for (int j = 0; j < nCols; j++) {
                HorizontalDrawnBoard.HORIZONTAL_BOARD[j][i] = HorizontalDrawnBoard.VERTICAL_BOARD[i][j];
            }
        }
    }

    final PlayArea playArea;

    public DrawnBoard(PlayArea playArea) {
        this.playArea = playArea;
    public HorizontalDrawnBoard(Counters counters) {
        this.counters = counters;

    }

    public List<String> fullBoard() {
        Deque<String> lines = new ArrayDeque<>(smallBoard());
        lines.addFirst(countersLine(Team.white));
        lines.addLast(countersLine(Team.black));
        return lines.stream().toList();
    }

    public String countersLine(Team team) {
        int completed = playArea.completedCount(team);
        int unstarted = PlayArea.COUNTERS_PER_PLAYER - playArea.inPlayCount(team) - completed;
        int padding = 1 + PlayArea.COUNTERS_PER_PLAYER - completed - unstarted;

        String teamCh = team.ch;
        return teamCh.repeat(unstarted) + " ".repeat(padding-1) + HorizontalDrawnBoard.COUNTER_START_SEPARATOR + teamCh.repeat(completed);
    }

    public List<String> smallBoard() {
        return board(HORIZONTAL_BOARD).stream()
                .map(l -> l.stream()
                        .map(b -> b.ch)
                        .collect(Collectors.joining("")))
                .toList();
    }

    // FIXME: Damon move to new class
    public List<List<BoardPart>> verticalBoard() {
        return board(HorizontalDrawnBoard.VERTICAL_BOARD);
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
