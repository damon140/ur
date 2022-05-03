package com.damon140.ur

class HorizontalDrawnBoard(val playArea: PlayArea) {
    fun fullBoard(): List<String> {
        val lines: ArrayDeque<String> = ArrayDeque(smallBoard())
        lines.addFirst(countersLine(Team.white))
        lines.addLast(countersLine(Team.black))
        return lines.toList()
    }

    fun countersLine(team: Team): String {
        val completed = playArea.completedCount(team)
        val unstarted = PlayArea.COUNTERS_PER_PLAYER - playArea.inPlayCount(team) - completed
        val padding = 1 + PlayArea.COUNTERS_PER_PLAYER - completed - unstarted
        val teamCh = team.ch
        return (teamCh.repeat(unstarted)
                + " ".repeat(padding - 1)
                + COUNTER_START_SEPARATOR
                + teamCh.repeat(completed))
    }

    fun smallBoard(): List<String> {
        return board(HORIZONTAL_BOARD)
            .map<String>(java.util.function.Function<List<BoardPart>, String> { l: List<BoardPart?> ->
                l.stream()
                    .map<String>(java.util.function.Function<BoardPart, String> { b: BoardPart -> b.ch })
                    .collect(Collectors.joining(""))
            })
            .toList()
    }

    // FIXME: Damon move to new class
    fun verticalBoard(): List<List<BoardPart>> {
        return board(VERTICAL_BOARD)
    }

    fun board(xxx: Array<Array<Square?>>?): List<List<BoardPart>> {
        return Arrays.stream<Array<Square>>(xxx)
            .map(java.util.function.Function<Array<Square>, List<BoardPart>> { l: Array<Square?>? ->
                Arrays.stream<Square>(l)
                    .map(java.util.function.Function<Square, BoardPart> { square: Square? ->
                        if (null == square) {
                            return@map BoardPart.space
                        }
                        BoardPart.from(square, playArea[square])
                    })
                    .collect(Collectors.toList())
            })
            .collect(Collectors.toList())
    }

    enum class BoardPart(private val ch: String) {
        white(Team.white.ch), black(Team.black.ch), star("*"), empty("."), space(" ");

        fun isChar(c: String): Boolean {
            return ch == c
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
        val COUNTER_START_SEPARATOR_PATTERN: String = java.util.regex.Pattern.quote(COUNTER_START_SEPARATOR)
        @Throws(NoSuchAlgorithmException::class)
        fun parsePlayAreaFromHorizontal(game: String): PlayArea {
            val deque: Deque<String> =
                Arrays.stream<String>(game.split("\n").toTypedArray()).collect<ArrayDeque<String>, Any>(
                    Collectors.toCollection<String, ArrayDeque<String>>(
                        Supplier<ArrayDeque<String>> { ArrayDeque() })
                )
            val whiteLine: String = deque.removeFirst()
            val blackLine: String = deque.removeLast()
            val c = PlayArea()
            parseAndBuildCompletedCounteres(blackLine, c, Team.black)
            parseAndBuildCompletedCounteres(whiteLine, c, Team.white)

            // TODO: tidy & shrink
            val topBoard: List<Square> = Arrays.stream<Square>(HORIZONTAL_BOARD[0]).toList()
            val topHozRow: String = deque.removeFirst()
            val midBoard: List<Square> = Arrays.stream<Square>(HORIZONTAL_BOARD[1]).toList()
            val midHozRow: String = deque.removeFirst()
            val botBoard: List<Square> = Arrays.stream<Square>(HORIZONTAL_BOARD[2]).toList()
            val botHozRow: String = deque.removeFirst()
            extracted(topBoard, topHozRow, c)
            extracted(midBoard, midHozRow, c)
            extracted(botBoard, botHozRow, c)
            return c
        }

        private fun extracted(maybeSparseBoard: List<Square>, row: String, playArea: PlayArea) {
            val boardRow: List<Square> =
                maybeSparseBoard.stream().filter(java.util.function.Predicate<Square> { obj: Any? ->
                    Objects.nonNull(
                        obj
                    )
                }).toList()
            val chars: List<String> = row.chars().mapToObj<String>(IntFunction<String> { codePoint: Int ->
                java.lang.Character.toString(
                    codePoint
                )
            })
                .filter(java.util.function.Predicate<String> { c: String ->
                    val equals = BoardPart.space.isChar(c)
                    !equals
                })
                .toList()
            val topRow = zipToMap(boardRow, chars)
            topRow.entries.stream()
                .filter(java.util.function.Predicate<Map.Entry<Square, String>> { (_, value): Map.Entry<Square?, String?> ->
                    Team.isTeamChar(
                        value!!
                    )
                })
                .forEach(java.util.function.Consumer<Map.Entry<Square, String>> { (square, value): Map.Entry<Square, String?> ->
                    val team = Team.fromCh(value!!)
                    playArea.move(Square.off_board_unstarted, square, team)
                })
        }

        fun <K, V> zipToMap(keys: List<K>, values: List<V>): Map<K, V> {
            return IntStream.range(0, keys.size).boxed()
                .collect<Map<K, V>, Any>(
                    Collectors.toMap<Int, K, V>(
                        java.util.function.Function<Int, K> { i: Int -> keys[i] },
                        java.util.function.Function<Int, V> { i: Int -> values[i] })
                )
        }

        private fun parseAndBuildCompletedCounteres(blackLine: String, c: PlayArea, white: Team) {
            var result = 0
            val deque: ArrayDeque<String> = Arrays.stream<String>(
                blackLine.replace(" ".toRegex(), "").split(
                    COUNTER_START_SEPARATOR_PATTERN
                ).toTypedArray()
            )
                .collect<ArrayDeque<String>, Any>(
                    Collectors.toCollection<String, ArrayDeque<String>>(
                        Supplier<ArrayDeque<String>> { ArrayDeque() })
                )

            // no completed counters are not started
            if (1 != deque.size) {
                result = deque.getLast().length
            }
            IntStream.range(0, result)
                .forEach(IntConsumer { x: Int -> c.move(Square.off_board_unstarted, Square.off_board_finished, white) })
        }

        protected val VERTICAL_BOARD = arrayOf(
            arrayOf<Square?>(Square.white_run_on_4, Square.shared_1, Square.black_run_on_4),
            arrayOf<Square?>(Square.white_run_on_3, Square.shared_2, Square.black_run_on_3),
            arrayOf<Square?>(Square.white_run_on_2, Square.shared_3, Square.black_run_on_2),
            arrayOf<Square?>(Square.white_run_on_1, Square.shared_4, Square.black_run_on_1),
            arrayOf(null, Square.shared_5, null),
            arrayOf(null, Square.shared_6, null),
            arrayOf<Square?>(Square.white_run_off_2, Square.shared_7, Square.black_run_off_2),
            arrayOf<Square?>(Square.white_run_off_1, Square.shared_8, Square.black_run_off_1)
        )
        private val HORIZONTAL_BOARD: Array<Array<Square?>>

        init {
            val nRows = VERTICAL_BOARD.size
            val nCols = VERTICAL_BOARD[0].size
            HORIZONTAL_BOARD = Array(com.damon140.ur.nCols) {
                arrayOfNulls<Square?>(
                    com.damon140.ur.nRows
                )
            }
            for (i in 0 until com.damon140.ur.nRows) {
                for (j in 0 until com.damon140.ur.nCols) {
                    HORIZONTAL_BOARD[j][i] = VERTICAL_BOARD[i][j]
                }
            }
        }
    }
}