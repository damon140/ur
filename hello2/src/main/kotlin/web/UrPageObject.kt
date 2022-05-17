package web

import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.stream.createHTML
import org.w3c.dom.Document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLUListElement

public class UrPageObject(document: Document) {

    object PageConstants {
        // ITERATION 1
        const val ROOT = "root";
        const val WHITE_COUNTERS_ID = "white-counters";
        const val BLACK_COUNTERS_ID = "black-counters";
        const val BOARD_ID = "board";
        const val INSTRUCTIONS_ID = "instructions";

        // ITERATION 2
        const val WHITE_COUNTERS2_ID = "white-counters2";
        const val WHITE_UNSTARTED_ID = "white-unstarted";
        const val WHITE_FINISHED_ID = "white-finished";
        const val BOARD_SPANS_ID = "board-spans";
        const val BLACK_COUNTERSS2_ID = "black-counters2";
        const val BLACK_UNSTARTED_ID = "black-unstarted";
        const val BLACK_FINISHED_ID = "black-finished";
    }

    private val document: Document
    private var blackCountersDiv: HTMLDivElement
    private var whiteCountersDiv: HTMLDivElement
    private var boardDiv: HTMLDivElement
    private var instructionsDiv: HTMLDivElement

    private val whiteCounters2: HTMLDivElement
    private val whiteUnstarted: HTMLUListElement
    private val whiteFinished: HTMLUListElement
    private val boardSpans: HTMLDivElement
    private val blackCounterss2: HTMLDivElement
    private val blackUnstarted: HTMLUListElement
    private val blackFinished: HTMLUListElement

    init {
        this.document = document

        val root = document.getElementById(PageConstants.ROOT)!! as HTMLDivElement
        root.innerHTML = createHTML().div {
            div {
                id = PageConstants.WHITE_COUNTERS_ID
            }
            div {
                id = PageConstants.BOARD_ID
            }
            div {
                id = PageConstants.BLACK_COUNTERS_ID
            }
            div {
                id = PageConstants.INSTRUCTIONS_ID
            }
        }

        // iteration 1
        this.whiteCountersDiv = document.getElementById(PageConstants.WHITE_COUNTERS_ID)!! as HTMLDivElement
        this.blackCountersDiv = document.getElementById(PageConstants.BLACK_COUNTERS_ID)!! as HTMLDivElement
        this.boardDiv = document.getElementById(PageConstants.BOARD_ID)!! as HTMLDivElement
        this.instructionsDiv = document.getElementById(PageConstants.INSTRUCTIONS_ID)!! as HTMLDivElement

        // iteration 2
        this.whiteCounters2 = document.getElementById(PageConstants.WHITE_COUNTERS2_ID)!! as HTMLDivElement
        this.whiteUnstarted = document.getElementById(PageConstants.WHITE_UNSTARTED_ID)!! as HTMLUListElement
        this.whiteFinished = document.getElementById(PageConstants.WHITE_FINISHED_ID)!! as HTMLUListElement
        this.boardSpans = document.getElementById(PageConstants.BOARD_SPANS_ID)!! as HTMLDivElement
        this.blackCounterss2 = document.getElementById(PageConstants.BLACK_COUNTERSS2_ID)!! as HTMLDivElement
        this.blackUnstarted = document.getElementById(PageConstants.BLACK_UNSTARTED_ID)!! as HTMLUListElement
        this.blackFinished = document.getElementById(PageConstants.BLACK_FINISHED_ID)!! as HTMLUListElement
    }

    //  -----------------
    // iteration 1

    fun findBlackCountersDiv(): HTMLDivElement {
        return blackCountersDiv
    }

    fun findWhiteCountersDiv(): HTMLDivElement {
        return whiteCountersDiv
    }

    fun findBoardDiv(): HTMLDivElement {
        return boardDiv
    }

    fun findInstructionsDiv(): HTMLDivElement {
        return instructionsDiv
    }

    // ----------------------------
    // iteration 2

    fun findWhiteUnstarted(): HTMLUListElement {
        return this.whiteUnstarted
    }

    fun findWhiteFinished(): HTMLUListElement {
        return this.whiteFinished
    }

    fun findBlackUnstarted(): HTMLUListElement {
        return this.blackUnstarted
    }

    fun findBlackFinished(): HTMLUListElement {
        return this.blackFinished
    }


}