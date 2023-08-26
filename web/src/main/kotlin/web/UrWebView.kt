package web

import com.damon140.ur.Dice
import com.damon140.ur.PlayArea
import com.damon140.ur.Square
import com.damon140.ur.Team
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement

class UrWebView (lastMove: LastMove) {

    private val pageObject: UrPageObject = UrPageObject(document)
    private val urWebSound: UrWebSound = UrWebSound(pageObject)
    private val urCanvasBoard: UrCanvasBoard

    private val rollSpace: HTMLDivElement
    private val rollBlack: HTMLDivElement
    private val rollWhite: HTMLDivElement


    init {
        this.urCanvasBoard = UrCanvasBoard(lastMove, pageObject, urWebSound)

        this.rollSpace = pageObject.findRollSpace();
        this.rollBlack = pageObject.findRollBlack()
        this.rollWhite = pageObject.findRollWhite()
    }

    fun getLevel(): Int {
        return this.pageObject.readLevel()
    }

    fun drawShowRollButton(playArea: PlayArea, continueFunction: () -> Unit) {
        urCanvasBoard.drawShowRollButton(playArea, continueFunction)
    }

    fun drawRobotThinking() {
        urCanvasBoard.drawRobotThinking()
    }

    fun drawAll(currentTeam: Team, dice: Dice, moves: Map<Square, Square>, playArea: PlayArea, continueFunction: () -> Unit) {
        urCanvasBoard.drawAll(currentTeam, dice, moves, playArea, continueFunction)
    }

    fun startMovesAnimation(playArea: PlayArea, currentTeam: Team, keys: Set<Square>) {
        urCanvasBoard.startMovesAnimation(playArea, currentTeam, keys)
    }

    fun endMovesAnimation() {
        urCanvasBoard.endMovesAnimation()
    }

    fun drawMost(playArea: PlayArea) {
        urCanvasBoard.drawMost(playArea)
    }

    fun animate(playArea: PlayArea, currentTeam: Team, fromSquare: Square, value: Square, continueFunction: () -> Unit) {
        urCanvasBoard.animate(playArea, currentTeam, fromSquare, value, continueFunction)
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
