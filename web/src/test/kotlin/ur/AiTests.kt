package ur

import com.damon140.ur.*
import web.PlayerSetup
import kotlin.test.Test

class AiTests {

    private fun getPlayer(playArea: PlayArea, team: Team): PlayerSetup.InputSupplier {
        // TODO: pull out?
        val aiWithLevels = AiWithLevels(playArea)

        return when(team) {
            Team.White -> aiWithLevels
            Team.Black -> aiWithLevels
        }
    }

    @Test
    fun simpleVsSimple() {
        val teamLevel1 = 0 // simple level
        val teamLevel2 = 2 // simple level
        
        val pair = playGame(teamLevel1, teamLevel2)
        val team: Team = pair.first
        val playArea = pair.second

        console.log("Team won was: $team")
        val board = HorizontalDrawnBoard(playArea)
        console.log(board.fullBoard().joinToString("\n"))
    }

    private fun playGame(
        teamLevel1: Int,
        teamLevel2: Int
    ): Pair<Team, PlayArea> {
        val levels: Map<Team, Int> = hashMapOf(
            Team.White to teamLevel1,
            Team.Black to teamLevel2
        )

        val dice = Dice()
        var moveResult: Ur.MoveResult = Ur.MoveResult.Legal // dummy start value
        var team: Team = Team.White
        val playArea = PlayArea(team)
        val ur = Ur(playArea)

        while (moveResult != Ur.MoveResult.GameOver) {
            team = playArea.currentTeam()
            dice.roll()
            val roll = dice.getLastValue()

            val moves = ur.askMoves(team, roll)
            val skipTurn = 0 == roll || moves.isEmpty()

            if (skipTurn) {
                ur.skipTurn()
                continue
            }

            val player = getPlayer(playArea, team)
            val choice = player.choose(levels.getValue(team), moves)

            val fromSquare: Square = moves.keys.toList()[choice - 1]
            moveResult = ur.moveCounter(fromSquare, choice)
        }
        return Pair(team, playArea)
    }
}
