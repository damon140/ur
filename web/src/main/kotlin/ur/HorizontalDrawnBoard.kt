package com.damon140.ur

import com.damon140.ur.Square.*
import com.damon140.ur.Team.*

class HorizontalDrawnBoard(playArea: PlayArea) {
    val playArea: PlayArea

    init {
        this.playArea = playArea
    }




    fun fullBoard(): List<String> {
        val lines: ArrayDeque<String> = ArrayDeque(smallBoard())
        lines.addFirst(countersLine(white))
        lines.addLast(countersLine(black))
        return lines.toList()
    }

    fun countersLine(team: Team): String {
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
                l.toList()
                    .map { b -> b.ch }
                    .joinToString("")
            }
            .toList()
    }

    fun verticleBoard2(): List<String> {
        return board(VERTICAL_BOARD)
            .map { l ->
                l.toList()
                    .map { b -> b.ch }
                    .joinToString("")
            }
            .toList()
    }

    // FIXME: Damon move to new class
    fun verticalBoard(): List<List<BoardPart>> {
        return board(VERTICAL_BOARD)
    }

    fun board(xxx: Array<Array<Square?>>): List<List<BoardPart>> {
        return xxx.map { l ->
            l.map { square ->
                if (null == square) {
                    return@map BoardPart.space
                } else {
                    return@map BoardPart.from(square, playArea.get(square))
                }
            }.toList()
        }.toList()
    }

    enum class BoardPart(public val ch: String) {
        white(Team.white.ch), black(Team.black.ch), star("*"), empty("."), space(" ");

        fun isChar(c: String?): Boolean {
            return ch.equals(c)
        }

        companion object {
            fun from(square: Square, team: Team?): BoardPart {
                // need teams first so that we draw a w in precedence to a * or .
                if (Team.white === team) {
                    return white
                }
                if (Team.black === team) {
                    return black
                }
                return if (square.rollAgain()) {
                    star
                } else empty
            }
        }
    }

    companion object {
        const val COUNTER_START_SEPARATOR = "|"

        fun parsePlayAreaFromHorizontal(game: String): PlayArea {
            val lines = game.split("\n").toList()
            assertLineCount(white, lines)
            assertLineCount(black, lines)

            val deque: ArrayDeque<String> = ArrayDeque(lines)

            val whiteLine: String = deque.removeFirst()
            val blackLine: String = deque.removeLast()
            val c = PlayArea()
            parseAndBuildCompletedCounters(blackLine, c, black)
            parseAndBuildCompletedCounters(whiteLine, c, white)

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
            val matchChar = team.ch.first();
            require(PlayArea.COUNTERS_PER_PLAYER == deque
                .map { l: String ->
                    l.toList().filter { c -> c.equals(matchChar) }.size
                }
                .sum()
            ) { "Wrong number of counters for " + team.name }
        }

        private fun extracted(maybeSparseBoard: List<Square?>, row: String, playArea: PlayArea) {
            val boardRow: List<Square> = maybeSparseBoard.filter { s -> s != null }.toList() as List<Square>
            val chars: List<String> = row.toList()
                .map { c -> "" + c }
                .filter { c ->  !BoardPart.space.isChar(c) }
                .toList()
            val topRow: Map<Square, String> = zipToMap<Square, String>(boardRow, chars)
            topRow.entries
                .filter { e -> Team.isTeamChar(e.value) }
                .forEach { e ->
                    val square: Square = e.key
                    val team: Team = Team.fromCh(e.value)
                    playArea.move(off_board_unstarted, square, team)
                }
        }

        fun <K, V> zipToMap(keys: List<K>, values: List<V>): Map<K, V> {
            if (keys.size < values.size) {
                throw Exception("keys list too small");
            }
            if (keys.size > values.size) {
                throw Exception("values list too small");
            }

            if (keys.size != values.size) {
                throw Exception("size mismach");
            }

            return (0..(keys.size - 1))
                .map {keys.get(it) to values.get(it)}
                .toMap()
        }

        private fun parseAndBuildCompletedCounters(line: String, c: PlayArea, white: Team) {
            require(line.contains(COUNTER_START_SEPARATOR)) { "Missing counter separator $COUNTER_START_SEPARATOR" }

            var result = 0
            if (!line.contains(COUNTER_START_SEPARATOR)) {
                throw Exception("No separator " + COUNTER_START_SEPARATOR);
            }

            val deque: ArrayDeque<String> = ArrayDeque(
                line.replace(" ", "").split(
                    COUNTER_START_SEPARATOR
                ))

            // no completed counters are not started
            if (1 != deque.size) {
                result = deque.last().length
            }
            (0..(result- 1))
                .forEach { _ -> c.move(off_board_unstarted, off_board_finished, white) }
        }

        protected val VERTICAL_BOARD: Array<Array<Square?>> = arrayOf(
            arrayOf<Square?>(white_run_on_4, shared_1, black_run_on_4),
            arrayOf<Square?>(white_run_on_3, shared_2, black_run_on_3),
            arrayOf<Square?>(white_run_on_2, shared_3, black_run_on_2),
            arrayOf<Square?>(white_run_on_1, shared_4, black_run_on_1),
            arrayOf<Square?>(null, shared_5, null),
            arrayOf<Square?>(null, shared_6, null),
            arrayOf<Square?>(white_run_off_2, shared_7, black_run_off_2),
            arrayOf<Square?>(white_run_off_1, shared_8, black_run_off_1)
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