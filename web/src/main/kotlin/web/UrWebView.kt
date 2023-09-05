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

import com.damon140.ur.Dice
import com.damon140.ur.PlayArea
import com.damon140.ur.Square
import com.damon140.ur.Team
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSpanElement

class UrWebView (lastMove: LastMove) {

    private val lastMove: LastMove

    private val pageObject: UrPageObject = UrPageObject(document)
    private val urWebSound: UrWebSound = UrWebSound(pageObject)
    private val urCanvasBoard: UrCanvasBoard

    private var moveCounterIntervalHandle = 0
    private var animateMovesIntervalHandle = 0

    private val rollSpace: HTMLDivElement
    private val rollPartsWhite: HTMLDivElement
    private val rollPartsBlack: HTMLDivElement
    private val rollBlack: HTMLDivElement
    private val rollWhite: HTMLDivElement
    private var slider: HTMLInputElement
    private var findAiLevel: HTMLSpanElement

    init {
        this.lastMove = lastMove
        this.urCanvasBoard = UrCanvasBoard(pageObject)

        // FIXME: Damon switch to setZ methods here to hide members
        this.rollSpace = pageObject.findRollSpace()
        this.rollPartsWhite = pageObject.findRollPartsWhite()
        this.rollPartsBlack = pageObject.findRollPartsBlack()
        this.rollBlack = pageObject.findRollBlack()
        this.rollWhite = pageObject.findRollWhite()
        this.slider = pageObject.findLevelSlider()
        this.findAiLevel = pageObject.findAiLevel()

        slider.addEventListener("change", {
            updateAiName()
        })

        updateAiName()
    }

    private fun updateAiName() {
        val levelInt = slider.value.toInt()
        console.log("Slider change to " + slider.value)

        // TODO: damon pull up to web game?
        val levelName = when (levelInt) {
            0 -> "pl0dder"
            1 -> "ro11er"
            2 -> "2aker"
            3 -> "hogg3r"
            4 -> "supr4me"
            else -> {
                "unknown, is bug"
            }
        }
        console.log("AI level name: " + levelName)

        findAiLevel.innerText = levelName
    }

    fun getLevel(): Int {
        return this.pageObject.readLevel()
    }

    fun drawShowRollButton(playArea: PlayArea, continueFunction: () -> Unit) {
        urCanvasBoard.draw(playArea)

        // instructions
        rollSpace.innerText = ""

        // FIXME: Damon move to urPageObject
        val button = document.createElement("button") as HTMLButtonElement
        button.innerHTML = "Click to roll"

        // blank out last roll
        rollBlack.innerText = ""
        rollWhite.innerText = ""

        button.addEventListener("click", {
            console.log("Clicked on button!")

            // run continue function here
            continueFunction()
        })
        rollSpace.append(button)

        rollPartsWhite.innerText = ""
        rollPartsBlack.innerText = ""
    }

    fun drawRobotThinking() {
        rollSpace.innerHTML = "<i>AI is thinking</i>"

        // blank out last roll
        rollBlack.innerText = ""
        rollWhite.innerText = ""
    }


    fun drawBoardAndSetContinueFunction(
        currentTeam: Team,
        dice: Dice,
        moves: Map<Square, Square>,
        playArea: PlayArea,
        continueFunction: () -> Unit
    ) {
        urCanvasBoard.draw(playArea)
        urCanvasBoard.click(squareClicked(moves, continueFunction))
        updateInstructions(currentTeam, dice, moves.isEmpty(), continueFunction)
    }

    private fun squareClicked(
        moves: Map<Square, Square>,
        continueFunction: () -> Unit
    ): (Square) -> Unit = {
        console.log("move for clicked square $it: ${moves.containsKey(it)}")
        console.log(moves.keys.joinToString(","))

        var found = false

        for ((index, entry) in moves.entries.withIndex()) {
            if (entry.key == it) {
                console.log("matched move of $it")
                lastMove.setLastChosen((index + 1).toString())
                // run continue function here
                continueFunction()
                found = true
            }
        }

        if (!found) {
            urWebSound.playBaBowSound()
        }
    }

