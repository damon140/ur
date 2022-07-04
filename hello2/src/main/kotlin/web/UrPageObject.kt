package web

import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.stream.createHTML
import org.w3c.dom.*

class UrPageObject(document: Document) {

    object PageConstants {
        // ITERATION 1
        const val ROOT = "root";
        const val INSTRUCTIONS_ID = "instructions";

        // ITERATION 2
        const val WHITE_COUNTERS2_ID = "white-counters2"
        const val WHITE_UNSTARTED_ID = "white-unstarted"
        const val WHITE_FINISHED_ID = "white-finished"
        const val BOARD_SPANS_ID = "board-spans"
        const val BLACK_COUNTERSS2_ID = "black-counters2"
        const val BLACK_UNSTARTED_ID = "black-unstarted"
        const val BLACK_FINISHED_ID = "black-finished"

        // ITERATION 3
        const val CANVAS_BOARD_ID = "canvas-board"
        const val ROLL_WHITE_ID = "roll-white"
        const val ROLL_BLACK_ID = "roll-black"
        const val ROLL_SPACE_ID = "roll-space2"

        // ITERATION 4
        const val DICE_ID = "dice-audio";
        const val HMM_ID = "hmm-audio";
    }

    // iteration 1
    private val document: Document
    private var instructionsDiv: HTMLDivElement

    // iteration 2
    private val whiteCounters2: HTMLDivElement
    private val whiteUnstarted: HTMLDivElement
    private val whiteFinished: HTMLDivElement
    private val boardSpans: HTMLDivElement
    private val blackCounterss2: HTMLDivElement
    private val blackUnstarted: HTMLDivElement
    private val blackFinished: HTMLDivElement

    // iteration 3
    private var canvasBoard: HTMLCanvasElement
    private var rollWhite: HTMLSpanElement
    private var rollBlack: HTMLSpanElement
    private var rollSpace: HTMLSpanElement

    // iteration 4
    private var dice: HTMLAudioElement
    private var hmm:  HTMLAudioElement

    init {
        this.document = document

        val root = document.getElementById(PageConstants.ROOT)!! as HTMLDivElement
        root.innerHTML = createHTML().div {
            div {
                id = PageConstants.INSTRUCTIONS_ID
            }
        }

        // iteration 1
        this.instructionsDiv = document.getElementById(PageConstants.INSTRUCTIONS_ID)!! as HTMLDivElement

        // iteration 2
        this.whiteCounters2 = document.getElementById(PageConstants.WHITE_COUNTERS2_ID)!! as HTMLDivElement
        this.whiteUnstarted = document.getElementById(PageConstants.WHITE_UNSTARTED_ID)!! as HTMLDivElement
        this.whiteFinished = document.getElementById(PageConstants.WHITE_FINISHED_ID)!! as HTMLDivElement
        this.boardSpans = document.getElementById(PageConstants.BOARD_SPANS_ID)!! as HTMLDivElement
        this.blackCounterss2 = document.getElementById(PageConstants.BLACK_COUNTERSS2_ID)!! as HTMLDivElement
        this.blackUnstarted = document.getElementById(PageConstants.BLACK_UNSTARTED_ID)!! as HTMLDivElement
        this.blackFinished = document.getElementById(PageConstants.BLACK_FINISHED_ID)!! as HTMLDivElement

        // iteration 3
        this.canvasBoard = document.getElementById(PageConstants.CANVAS_BOARD_ID)!! as HTMLCanvasElement
        this.rollWhite = document.getElementById(PageConstants.ROLL_WHITE_ID)!! as HTMLSpanElement
        this.rollBlack = document.getElementById(PageConstants.ROLL_BLACK_ID)!! as HTMLSpanElement
        this.rollSpace = document.getElementById(PageConstants.ROLL_SPACE_ID)!! as HTMLSpanElement

        // iteration 4
        this.dice = document.getElementById(PageConstants.DICE_ID)!! as HTMLAudioElement
        this.hmm = document.getElementById(PageConstants.HMM_ID)!! as HTMLAudioElement

    }

    //  -----------------
    // iteration 1

    fun findInstructionsDiv(): HTMLDivElement {
        return instructionsDiv
    }

    // ----------------------------
    // iteration 2

    fun findWhiteUnstarted(): HTMLDivElement {
        return this.whiteUnstarted
    }

    fun findWhiteFinished(): HTMLDivElement {
        return this.whiteFinished
    }

    fun findBlackUnstarted(): HTMLDivElement {
        return this.blackUnstarted
    }

    fun findBlackFinished(): HTMLDivElement {
        return this.blackFinished
    }

    fun findBoardSpan(index: Int): HTMLDivElement {
        return document.getElementById("board-spans" + index)!! as HTMLDivElement
    }

    // Iteration 3

    fun findCanvasBoard(): HTMLCanvasElement {
        return this.canvasBoard
    }

    fun findRollWhite(): HTMLSpanElement {
        return this.rollWhite
    }

    fun findRollSpace(): HTMLSpanElement {
        return this.rollSpace
    }

    fun findRollBlack(): HTMLSpanElement {
        return this.rollBlack
    }

    fun findDice(): HTMLAudioElement {
        return this.dice
    }

    fun findHmm():  HTMLAudioElement {
        return this.hmm
    }

}
