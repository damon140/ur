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

import org.w3c.dom.*
import kotlin.random.Random
class UrPageObject(document: Document) {

    // iteration 1
    private val document: Document

    // iteration 3
    private var canvasBoard: HTMLCanvasElement
    private var rollWhite: HTMLDivElement
    private var rollBlack: HTMLDivElement
    private var rollSpace: HTMLDivElement

    // iteration 4
    private var hmmSound:  HTMLAudioElement
    private var counterTakenSound:  HTMLAudioElement
    private var babowSound: HTMLAudioElement
    private var diceSound: HTMLAudioElement
    private var ticSound: HTMLAudioElement

    // iteration 5
    private var rollPartsWhiteDiv: HTMLDivElement
    private var rollPartsBlackDiv: HTMLDivElement
    private var levelSlider: HTMLInputElement

    // iteration 6
    private var aiWinsSound: HTMLAudioElement
    private var clapsSound: HTMLAudioElement

    // iteration 7
    private var cantMoveSound1: HTMLAudioElement
    private var cantMoveSound2: HTMLAudioElement

    // iteration 8
    private var counterMovedHomeSound: HTMLAudioElement

    init {
        this.document = document

        // iteration 3
        this.canvasBoard = htmlCanvasElement(document, "canvas-board")
        this.rollWhite = htmlDivElement(document, "roll-white")
        this.rollBlack = htmlDivElement(document, "roll-black")
        this.rollSpace = htmlDivElement(document, "roll-space2")

        // iteration 4
        this.hmmSound = htmlAudioElement(document, "hmm-audio")
        this.counterTakenSound = htmlAudioElement(document, "counter-taken-audio")
        this.babowSound = htmlAudioElement(document, "babow-sound")
        this.diceSound = htmlAudioElement(document, "dice-sound")
        this.ticSound = htmlAudioElement(document, "tic-sound")

        // iteration 5
        this.rollPartsWhiteDiv = htmlDivElement(document, "roll-parts-white")
        this.rollPartsBlackDiv = htmlDivElement(document, "roll-parts-black")
        this.levelSlider = document.getElementById("ai-slider")!! as HTMLInputElement

        // iteration 6
        this.aiWinsSound = htmlAudioElement(document, "ai-wins-sound")
        this.clapsSound = htmlAudioElement(document, "claps-sound")

        // iteration 7
        this.cantMoveSound1 = htmlAudioElement(document, "wah-wah1-sound")
        this.cantMoveSound2 = htmlAudioElement(document, "wah-wah2-sound")

        // iteration 8
        // FIXME: Damon add "ting" success sound for counter getting offboard to home
        this.counterMovedHomeSound = htmlAudioElement(document, "ting-sound")
    }

    private fun htmlAudioElement(document: Document, elementId: String) =
        document.getElementById(elementId)!! as HTMLAudioElement

    private fun htmlDivElement(document: Document, elementId: String) =
        document.getElementById(elementId)!! as HTMLDivElement

    private fun htmlCanvasElement(document: Document, elementId: String) =
        document.getElementById(elementId)!! as HTMLCanvasElement

    // Iteration 3
    fun findCanvasBoard(): HTMLCanvasElement {
        return this.canvasBoard
    }

    fun findRollWhite(): HTMLDivElement {
        return this.rollWhite
    }

    fun findRollSpace(): HTMLDivElement {
        return this.rollSpace
    }

    fun findRollBlack(): HTMLDivElement {
        return this.rollBlack
    }

    fun findLevelSlider(): HTMLInputElement {
        return this.levelSlider;
    }

    fun playHmmSound() {
        hmmSound.play()
    }

    fun playCounterTakenSound() {
        counterTakenSound.play()
    }

    fun playBaBowSound() {
        babowSound.currentTime = 0.15
        babowSound.play()
    }

    fun playDiceSound() {
        diceSound.volume = 0.7
        diceSound.currentTime = 0.65
        diceSound.play()
    }

    fun playTicSound() {
        ticSound.volume = 0.4
        ticSound.currentTime = 0.65
        ticSound.play()
    }

    // Iteration 5
    fun findRollPartsBlack(): HTMLDivElement {
        return this.rollPartsBlackDiv
    }

    fun findRollPartsWhite(): HTMLDivElement {
        return this.rollPartsWhiteDiv
    }

    fun readLevel(): Int {
      return this.levelSlider.value.toInt()
    }

    // iteration 6
    fun playAiWinsSound() {
        this.aiWinsSound.play()
    }

    fun playClapsSound() {
        this.clapsSound.play()
    }

    fun playCantMoveSound() {
        if (4 != Random.nextInt(1, 4)) {
            // prefer this sound most of the time
            cantMoveSound1.volume = 0.8
            cantMoveSound1.currentTime = 0.4
            cantMoveSound1.play()
        } else {
            // this is Jazz's funny sound!!
            cantMoveSound2.volume = 0.4
            cantMoveSound2.currentTime = 0.65
            cantMoveSound2.play()
        }
    }

    fun playCounterMovedHomeSound() {
        counterMovedHomeSound.play()
    }

}

