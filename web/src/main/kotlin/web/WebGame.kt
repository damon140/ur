package web

import com.damon140.ur.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlin.random.Random

class WebGame {
    private val dice = Dice()
    private var playArea = PlayArea()
    private var lastMove = LastMove()
    private var ur: Ur = Ur(playArea)
    private var pageObject = UrPageObject(document)
    private var urCanvasView = UrCanvasView(lastMove, pageObject)
    private var roll: Int = 0

    // TODO: impl player setup
    private var playerSetup: PlayerSetup = PlayerSetup(lastMove)
    private var moveSuppliers: HashMap<Team, PlayerSetup.InputSupplier> = hashMapOf(
        Team.white to playerSetup.getPlayer(Team.white),
        Team.black to playerSetup.getPlayer(Team.black)
    )

    // set up a game for testing
    fun fakeGame(game: String) {
        playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal(game)
        ur = Ur(playArea!!)
    }

    // draw (show roll button)
    // wait for roll button to be clicked *
    fun playPart1() {
        val continueFunction = {
            console.log("About to run continue function from roll")
            playPart2()
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
                playPart2()
            }, timeout =  Random.nextInt(0, 900))
        }
    }

    // play ZZZ sound
    // set interval callback to m05
    private fun playPart2() {
        urCanvasView.playDiceRoll()
        playPart3()
    }

    private fun playPart3() {
        val currentTeam = ur.currentTeam()
        console.log("Current team is $currentTeam")

        // FIXME: switch to dice object
        this.roll = dice.roll()

        val moves: Map<Square, Square> = ur.askMoves(currentTeam, roll)
        val continueFunction = {
            if (0 == roll || moves.isEmpty()) {
                // skip processing of human roll, nothing to do
                playPart4()
            } else {
                // regular turn
                playPart4()
            }
        }

        // UI iteration 3!
        urCanvasView.drawAll(currentTeam, roll, moves, playArea, continueFunction)

        if (currentPlayerIsAi()) {
            playPart4()
        } else {
            urCanvasView.startMovesAnimation(this.playArea, currentTeam, moves.keys)
        }
    }

    private fun playPart4() {
        urCanvasView.endMovesAnimation()

        val currentTeam = ur.currentTeam()
        val moves: Map<Square, Square> = ur.askMoves(currentTeam, roll)

        val moveSupplier = moveSuppliers.get(currentTeam)!!

        val input = moveSupplier.choose(moves)

        val skipTurn = 0 == roll || moves.isEmpty()

        if (skipTurn) {
            ur.skipTurn()
            urCanvasView.drawMost(playArea)
            console.log("Skipping turn of $currentTeam. roll is $roll and moves size is ${moves.size}")

            playPart1()
            return
        } else {
            console.log("Not skipping turn as skipTurn is $skipTurn")
        }

        val moveIndex: Int = input.toInt()
        val fromSquare: Square = moves.keys.toList()[moveIndex - 1]

        val continueFunction = {
            val result = ur.moveCounter(fromSquare, roll)
            console.log("move result is $result")

            playPart5(result)
        }

        urCanvasView.animate(playArea, currentTeam, fromSquare, moves[fromSquare]!!, continueFunction)
    }

    private fun playPart5(result: Ur.MoveResult) {
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
                playPart6()
            }, timeout = Random.nextInt(0, 500))
            return
        }

        // else
        playPart6()
    }

    private fun playPart6() {
        val continueFunction = {
            playPart1()
        }

        val currentTeam = ur.currentTeam()
        val moves: Map<Square, Square> = ur.askMoves(currentTeam, roll)

        urCanvasView.drawAll(currentTeam, roll, moves, playArea, continueFunction)

        console.log("playEeee: Current team is " + ur.currentTeam())

        // loop the game loop
        playPart1()
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