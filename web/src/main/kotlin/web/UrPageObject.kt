package web

import org.w3c.dom.*

class UrPageObject(document: Document) {

    object PageConstants {
        // ITERATION 3
        const val CANVAS_BOARD_ID = "canvas-board"
        const val ROLL_WHITE_ID = "roll-white"
        const val ROLL_BLACK_ID = "roll-black"
        const val ROLL_SPACE_ID = "roll-space2"

        // ITERATION 4
        const val HMM_ID = "hmm-audio"
        const val COUNTER_TAKEN_SOUND_ID = "counter-taken-audio"
        const val BABOW_SOUND_ID = "babow-sound"
        const val DICE_SOUND_ID = "dice-sound"
        const val TIC_SOUND_ID = "tic-sound"
    }

    // iteration 1
    private val document: Document

    // iteration 3
    private var canvasBoard: HTMLCanvasElement
    private var rollWhite: HTMLDivElement
    private var rollBlack: HTMLDivElement
    private var rollSpace: HTMLDivElement

    // iteration 4
    private var hmm:  HTMLAudioElement
    private var counterTakenSound:  HTMLAudioElement
    private var babowSound: HTMLAudioElement
    private var diceSound: HTMLAudioElement
    private var ticSound: HTMLAudioElement

    init {
        this.document = document

        // iteration 3
        this.canvasBoard = document.getElementById(PageConstants.CANVAS_BOARD_ID)!! as HTMLCanvasElement
        this.rollWhite = document.getElementById(PageConstants.ROLL_WHITE_ID)!! as HTMLDivElement
        this.rollBlack = document.getElementById(PageConstants.ROLL_BLACK_ID)!! as HTMLDivElement
        this.rollSpace = document.getElementById(PageConstants.ROLL_SPACE_ID)!! as HTMLDivElement

        // iteration 4
        this.hmm = document.getElementById(PageConstants.HMM_ID)!! as HTMLAudioElement
        this.counterTakenSound = document.getElementById(PageConstants.COUNTER_TAKEN_SOUND_ID)!! as HTMLAudioElement
        this.babowSound = document.getElementById(PageConstants.BABOW_SOUND_ID)!! as HTMLAudioElement
        this.diceSound = document.getElementById(PageConstants.DICE_SOUND_ID)!! as HTMLAudioElement
        this.ticSound = document.getElementById(PageConstants.TIC_SOUND_ID)!! as HTMLAudioElement
    }

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

    // FIXME: switch to play
    fun findHmmSound(): HTMLAudioElement {
        return this.hmm
    }

    // FIXME: switch to play
    fun findCounterTakenSound(): HTMLAudioElement {
        return this.counterTakenSound
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

}
