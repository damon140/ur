package web

import com.damon140.ur.PlayArea
import com.damon140.ur.Square
import com.damon140.ur.Team
import com.damon140.ur.Team.black
import ur.AiWithLevels

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
            black -> AiWithLevels(playArea)
        }
    }

    private inner class HtmlSupplier : InputSupplier {
        override fun waitForPlayer(): Boolean {
            return !lastMove.hasLastChosen()
        }

        override fun choose(unused: Int, moves: Map<Square, Square>): Int {
            return lastMove.getLastChosen().toInt()
        }

        override fun isHuman(): Boolean {
            return true
        }
    }

}
