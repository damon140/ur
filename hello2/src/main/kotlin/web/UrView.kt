package web

import com.damon140.ur.Square
import com.damon140.ur.Team
import kotlinx.browser.document
import kotlinx.html.div
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLLIElement
import org.w3c.dom.HTMLParagraphElement
import org.w3c.dom.HTMLUListElement
import org.w3c.dom.events.Event

public class UrView(pageObject: UrPageObject) {
    private val pageObject: UrPageObject
    private var lastChosen: String = ""

    init {
        this.pageObject = pageObject
    }

    /*

    white counters div     board di      black counters div
      top div               8 divs
        ol started          of spans
      bot div
        ol fin
     */


    fun updateWhiteCounters(line: String, unstarted: Int, completed: Int) {
        // iteration 1
        pageObject.findWhiteCountersDiv().innerHTML = createHTML().p {
            +line
        }

        // iteration 2
        pageObject.findWhiteUnstarted().innerHTML = "<li>w</li>".repeat(unstarted)
        pageObject.findWhiteFinished().innerHTML = "<li>w</li>".repeat(completed)
    }

    fun updateBlackCounters(line: String, unstarted: Int, completed: Int) {
        // iteration 1
        pageObject.findBlackCountersDiv().innerHTML = createHTML().p {
            +line
        }

        // iteration 2
        pageObject.findBlackUnstarted().innerHTML = "<li>b</li>".repeat(unstarted)
        pageObject.findBlackFinished().innerHTML = "<li>b</li>".repeat(completed)
    }

    fun updateBoard(line1: String, line2: String, line3: String, vertBoard: List<String>) {
        // iteration 1
        pageObject.findBoardDiv().innerHTML = createHTML().div {
            p {
                +line1
            }
            p {
                +line2
            }
            p {
                +line3
            }
        }

        // iteration 2
        console.log("Vert board:")
        console.log(vertBoard.joinToString("\n"))

        // FIXME: Damon drawn in new div here
        // "<span>X</span"
    }

    fun clearLastChosen() {
        this.lastChosen = "";
    }

    fun hasLastChosen():Boolean {
        console.log("lastChosen is [" + this.lastChosen + "]")
        console.log("lastChosen length is " + this.lastChosen.length)
        return 0 < this.lastChosen.length;
    }

    fun getLastChosen(): String {
        return lastChosen
    }

    private fun setLastChosen(newValue: String) {
        this.lastChosen = newValue
        console.log("Last chosen is now " + this.lastChosen);
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
        instructionsDiv.appendChild(p);

        val ul = document.createElement("ul") as HTMLUListElement
        instructionsDiv.appendChild(ul);

        if (0 == roll) {
            makeButton("You rolled zero, can't move, bummer!!", ul, "Go") {
                continueFunction();
            }
        } else if (moves.isEmpty()) {
            makeButton("No legal moves, bummer!!", ul, "Go") {
                continueFunction();
            }
        } else {
            for ((index, entry) in moves.entries.withIndex()) {
                val message = "" + (1 + index) + " - " + entry.key + " to " + entry.value

                val callback: (Event) -> Unit = {
                    console.log("Clicked on button!")
                    setLastChosen((index + 1).toString())

                    // run continue function here
                    continueFunction();
                }
                val buttonText = "Go";

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