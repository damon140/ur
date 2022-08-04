package web

import com.damon140.ur.PlayArea
import com.damon140.ur.Square
import com.damon140.ur.Team
import com.damon140.ur.Team.black

class PlayerSetup(playArea: PlayArea, lastMove: LastMove) {

    private val playArea: PlayArea
    private val lastMove: LastMove

    init {
        this.playArea = playArea
        this.lastMove = lastMove
    }

    interface InputSupplier {
        fun waitForPlayer(): Boolean
        fun choose(level: Int, moves: Map<Square, Square>): Int
        fun isHuman(): Boolean
    }

    fun getPlayer(team: Team): InputSupplier {
        // TODO: need to read from web here
        // urView....

//        println("Player for $team will be")
//        println("1 human via console")
//        println("2 computer bad ai 1")
//        return when (scanner.next()) {
//            "1" -> ConsoleSupplier(scanner)
//            "2" -> BadAi1()
//            else -> null
//        }

        return when(team) {
            Team.white -> HtmlSupplier()
            black -> BadAi1(playArea)
        }
    }

    private inner class HtmlSupplier : InputSupplier {
        override fun waitForPlayer(): Boolean {
            return !lastMove.hasLastChosen()
        }

        override fun choose(unused: Int, moves: Map<Square, Square>): Int {
            // TODO: change stored to Int
            return lastMove.getLastChosen().toInt()
        }

        override fun isHuman(): Boolean {
            return true
        }
    }

    private inner class AiCompare(playArea: PlayArea, level: Int): Comparator<Map.Entry<Square, Square>> {
        val playArea = playArea
        val level = level

        override fun compare(a: Map.Entry<Square, Square>, b: Map.Entry<Square, Square>): Int {
            return score(b.key, b.value) - score(a.key, a.value)
        }

        fun score(fromSquare: Square, toSquare: Square) : Int {
            if (level > 3 && fromSquare.isSafeSquare && fromSquare.rollAgain()) {
                return -100 // don't move great piece
            }
            if (level > 2 && playArea.moveIsOnShareRace(black, fromSquare, toSquare)) {
                return 3
            }
            if (level > 1 && playArea.moveTakes(black, fromSquare, toSquare)) {
                return 2
            }
            if (level > 0 && toSquare.rollAgain()) {
                return 1
            }

            return 0
        }
    }

    private inner class BadAi1(playArea: PlayArea) : InputSupplier {
        val playArea = playArea

        override fun waitForPlayer(): Boolean {
            return false
        }

        override fun choose(level: Int, moves: Map<Square, Square>): Int {
            val first = moves.entries
                .sortedWith(AiCompare(playArea, level))
                .first()
            console.log("AI best move is $first given level $level")

            return 1 + moves.keys.indexOf(first.key)
        }

        override fun isHuman(): Boolean {
            return false
        }
    }

}