    private fun updateInstructions(currentTeam: Team, dice: Dice, zeroMoves: Boolean, continueFunction: () -> Unit) {
        val roll = dice.getLastValue()
        val spanToUpdate: HTMLDivElement
        val spanToBlank: HTMLDivElement
        if (Team.White == currentTeam) {
            spanToUpdate = rollWhite
            spanToBlank = rollBlack
        } else {
            spanToUpdate = rollBlack
            spanToBlank = rollWhite
        }

        spanToUpdate.innerText = "" + roll
        spanToBlank.innerText = ""
        val findRollSpace = rollSpace
        findRollSpace.innerText = ""

        if (currentTeam == Team.White) {
            rollPartsWhite.innerText = dice.getLastString()
            rollPartsBlack.innerText = ""
        } else {
            rollPartsWhite.innerText = ""
            rollPartsBlack.innerText = dice.getLastString()
        }

        if (roll == 0) {
            val button = document.createElement("button") as HTMLButtonElement
            button.innerHTML = "You rolled zero, can't move, bummer!!"

            button.addEventListener("click", {
                console.log("Clicked on button!")
                // run continue function here
                continueFunction()
            })
            findRollSpace.append(button)
        } else if (zeroMoves) {
            val button = document.createElement("button") as HTMLButtonElement
            button.innerHTML = "All moves blocked, bummer!!"

            button.addEventListener("click", {
                console.log("Clicked on button!")
                // run continue function here
                continueFunction()
            })
            findRollSpace.append(button)
        }
    }


    fun startMovesAnimation(playArea: PlayArea, currentTeam: Team, keys: Set<Square>) {
        val handler = urCanvasBoard.startMovesAnimation(playArea, currentTeam, keys)
        this.animateMovesIntervalHandle = window.setInterval(handler, 777)
    }

    fun endMovesAnimation() {
        window.clearInterval(this.animateMovesIntervalHandle)
    }

    fun drawBoard(playArea: PlayArea) {
        urCanvasBoard.draw(playArea)
    }

    fun animate(playArea: PlayArea, team: Team, fromSquare: Square, toSquare: Square, continueFunction: () -> Unit) {
        val squares = Square.calculateSquaresBetween(team, fromSquare, toSquare)
        console.log("Will animate counter path $squares")

        var oneSqaureAnim = 0

        var lastSquare = squares.removeFirst()
        var currentSquare = squares.removeFirst()

        // Hmm, this sort of works
        val handler: () -> Unit = {
            val countCompleted = oneSqaureAnim++ == 5
            var draw = true
            if (countCompleted) {
                if (squares.isEmpty()) {
                    window.clearInterval(this.moveCounterIntervalHandle)
                    draw = false
                    drawBoard(playArea)
                    continueFunction()
                } else {
                    oneSqaureAnim = 0
                    lastSquare = currentSquare
                    currentSquare = squares.removeFirst()
                }
                // TODO: push up and out of this class
                urWebSound.playTicSound()
            }

            if (draw) {
                urCanvasBoard.drawCounterAnimationFrame(team, lastSquare, currentSquare, oneSqaureAnim)
            }
        }

        this.moveCounterIntervalHandle = window.setInterval(handler, 85)
    }


    fun updateBoard(playArea: PlayArea) {
        urCanvasBoard.updateBoard(playArea)

    }

    fun gameWon(currentTeam: Team) {
        // black rolls
        rollWhite.innerHTML = ""
        rollBlack.innerHTML = ""

        // FIXME: Damon move to page object
        val button = document.createElement("button") as HTMLButtonElement
        button.innerHTML = "Game won by $currentTeam. Click to restart"

        button.addEventListener("click", {
            // FIXME: want a function handle to call here on game won
            console.log("reloading")
            window.location.reload()
        })

        rollSpace.innerText = ""
        rollSpace.append(button)

        if (Team.White == currentTeam) {
            urWebSound.playClapsSound()
        } else {
            urWebSound.playAiWinsSound()
        }
    }

    fun playHmm() {
        urWebSound.playHmm()
    }

    fun playDiceRoll() {
        urWebSound.playDiceRoll()
    }

    fun playCantMoveSound() {
        urWebSound.playCantMoveSound()
    }

    fun playCounterTakenSound() {
        urWebSound.playCounterTakenSound()
    }

    fun playCounterMovedHomeSound() {
        urWebSound.playCounterMovedHomeSound()
    }

}
