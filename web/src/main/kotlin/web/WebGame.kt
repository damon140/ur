package web

import com.damon140.ur.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlin.random.Random

class WebGame {

    private var roll: Int = 0
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

    // m01 draw (show roll button)
    // m02 wait for roll button to be clicked * m03

    // m03 play sound
    // m04 set interval callback to m05

    // m05 roll button clicked <- button handler
    // m10 generate new roll
    // m11 calculate moves
    // m12 draw (show roll and show moves)
    // m13 wait for move or skipped from player *

    // m14 move selection received <- button handler or fn
    // m15 play counter move sound of wah-wah 1 2 3 4
    // m16 wait for move sound to finish *

    // m17 move sound finished <- interval
    // m18 move
    // m19 draw here??
    // m20 play counter taken or off board sound if needed
    // m21 wait for sound to finish *
    // m22 counter sound finished <- interval
    // m23 change player if needed

    fun play() {
        playM0102()
        console.log("Fell out of play method")
    }

    // m01 draw (show roll button)
    // m02 wait for roll button to be clicked *
    private fun playM0102() {
        val continueFunction = {
            console.log("About to run continue function from roll")
            playM0304()
        }

        if (currentPlayerIsHuman()) {
            urCanvasView.drawShowRollButton(playArea, continueFunction)
        } else {
            urCanvasView.drawRobotThinking(playArea)

            if (1 == Random.nextInt(1, 9)) {
                urCanvasView.playHmm()
            }

            window.setTimeout(handler = {
                console.log("Robot finished thinking here")
                playM0304()
            }, timeout =  Random.nextInt(0, 900))
        }
    }

    // m03 play sound
    // m04 set interval callback to m05
    private fun playM0304() {
        urCanvasView.playDiceRoll()

        val diceRollMs = 700
        window.setTimeout(handler = {
            playMbbbb()
        }, timeout =  diceRollMs)
    }

    private fun playMbbbb() {
        val currentTeam = ur.currentTeam()
        console.log("Current team is $currentTeam")

        // TODO: push value into object
        this.roll = dice.roll()

        val moves: Map<Square, Square> = ur.askMoves(currentTeam, roll)
        val continueFunction = {
            if (0 == roll || moves.isEmpty()) {
                // skip processing of human roll, nothing to do
                playM0ccc()
            } else {
                // regular turn
                playM0ccc()
            }
        }

        // UI iteration 3!
        urCanvasView.drawAll(currentTeam, roll, moves, playArea, continueFunction)

        if (currentPlayerIsAi()) {
            playM0ccc()
        } else {
            urCanvasView.startMovesAnimation()
        }
    }

    private fun playM0ccc() {
        urCanvasView.endMovesAnimation()

        val currentTeam = ur.currentTeam()
        val moves: Map<Square, Square> = ur.askMoves(currentTeam, roll)

        val moveSupplier = moveSuppliers.get(currentTeam)!!

        val input = moveSupplier.choose(moves)

        // console.log("Making turn of $currentTeam")
        // console.log("Moves are: ")
        //moves.entries.forEach { e -> console.log(e.key.name + " -> " + e.value.name) }
        //console.log("player input was $input")

        val skipTurn = 0 == roll || moves.isEmpty()

        if (skipTurn) {
            ur.skipTurn(roll)
            urCanvasView.drawMost(playArea)
            console.log("Skipping turn of $currentTeam. roll is $roll and moves size is ${moves.size}")

            playM0102()
            return
        }

        val moveIndex: Int = input.toInt()
        val fromSquare: Square = moves.keys.toList()[moveIndex - 1]

        val continueFunction = {
            val result = ur.moveCounter(fromSquare, roll)
            console.log("move result is $result")

            playDddd(result)
        }

        urCanvasView.animate(playArea, currentTeam, fromSquare, moves[fromSquare]!!, continueFunction)
    }

    private fun playDddd(result: Ur.MoveResult) {
        val currentTeam = ur.currentTeam()

        if (result == Ur.MoveResult.gameOver) {
            console.log("Game won by $currentTeam")
            urCanvasView.updateBoard(playArea)
            urCanvasView.gameWon(currentTeam)
            return
        }

        if (result == Ur.MoveResult.counterTaken) {
            console.log("FIXME: play taken sound here")
            urCanvasView.playCounterTakenSound()
            window.setTimeout(handler = {
                console.log("Robot finished thinking here")
                playRenderFinalMoveState()
            }, timeout = Random.nextInt(0, 500))
            return
        }

        // else
        playRenderFinalMoveState()
    }

    private fun playRenderFinalMoveState() {
        val continueFunction = {
            playM0102()
        }

        val currentTeam = ur.currentTeam()
        val moves: Map<Square, Square> = ur.askMoves(currentTeam, roll)

        urCanvasView.drawAll(currentTeam, roll, moves, playArea, continueFunction)

        console.log("playEeee: Current team is " + ur.currentTeam())

        // loop the game loop
        playM0102()
    }


    private fun currentPlayerIsHuman(): Boolean {
        val currentTeam = ur.currentTeam()
        val inputSupplier = moveSuppliers[currentTeam]!!
        return inputSupplier.isHuman()
    }

    private fun currentPlayerIsAi(): Boolean {
        return !currentPlayerIsHuman()
    }

}