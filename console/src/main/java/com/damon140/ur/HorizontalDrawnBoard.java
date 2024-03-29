/*
 * Copyright 2023 Damon van Opdorp
 *
 * Licensed under GNU General Public License v3.0.
 *
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import static com.damon140.ur.Team.*;

public class HorizontalDrawnBoard {

    public static final String COUNTER_START_SEPARATOR = "|";
    public static final String COUNTER_START_SEPARATOR_PATTERN = Pattern.quote(COUNTER_START_SEPARATOR);

    public static PlayArea parsePlayAreaFromHorizontal(String game) throws NoSuchAlgorithmException {
        Deque<String> deque = Arrays.stream(game.split("\n")).collect(Collectors.toCollection(ArrayDeque::new));
        assertLineCount(white, deque);
        assertLineCount(black, deque);

        String whiteLine = deque.removeFirst();
        String blackLine = deque.removeLast();

        PlayArea c = new PlayArea();

        parseAndBuildCompletedCounteres(blackLine, c, black);
        parseAndBuildCompletedCounteres(whiteLine, c, white);

        // TODO: tidy & shrink
        List<Square> topBoard = Arrays.stream(HORIZONTAL_BOARD[0]).toList();
        String topHozRow = deque.removeFirst();

        List<Square> midBoard = Arrays.stream(HORIZONTAL_BOARD[1]).toList();
        String midHozRow = deque.removeFirst();

        List<Square> botBoard = Arrays.stream(HORIZONTAL_BOARD[2]).toList();
        String botHozRow = deque.removeFirst();

        extracted(topBoard, topHozRow, c);
        extracted(midBoard, midHozRow, c);
        extracted(botBoard, botHozRow, c);

        return c;
    }

    private static void assertLineCount(Team team, Deque<String> deque) {
        var matchChar = team.ch.codePointAt(0);
        if (PlayArea.COUNTERS_PER_PLAYER != deque.stream()
                .mapToLong(l -> l.codePoints().filter(c -> c == matchChar).count())
                .sum()) {
            throw new IllegalArgumentException("Wrong number of counters for " + team.name());
        }
    }

    private static void extracted(List<Square> maybeSparseBoard, String row, PlayArea playArea) {
        List<Square> boardRow = maybeSparseBoard.stream().filter(Objects::nonNull).toList();
        List<String> chars = row.chars().mapToObj(Character::toString)
                .filter(c -> {
                    boolean equals = HorizontalDrawnBoard.BoardPart.space.isChar(c);
                    return !equals;
                })
                .toList();
        Map<Square, String> topRow = zipToMap(boardRow, chars);
        topRow.entrySet().stream()
                .filter(e -> isTeamChar(e.getValue()))
                .forEach(e -> {
                    Square square = e.getKey();
                    Team team = fromCh(e.getValue());
                    playArea.move(off_board_unstarted, square, team);
                });
    }

    public static <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
        return IntStream.range(0, keys.size()).boxed()
                .collect(Collectors.toMap(keys::get, values::get));
    }


    private static void parseAndBuildCompletedCounteres(String counterLine, PlayArea c, Team white) {

        if (!counterLine.contains(COUNTER_START_SEPARATOR)) {
            throw new IllegalArgumentException("Missing counter separator " + COUNTER_START_SEPARATOR);
        }

        int result = 0;
        ArrayDeque<String> deque = Arrays.stream(counterLine.replaceAll(" ", "").split(COUNTER_START_SEPARATOR_PATTERN))
                .collect(Collectors.toCollection(ArrayDeque::new));
        if (deque.isEmpty()) {
            // no counters are off-board, all on board
            return;
        }

        // none completed, counters are not started
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

    public HorizontalDrawnBoard(PlayArea playArea) {
        this.playArea = playArea;
    }

    public List<String> fullBoard() {
        Deque<String> lines = new ArrayDeque<>(smallBoard());
        lines.addFirst(countersLine(white));
        lines.addLast(countersLine(black));
        return lines.stream().toList();
    }

    public String countersLine(Team team) {
        int completed = playArea.completedCount(team);
        int unstarted = (PlayArea.COUNTERS_PER_PLAYER - playArea.inPlayCount(team)) - completed;
        int padding = 1 + PlayArea.COUNTERS_PER_PLAYER - completed - unstarted;

        String teamCh = team.ch;
        return teamCh.repeat(unstarted)
                + " ".repeat(padding-1)
                + HorizontalDrawnBoard.COUNTER_START_SEPARATOR
                + teamCh.repeat(completed);
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
