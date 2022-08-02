package web

import com.damon140.ur.Square
import com.damon140.ur.Team

class PlayerSetup(lastMove: LastMove) {

    private val lastMove: LastMove

    init {
        this.lastMove = lastMove
    }

    interface InputSupplier {
        fun waitForPlayer(): Boolean
        fun choose(moves: Map<Square, Square>): String
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
            Team.black -> BadAi1()
        }
    }

    private inner class HtmlSupplier : InputSupplier {
        override fun waitForPlayer(): Boolean {
            return !lastMove.hasLastChosen()
        }

        override fun choose(moves: Map<Square, Square>): String {
            return lastMove.getLastChosen()
        }

        override fun isHuman(): Boolean {
            return true
        }
    }

    private inner class BadAi1 : InputSupplier {
        override fun waitForPlayer(): Boolean {
            return false
        }

        override fun choose(unused: Map<Square, Square>): String {
            // FIXME: do "AI" sorting here with
            //playArea.moveTakes(team: Team, fromSquare: Square, toSquare: Square)
            //playArea.moveIsOnShareRace(team: Team, fromSquare: Square, toSquare: Square)

            // always take the first move
            return "1"
        }

        override fun isHuman(): Boolean {
            return false
        }
    }

}
