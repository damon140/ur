package ur

import com.damon140.ur.PlayArea
import com.damon140.ur.Square
import com.damon140.ur.Team
import web.PlayerSetup

class AiWithLevels(playArea: PlayArea) : PlayerSetup.InputSupplier {

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

    class AiCompare(playArea: PlayArea, level: Int): Comparator<Map.Entry<Square, Square>> {
        val playArea = playArea
        val level = level

        override fun compare(a: Map.Entry<Square, Square>, b: Map.Entry<Square, Square>): Int {
            return score(b.key, b.value) - score(a.key, a.value)
        }

        private fun score(fromSquare: Square, toSquare: Square) : Int {
            // FIXME: need a better rule here
            if (level > 3 && fromSquare.isSafeSquare && fromSquare.rollAgain()) {
                return -100 // don't move great piece
            }
            if (level > 2 && playArea.moveIsOnShareRace(Team.black, fromSquare, toSquare)) {
                return 3
            }
            if (level > 1 && playArea.moveTakes(Team.black, fromSquare, toSquare)) {
                return 2
            }
            if (level > 0 && toSquare.rollAgain()) {
                return 1
            }

            return 0
        }
    }

}