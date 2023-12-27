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

    private val document: Document
    private var canvasBoard: HTMLCanvasElement
    private var rollWhite: HTMLDivElement
    private var rollBlack: HTMLDivElement
    private var rollSpace: HTMLDivElement
    private var aiDescription: HTMLSpanElement
    private var rollPartsWhiteDiv: HTMLDivElement
    private var rollPartsBlackDiv: HTMLDivElement
    private var levelSlider: HTMLInputElement

    private var hmmSound: HTMLAudioElement
    private var counterTakenSound:  HTMLAudioElement
    private var babowSound: HTMLAudioElement
    private var diceSound: HTMLAudioElement
    private var ticSound: HTMLAudioElement
    private var aiWinsSound: HTMLAudioElement
    private var clapsSound: HTMLAudioElement
    private var cantMoveSound1: HTMLAudioElement
    private var cantMoveSound2: HTMLAudioElement
    private var counterMovedHomeSound: HTMLAudioElement

    init {
        this.document = document
        
        this.canvasBoard = htmlCanvasElement("canvas-board")
        this.rollWhite = htmlDivElement("roll-white")
        this.rollBlack = htmlDivElement("roll-black")
        this.rollSpace = htmlDivElement("roll-space2")
        this.hmmSound = htmlAudioElement("hmm-audio")
        this.counterTakenSound = htmlAudioElement("counter-taken-audio")
        this.babowSound = htmlAudioElement("babow-sound")
        this.diceSound = htmlAudioElement("dice-sound")
        this.ticSound = htmlAudioElement("tic-sound")
        this.rollPartsWhiteDiv = htmlDivElement("roll-parts-white")
        this.rollPartsBlackDiv = htmlDivElement("roll-parts-black")
        this.levelSlider = htmlInputElement("ai-slider")
        this.aiWinsSound = htmlAudioElement("ai-wins-sound")
        this.clapsSound = htmlAudioElement("claps-sound")
        this.cantMoveSound1 = htmlAudioElement("wah-wah1-sound")
        this.cantMoveSound2 = htmlAudioElement("wah-wah2-sound")
        this.counterMovedHomeSound = htmlAudioElement("ting-sound")
        this.aiDescription = htmlSpanElement("ai-description")

        // Set initial sound volumes
        this.diceSound.volume = 0.7
        this.ticSound.volume = 0.4
        this.cantMoveSound1.volume = 0.2
        this.cantMoveSound2.volume = 0.2
    }

    // FIXME: Damon, move to UrWebView class, is logic not page access
    fun bindAudioVolumeFromBase(baseId: String) {
        val audio = htmlAudioElement(baseId)
        val display =  htmlSpanElement(baseId + "-display")
        val button = htmlButtonElement(baseId + "-button")
        val volumeSlider = htmlInputElement(baseId + "-volume")

        button.addEventListener("click", {
            audio.play()
        })

        volumeSlider.addEventListener("input", {
            val percentage = volumeSlider.value.toInt()
            console.log("percentage is: " + percentage)
            display.innerText = percentage.toString()

            val newLevel = percentage/100f;
            console.log("new level is: " + newLevel.toString())
            audio.volume = newLevel.toDouble()
        })

        // set slider and display to audio control initial volume value
        val volumeString: String = "" + (audio.volume * 100)
        display.innerText = volumeString
        volumeSlider.value = volumeString
    }

    private fun htmlAudioElement(elementId: String) =
        this.document.getElementById(elementId)!! as HTMLAudioElement

    private fun htmlDivElement(elementId: String) =
        this.document.getElementById(elementId)!! as HTMLDivElement

    private fun htmlCanvasElement(elementId: String) =
        this.document.getElementById(elementId)!! as HTMLCanvasElement

    private fun htmlSpanElement(elementId: String) =
        this.document.getElementById(elementId)!! as HTMLSpanElement

    private fun htmlButtonElement(elementId: String) =
        this.document.getElementById(elementId)!! as HTMLButtonElement

    private fun htmlInputElement(elementId: String) =
        this.document.getElementById(elementId)!! as HTMLInputElement


    ///////////////////////////////////
    // Find elements
    ///////////////////////////////////


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
        return this.levelSlider
    }

    fun findAiLevel(): HTMLSpanElement {
        return this.aiDescription
    }

    fun findRollPartsBlack(): HTMLDivElement {
        return this.rollPartsBlackDiv
    }

    fun findRollPartsWhite(): HTMLDivElement {
        return this.rollPartsWhiteDiv
    }

    fun readLevel(): Int {
        return this.levelSlider.value.toInt()
    }

    ///////////////////////////////////
    // Play Sounds
    ///////////////////////////////////

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
        diceSound.currentTime = 0.65
        diceSound.play()
    }

    fun playTicSound() {
        ticSound.currentTime = 0.65
        ticSound.play()
    }

    fun playAiWinsSound() {
        this.aiWinsSound.play()
    }

    fun playClapsSound() {
        this.clapsSound.play()
    }

    fun playCantMoveSound() {
        if (4 != Random.nextInt(1, 4)) {
            // prefer this sound most of the time
            cantMoveSound1.currentTime = 0.4
            cantMoveSound1.play()
        } else {
            // this is Jazz's funny sound!!
            cantMoveSound2.currentTime = 0.65
            cantMoveSound2.play()
        }
    }

    fun playCounterMovedHomeSound() {
        counterMovedHomeSound.play()
    }

}
