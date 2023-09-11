/*
 * Copyright 2023 Damon van Opdorp
 *
 * Licensed under GNU General Public License v3.0.
 *
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package web

import com.damon140.ur.*
import kotlinx.browser.window
import kotlin.random.Random

class WebGame {
    private val dice = Dice()
    // Random team makes it much harder to win
    private var playArea = PlayArea(Team.random())
    private var lastMove = LastMove()
    private var ur: Ur = Ur(playArea)
    private var aiNameMap: Map<Int, String> = mapOf(
        0 to "pl0dder",
        1 to "ro11er",
        2 to "2aker",
        3 to "hogg3r",
        4 to "supr4me",
    )

    private var urWebView = UrWebView(lastMove, aiNameMap)

    private var roll: Int = 0

    // TODO: impl player setup
    private var playerSetup: PlayerSetup = PlayerSetup(playArea, lastMove)
    private var moveSuppliers: HashMap<Team, PlayerSetup.InputSupplier> = hashMapOf(
        Team.White to playerSetup.getPlayer(Team.White),
        Team.Black to playerSetup.getPlayer(Team.Black)
    )

    // set up a game for testing
    fun fakeGame(game: String) {
        playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal(game)
        ur = Ur(playArea)
        playerSetup = PlayerSetup(playArea, lastMove)
    }

    // draw (show roll button)
    // wait for roll button to be clicked *
    fun playPart1() {
        val continueFunction = {
            console.log("About to run continue function from roll")
            playPart2()
        }

        if (currentPlayerIsHuman()) {
            urWebView.drawShowRollButton(playArea, continueFunction)
        } else {
            urWebView.drawRobotThinking()

            // FIXME: Damon push into webView
            if (1 == Random.nextInt(1, 9)) {
                urWebView.playHmm()
            }

            window.setTimeout(handler = {
                console.log("Robot finished thinking here")
                playPart2()
            }, timeout =  Random.nextInt(0, 900))
        }
    }

    private fun playPart2() {
        urWebView.playDiceRoll()
        playPart3()
    }

    private fun playPart3() {
        val currentTeam = ur.currentTeam()
        console.log("Current team is $currentTeam")

        dice.roll()
        this.roll = dice.getLastValue()

        val moves: Map<Square, Square> = ur.askMoves(currentTeam, roll)

        val cantMove = 0 == roll || moves.isEmpty()

        if (cantMove) {
            urWebView.playCantMoveSound()
        }

        val continueFunction = {
            if (cantMove) {
                // skip processing of human roll, nothing to do
                playPart4()
            } else {
                // regular turn
                playPart4()
            }
        }

        // UI iteration 3!
        urWebView.drawBoardAndSetContinueFunction(currentTeam, dice, moves, playArea, continueFunction)

        if (currentPlayerIsAi()) {
            playPart4()
        } else {
            urWebView.startMovesAnimation(this.playArea, currentTeam, moves.keys)
        }
    }

    private fun playPart4() {
        urWebView.endMovesAnimation()

        val currentTeam = ur.currentTeam()
        val moves: Map<Square, Square> = ur.askMoves(currentTeam, roll)

        val moveSupplier = moveSuppliers.getValue(currentTeam)

        val skipTurn = 0 == roll || moves.isEmpty()

        if (skipTurn) {
            ur.skipTurn()
            urWebView.drawBoard(playArea)
            console.log("Skipping turn of $currentTeam. roll is $roll and moves size is ${moves.size}")

            playPart1()
            return
        } else {
            console.log("Not skipping turn as skipTurn is $skipTurn")
        }

        val level = urWebView.getLevel()
        val moveIndex = moveSupplier.choose(level, moves)
        val fromSquare: Square = moves.keys.toList()[moveIndex - 1]

        val continueFunction = {
            val result = ur.moveCounter(fromSquare, roll)
            console.log("move result is $result")
            playPart5(result)
        }

        urWebView.animate(playArea, currentTeam, fromSquare, moves.getValue(fromSquare), continueFunction)
    }

    private fun playPart5(result: Ur.MoveResult) {
        val currentTeam = ur.currentTeam()

        if (result == Ur.MoveResult.GameOver) {
            urWebView.updateBoard(playArea)
            urWebView.gameWon(currentTeam)
            return
        }

        var showThinking = false
        if (result == Ur.MoveResult.CounterTaken) {
            urWebView.playCounterTakenSound()
            showThinking = true
        }

        if (result == Ur.MoveResult.CounterOffboard) {
            urWebView.playCounterMovedHomeSound()
            showThinking = true
        }

        if (showThinking) {
            window.setTimeout(handler = {
                console.log("Robot finished thinking here")
                playPart6()
            }, timeout = Random.nextInt(0, 500))
            return
        } else {
            playPart6()
        }
    }

    private fun playPart6() {
        val continueFunction = {
            playPart1()
        }

        val currentTeam = ur.currentTeam()
        val moves: Map<Square, Square> = ur.askMoves(currentTeam, roll)

        console.log("About to drawAll().")
        urWebView.drawBoardAndSetContinueFunction(currentTeam, this.dice, moves, playArea, continueFunction)

        // loop the game loop
        playPart1()
    }

    private fun currentPlayerIsHuman(): Boolean {
        val currentTeam = ur.currentTeam()
        val inputSupplier = moveSuppliers.getValue(currentTeam)
        return inputSupplier.isHuman()
    }

    private fun currentPlayerIsAi(): Boolean {
        return !currentPlayerIsHuman()
    }

}