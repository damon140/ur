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
        const val ROLL1_ID = "roll1-sound"
        const val ROLL2_ID = "roll2-sound"
        const val ROLL3_ID = "roll3-sound"
        const val ROLL4_ID = "roll4-sound"

        const val HMM_ID = "hmm-audio"
        const val COUNTER_TAKEN_SOUND_ID = "counter-taken-audio"
        const val BABOW_SOUND_ID = "babow-sound"
    }

    // iteration 1
    private val document: Document

    // iteration 3
    private var canvasBoard: HTMLCanvasElement
    private var rollWhite: HTMLDivElement
    private var rollBlack: HTMLDivElement
    private var rollSpace: HTMLDivElement

    // iteration 4
    private var roll1: HTMLAudioElement
    private var roll2: HTMLAudioElement
    private var roll3: HTMLAudioElement
    private var roll4: HTMLAudioElement
    private var hmm:  HTMLAudioElement
    private var counterTakenSound:  HTMLAudioElement
    private var babowSound: HTMLAudioElement

    init {
        this.document = document

        // iteration 3
        this.canvasBoard = document.getElementById(PageConstants.CANVAS_BOARD_ID)!! as HTMLCanvasElement
        this.rollWhite = document.getElementById(PageConstants.ROLL_WHITE_ID)!! as HTMLDivElement
        this.rollBlack = document.getElementById(PageConstants.ROLL_BLACK_ID)!! as HTMLDivElement
        this.rollSpace = document.getElementById(PageConstants.ROLL_SPACE_ID)!! as HTMLDivElement

        // iteration 4
        this.roll1 = document.getElementById(PageConstants.ROLL1_ID)!! as HTMLAudioElement
        this.roll2 = document.getElementById(PageConstants.ROLL2_ID)!! as HTMLAudioElement
        this.roll3 = document.getElementById(PageConstants.ROLL3_ID)!! as HTMLAudioElement
        this.roll4 = document.getElementById(PageConstants.ROLL4_ID)!! as HTMLAudioElement
        this.hmm = document.getElementById(PageConstants.HMM_ID)!! as HTMLAudioElement
        this.counterTakenSound = document.getElementById(PageConstants.COUNTER_TAKEN_SOUND_ID)!! as HTMLAudioElement
        this.babowSound = document.getElementById(PageConstants.BABOW_SOUND_ID)!! as HTMLAudioElement
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

    fun findRoll1(): HTMLAudioElement {
        return this.roll1
    }

    fun findRoll2(): HTMLAudioElement {
        return this.roll2
    }

    fun findRoll3(): HTMLAudioElement {
        return this.roll3
    }

    fun findRoll4(): HTMLAudioElement {
        return this.roll4
    }

    fun findHmm(): HTMLAudioElement {
        return this.hmm
    }

    fun findCounterTakenSound(): HTMLAudioElement {
        return this.counterTakenSound
    }

    fun findBaBowSound(): HTMLAudioElement {
        return this.babowSound
    }


}
