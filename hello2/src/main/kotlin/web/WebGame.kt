package web

import com.damon140.ur.*
import kotlinx.browser.document

class WebGame {

    private val dice = Dice()
    private val playArea = PlayArea()
    private var lastMove = LastMove()
    private val ur: Ur = Ur(playArea)
    private var pageObject = UrPageObject(document)
    private var urCanvasView = UrCanvasView(lastMove, pageObject)
    // TODO: impl player setup
    private var playerSetup: PlayerSetup = PlayerSetup(lastMove)
    private var moveSuppliers: HashMap<Team, PlayerSetup.InputSupplier> = hashMapOf(
        Team.white to playerSetup.getPlayer(Team.white),
        Team.black to playerSetup.getPlayer(Team.black)
    )

    fun play() {
        playUr(0, false)
    }

    private fun playUr(humansRoll: Int, isCallBackIn: Boolean) {
        var isCallBack = isCallBackIn
        while (true) {
            val currentTeam = ur.currentTeam()
            var roll: Int

            if (isCallBack) {
                roll = humansRoll
            } else {
                roll = dice.roll()
                lastMove.clearLastChosen()
            }

            val moves: Map<Square, Square> = ur.askMoves(currentTeam, roll)
            val continueFunction = {
                if (0 == roll || moves.isEmpty()) {
                    // skip processing of human roll, nothing to do
                    playUr(roll, false)
                } else {
                    // regular turn
                    playUr(roll, true)
                }
            }

            // UI iteration 3!
            urCanvasView.blank()
            urCanvasView.drawGrid(moves, continueFunction)
            urCanvasView.updateWhiteCounters(playArea.unstartedCount(Team.white), playArea.completedCount(Team.white))
            urCanvasView.updateBlackCounters(playArea.unstartedCount(Team.black), playArea.completedCount(Team.black))
            urCanvasView.updateBoard(playArea)


            // FIXME: need central event routing game thingy that checks for sound playing
            // HTMLMediaElement.paused


            // FIXME: fix bug with taking other team's counter using console.log

            urCanvasView.updateInstructions(currentTeam, roll, continueFunction)

            val moveSupplier = moveSuppliers.get(currentTeam)!!

            if (moveSupplier.waitForPlayer()) {
                console.log("Waiting for input of player $currentTeam")
                return
            }

            val input = moveSupplier.choose(moves)

            console.log("Making turn of $currentTeam")
            console.log("Moves are: ")

            //moves.entries.forEach { e -> console.log(e.key.name + " -> " + e.value.name) }
            console.log("player input was $input")

            if (0 == roll) {
                ur.skipTurn(roll)
                continue
            }

            if (moves.isEmpty()) {
                ur.skipTurn(roll)
                continue
            }

            if ("x".equals(input)) {
                return
            }

            val moveIndex: Int = input.toInt()
            val fromSquare: Square = moves.keys.toList()[moveIndex - 1]

            val result = ur.moveCounter(fromSquare, roll)

            // FIXME: game over bug here for black
            if (result == Ur.MoveResult.gameOver) {
                //System.out.println("Game won by " + playArea.currentTeam());
                // TODO: add game alert here
                return
            }
            isCallBack = false
        }
    }

}