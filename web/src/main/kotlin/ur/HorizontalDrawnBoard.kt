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

package com.damon140.ur

import com.damon140.ur.Square.*
import com.damon140.ur.Team.*

class HorizontalDrawnBoard(playArea: PlayArea) {
    private val playArea: PlayArea

    init {
        this.playArea = playArea
    }




    fun fullBoard(): List<String> {
        val lines: ArrayDeque<String> = ArrayDeque(smallBoard())
        lines.addFirst(countersLine(White))
        lines.addLast(countersLine(Black))
        return lines.toList()
    }

    private fun countersLine(team: Team): String {
        val completed: Int = playArea.completedCount(team)
        val unstarted: Int = PlayArea.COUNTERS_PER_PLAYER - playArea.inPlayCount(team) - completed
        val padding: Int = 1 + PlayArea.COUNTERS_PER_PLAYER - completed - unstarted
        val teamCh: String = team.ch
        return (teamCh.repeat(unstarted)
                + " ".repeat(padding - 1)
                + COUNTER_START_SEPARATOR
                + teamCh.repeat(completed))
    }

    fun smallBoard(): List<String> {
        return board(HORIZONTAL_BOARD)
            .map { l ->
                l.toList().joinToString("") { b -> b.ch }
            }
            .toList()
    }

    private fun board(xxx: Array<Array<Square?>>): List<List<BoardPart>> {
        return xxx.map { l ->
            l.map { square ->
                if (null == square) {
                    return@map BoardPart.Space
                } else {
                    return@map BoardPart.from(square, playArea[square])
                }
            }.toList()
        }.toList()
    }

    enum class BoardPart(val ch: String) {
        White(Team.White.ch), Black(Team.Black.ch), Star("*"), Empty("."), Space(" ");

        fun isChar(c: String?): Boolean {
            // TODO: put back constructor and add lowercasing then remove here
            return ch.lowercase() == c
        }

        companion object {
            fun from(square: Square, team: Team?): BoardPart {
                // need teams first so that we draw a w in precedence to a * or .
                if (Team.White === team) {
                    return White
                }
                if (Team.Black === team) {
                    return Black
                }
                return if (square.rollAgain()) {
                    Star
                } else Empty
            }
        }
    }

    companion object {
        const val COUNTER_START_SEPARATOR = "|"

        fun parsePlayAreaFromHorizontal(game: String): PlayArea {
            val lines = game.split("\n").toList()
            assertLineCount(White, lines)
            assertLineCount(Black, lines)

            val deque: ArrayDeque<String> = ArrayDeque(lines)

            val whiteLine: String = deque.removeFirst()
            val blackLine: String = deque.removeLast()
            val c = PlayArea(White)
            parseAndBuildCompletedCounters(blackLine, c, Black)
            parseAndBuildCompletedCounters(whiteLine, c, White)

            // TODO: tidy & shrink
            val topBoard: List<Square?> = HORIZONTAL_BOARD[0].toList()
            val topHozRow: String = deque.removeFirst()
            val midBoard: List<Square?> = HORIZONTAL_BOARD[1].toList()
            val midHozRow: String = deque.removeFirst()
            val botBoard: List<Square?> = HORIZONTAL_BOARD[2].toList()
            val botHozRow: String = deque.removeFirst()
            extracted(topBoard, topHozRow, c)
            extracted(midBoard, midHozRow, c)
            extracted(botBoard, botHozRow, c)

            return c
        }

        private fun assertLineCount(team: Team, deque: List<String>) {
            val matchChar = team.ch.first()
            require(PlayArea.COUNTERS_PER_PLAYER == deque.sumOf { l: String ->
                l.toList().filter { c -> c == matchChar }.size
            }
            ) { "Wrong number of counters for " + team.name }
        }

        private fun extracted(maybeSparseBoard: List<Square?>, row: String, playArea: PlayArea) {
            val boardRow: List<Square> = maybeSparseBoard.filterNotNull().toList()
            val chars: List<String> = row.toList()
                .map { c -> "" + c }
                .filter { c ->  !BoardPart.Space.isChar(c) }
                .toList()
            val topRow: Map<Square, String> = zipToMap<Square, String>(boardRow, chars)
            topRow.entries
                .filter { e -> Team.isTeamChar(e.value) }
                .forEach { e ->
                    val square: Square = e.key
                    val team: Team = Team.fromCh(e.value)
                    playArea.move(Off_board_unstarted, square, team)
                }
        }

        private fun <K, V> zipToMap(keys: List<K>, values: List<V>): Map<K, V> {
            if (keys.size < values.size) {
                throw Exception("keys list too small")
            }
            if (keys.size > values.size) {
                throw Exception("values list too small")
            }

            if (keys.size != values.size) {
                throw Exception("size mismach")
            }

            return (keys.indices).associate { keys[it] to values[it] }
        }

        private fun parseAndBuildCompletedCounters(line: String, c: PlayArea, white: Team) {
            require(line.contains(COUNTER_START_SEPARATOR)) { "Missing counter separator $COUNTER_START_SEPARATOR" }

            var result = 0
            if (!line.contains(COUNTER_START_SEPARATOR)) {
                throw Exception("No separator $COUNTER_START_SEPARATOR")
            }

            val deque: ArrayDeque<String> = ArrayDeque(
                line.replace(" ", "").split(
                    COUNTER_START_SEPARATOR
                ))

            // no completed counters are not started
            if (1 != deque.size) {
                result = deque.last().length
            }
            (0 until result)
                .forEach { _ -> c.move(Off_board_unstarted, Off_board_finished, white) }
        }

        private val VERTICAL_BOARD: Array<Array<Square?>> = arrayOf(
            arrayOf(White_run_on_4, Shared_1, Black_run_on_4),
            arrayOf(White_run_on_3, Shared_2, Black_run_on_3),
            arrayOf(White_run_on_2, Shared_3, Black_run_on_2),
            arrayOf(White_run_on_1, Shared_4, Black_run_on_1),
            arrayOf(null, Shared_5, null),
            arrayOf(null, Shared_6, null),
            arrayOf(White_run_off_2, Shared_7, Black_run_off_2),
            arrayOf(White_run_off_1, Shared_8, Black_run_off_1)
        )
        private val HORIZONTAL_BOARD: Array<Array<Square?>>

        init {
            val nRows = VERTICAL_BOARD.size
            val nCols = VERTICAL_BOARD[0].size
            HORIZONTAL_BOARD = Array<Array<Square?>>(nCols) { arrayOfNulls<Square?>(nRows) }
            for (i in 0 until nRows) {
                for (j in 0 until nCols) {
                    HORIZONTAL_BOARD[j][i] = VERTICAL_BOARD[i][j]
                }
            }
        }
    }
}