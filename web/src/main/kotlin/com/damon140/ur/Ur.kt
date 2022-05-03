package com.damon140.ur

class Ur(private val playArea: PlayArea) {
    fun currentTeam(): Team {
        return playArea.currentTeam()
    }

    fun skipTurn(count: Int): Boolean {
        if (0 != count) {
            return false
        }
        playArea.swapTeam()
        return true
    }

    enum class MoveResult {
        illegal, legal, gameOver
    }

    fun moveCounter(square: Square, count: Int): MoveResult {
        return moveCounter(playArea.currentTeam(), square, count)
    }

    fun moveCounter(team: Team, fromSquare: Square, count: Int): MoveResult {
        // FIXMME: check correct team
        if (playArea.allCountersStarted(team)) {
            return MoveResult.illegal // can't add any more counters
        }
        if (fromSquare !== Square.off_board_unstarted && playArea.occupied(fromSquare)
            && playArea[fromSquare] !== team
        ) {
            return MoveResult.illegal // teams counter not on square to move from
        }
        val newSquare: Square? = fromSquare.calculateNewSquare(team, count)
        if (null == newSquare) {
            return MoveResult.illegal
        }

        if (0 == count) {
            return MoveResult.illegal // illegal move of zero
        }
        val occupant = playArea[newSquare]

        // FIXME: Damon safe square logic needed here
        if (null != occupant) {
            if (team === occupant) {
                return MoveResult.illegal // clashes with own counter
            } else {
                // FIXME: to an unstarted counter change??
            }
        }

        // move counter
        playArea.move(fromSquare, newSquare, team)
        if (playArea.allCompleted(team)) {
            return MoveResult.gameOver
        }
        if (newSquare.dontRollAgain()) {
            playArea.swapTeam()
        }
        return MoveResult.legal
    }

    // TODO: upgrade to object with properties for use by "AI"
    // takes_other, rolls_again, safe
    fun askMoves(team: Team, roll: Int): Map<Square, Square?> {
        val moves: MutableMap<Square, Square?> = HashMap()
        if (!playArea.allStartedOrComplete(team)) {
            // start a new counter
            moves[Square.off_board_unstarted] =
                canUseOrNull(team, Square.off_board_unstarted.calculateNewSquare(team, roll))
        }

        // current counters
        playArea.countersForTeam(team)
            .forEach { startSquare ->
                val endSquare = canUseOrNull(team, startSquare.calculateNewSquare(team, roll))
                moves[startSquare] = endSquare
            }
        moves.values.removeIf(java.util.function.Predicate<Square> { obj: Any? -> Objects.isNull(obj) })
        return moves
    }

    private fun canUseOrNull(team: Team, square: Square?): Square? {
        if (square.isEmpty()) {
            return null
        }
        // if empty
        if (!playArea.occupied(square)) {
            return square
        }
        // or other counter and not a safe square
        val occupantTeam = playArea[square]
        return if (occupantTeam !== team && square.isSafeSquare) {
            square
        } else null
    }
}