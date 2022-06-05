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

class UrHtmlView(pageObject: UrPageObject) {
    private val pageObject: UrPageObject
    private var lastChosen: String = ""

    init {
        this.pageObject = pageObject
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