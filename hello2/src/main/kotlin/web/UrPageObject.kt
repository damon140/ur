package web

import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.stream.createHTML
import org.w3c.dom.Document
import org.w3c.dom.HTMLDivElement

public class UrPageObject(document: Document) {

    object PageConstants {
        const val ROOT = "root";
        const val WHITE_COUNTERS_ID = "white-counters";
        const val BLACK_COUNTERS_ID = "black-counters";
        const val BOARD_ID = "board";
        const val INSTRUCTIONS_ID = "instructions";
    }

    private val document: Document
    private var blackCountersDiv: HTMLDivElement
    private var whiteCountersDiv: HTMLDivElement
    private var boardDiv: HTMLDivElement
    private var instructionsDiv: HTMLDivElement

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

        this.whiteCountersDiv = document.getElementById(PageConstants.WHITE_COUNTERS_ID)!! as HTMLDivElement
        this.blackCountersDiv = document.getElementById(PageConstants.BLACK_COUNTERS_ID)!! as HTMLDivElement
        this.boardDiv = document.getElementById(PageConstants.BOARD_ID)!! as HTMLDivElement
        this.instructionsDiv = document.getElementById(PageConstants.INSTRUCTIONS_ID)!! as HTMLDivElement
    }

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
}