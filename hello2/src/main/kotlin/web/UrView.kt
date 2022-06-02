package web

import com.damon140.ur.HorizontalDrawnBoard
import com.damon140.ur.HorizontalDrawnBoard.*
import com.damon140.ur.Square
import com.damon140.ur.Square.*
import com.damon140.ur.Team
import kotlinx.browser.document
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import org.w3c.dom.*
import org.w3c.dom.events.Event

class UrView(pageObject: UrPageObject) {
    private val pageObject: UrPageObject
    private var lastChosen: String = ""

    init {
        this.pageObject = pageObject
    }

    // TODO: move to new class
    fun drawSquare() {
        var c = pageObject.findCanvasBoard()
        var ctx: CanvasRenderingContext2D = c.getContext("2d") as CanvasRenderingContext2D

        // FIXME: board part to xy pair function via map

        val squares: List<Pair<Int, Int>> = listOf(
            black_run_on_1,
            black_run_on_2,
            black_run_on_3,
            black_run_on_4,
            black_run_off_1,
            black_run_off_2,
            shared_1,
            shared_2,
            shared_3,
            shared_4,
            shared_5,
            shared_6,
            shared_7,
            shared_8,
            white_run_on_1,
            white_run_on_2,
            white_run_on_3,
            white_run_on_4,
            white_run_off_1,
            white_run_off_2
        ).map { s -> squareToCoordinates(s) }

        drawSquares(ctx, squares)
    }

    private fun squareToCoordinates(square: Square): Pair<Int, Int> {
        return when(square) {
            white_run_on_1 -> Pair(2, 3)
            white_run_on_2 -> Pair(2, 2)
            white_run_on_3 -> Pair(2, 1)
            white_run_on_4 -> Pair(2, 0)
            white_run_off_1 ->Pair(2, 7)
            white_run_off_2 -> Pair(2, 8)

            black_run_on_1 -> Pair(4, 3)
            black_run_on_2 -> Pair(4, 2)
            black_run_on_3 -> Pair(4, 1)
            black_run_on_4 -> Pair(4, 0)
            black_run_off_1 -> Pair(4, 7)
            black_run_off_2 -> Pair(4, 8)

            shared_1 -> Pair(3, 0)
            shared_2 -> Pair(3, 1)
            shared_3 -> Pair(3, 2)
            shared_4 -> Pair(3, 3)
            shared_5 -> Pair(3, 4)
            shared_6 -> Pair(3, 5)
            shared_7 -> Pair(3, 6)
            shared_8 -> Pair(3, 7)

            off_board_unstarted -> Pair(0, 0)
            off_board_finished -> Pair(0, 0)
        }
    }

    private fun drawSquares(ctx: CanvasRenderingContext2D, squares: List<Pair<Int, Int>>) {
        squares.forEach {
            e ->
            ctx.beginPath();
            ctx.rect(50.0 * e.first, 50.0 * e.second, 50.0, 50.0);
            ctx.stroke();
        }
    }

    fun updateWhiteCounters(unstarted: Int, completed: Int) {
        pageObject.findWhiteUnstarted().innerHTML = offboardCounters(unstarted, "w") + "<pre>--</pre>"
        pageObject.findWhiteFinished().innerHTML = offboardCounters(completed, "w")
    }

    private fun offboardCounters(count: Int, charsy: String): String {
        val twosCount = count / 2
        val onesCount = count % 2

        return "<pre>$charsy$charsy</pre>".repeat(twosCount) + "<pre>$charsy</pre>".repeat(onesCount)
    }

    fun updateBlackCounters(unstarted: Int, completed: Int) {
        pageObject.findBlackUnstarted().innerHTML = offboardCounters(unstarted, "b") + "<pre>--</pre>"
        pageObject.findBlackFinished().innerHTML = offboardCounters(completed, "b")
    }

    fun updateBoard(vertBoard: List<String>) {
        // iteration 2
        console.log("Vert board:")
        console.log(vertBoard.joinToString("\n"))

        vertBoard.forEachIndexed { index, element ->
            // use mid-dot for empty square
            pageObject.findBoardSpan(1 + index).innerHTML =
                "<pre>" + makeHtml(element) + "</pre>"
        }
    }

    private fun makeHtml(element: String): String {
        element.replace(".", "&#183;")
        return element.map { e -> "<span class=\"squary\">$e</span>" }.joinToString("")
    }

    fun clearLastChosen() {
        this.lastChosen = ""
    }

    fun hasLastChosen(): Boolean {
        console.log("lastChosen is [" + this.lastChosen + "]")
        console.log("lastChosen length is " + this.lastChosen.length)
        return this.lastChosen.isNotEmpty()
    }

    fun getLastChosen(): String {
        return lastChosen
    }

    private fun setLastChosen(newValue: String) {
        this.lastChosen = newValue
        console.log("Last chosen is now " + this.lastChosen)
    }

    fun updateInstructions(team: Team, roll: Int, moves: Map<Square, Square?>, continueFunction: () -> Unit) {
        val instructionsDiv = pageObject.findInstructionsDiv()
        if (null != instructionsDiv.firstChild) {
            instructionsDiv.removeChild(instructionsDiv.firstChild!!)
        }
        if (null != instructionsDiv.firstChild) {
            instructionsDiv.removeChild(instructionsDiv.firstChild!!)
        }

        val p = document.createElement("p") as HTMLParagraphElement
        p.innerText = team.name + " (you) rolled " + roll
        instructionsDiv.appendChild(p)

        val ul = document.createElement("ul") as HTMLUListElement
        instructionsDiv.appendChild(ul)

        if (0 == roll) {
            makeButton("You rolled zero, can't move, bummer!!", ul, "Go") {
                continueFunction()
            }
        } else if (moves.isEmpty()) {
            makeButton("No legal moves, bummer!!", ul, "Go") {
                continueFunction()
            }
        } else {
            for ((index, entry) in moves.entries.withIndex()) {
                val message = "" + (1 + index) + " - " + entry.key + " to " + entry.value

                val callback: (Event) -> Unit = {
                    console.log("Clicked on button!")
                    setLastChosen((index + 1).toString())

                    // run continue function here
                    continueFunction()
                }
                val buttonText = "Go  "

                makeButton(message, ul, buttonText, callback)
            }
        }
    }

    private fun makeButton(
        message: String,
        ul: HTMLUListElement,
        buttonText: String,
        callback: (Event) -> Unit
    ) {
        val li = document.createElement("li") as HTMLLIElement

        li.innerHTML = createHTML().span {

            +message
        }
        ul.appendChild(li)

        val button = document.createElement("button") as HTMLButtonElement

        button.innerHTML = buttonText
        button.addEventListener("click", callback)
        li.prepend(button)
    }
}