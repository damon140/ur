package com.damon140.ur

enum class Square {
    off_board_unstarted,  // synthetic square, not a square on the board
    black_run_on_1, black_run_on_2, black_run_on_3, black_run_on_4, black_run_off_1, black_run_off_2, shared_1, shared_2, shared_3, shared_4, shared_5, shared_6, shared_7, shared_8, white_run_on_1, white_run_on_2, white_run_on_3, white_run_on_4, white_run_off_1, white_run_off_2, off_board_finished;

    companion object {
        fun drawableSquares(): List<Square> {
            return Square.values().toList().filter { v -> v != off_board_finished && v!= off_board_unstarted }
        }

        // FIXME: Damon unit test
        fun calculateSquaresBetween(team: Team, startSquare: Square, endSquare: Square): MutableList<Square> {
            var currentSquare = startSquare
            val squares = ArrayList<Square>()
            squares.add(startSquare)

            while(currentSquare != endSquare && currentSquare != off_board_finished) {
                currentSquare = currentSquare.calculateNewSquare(team)
                squares.add(currentSquare)
            }
            return squares
        }
    }

    // TODO: switch to new illegal_square square instead of opt??
    fun calculateNewSquare(team: Team, count: Int): Square? {
        if (this == off_board_finished) {
            return off_board_finished;
        }
        var newSquare = this
        for (looper in 0 until count) {
            if (off_board_finished == newSquare) {
                return null;
            }
            newSquare = newSquare.calculateNewSquare(team)
        }
        return newSquare;
    }

    // synthetic
    fun calculateNewSquare(team: Team): Square {
        return when (this) {
            black_run_on_4, white_run_on_4 -> shared_1
            shared_8 -> if (team === Team.black) black_run_off_1 else white_run_off_1
            black_run_off_2, white_run_off_2 -> off_board_finished
            off_board_unstarted -> if (team === Team.black) black_run_on_1 else white_run_on_1
            else -> values()[1 + ordinal]
        }
    }

    fun dontRollAgain(): Boolean {
        return !rollAgain()
    }

    val isSafeSquare: Boolean
        get() = shared_4 == this

    fun rollAgain(): Boolean {
        return setOf(black_run_on_4, white_run_on_4, shared_4, black_run_off_2, white_run_off_2)
            .contains(this)
    }
}