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

public class UrView(pageObject: UrPageObject) {
    private val pageObject: UrPageObject
    private var lastChosen: String = ""

    init {
        this.pageObject = pageObject
    }

    fun updateWhiteCounters(line: String) {
        pageObject.findWhiteCountersDiv().innerHTML = createHTML().p {
            +line
        }
    }

    fun updateBlackCounters(line: String) {
        pageObject.findBlackCountersDiv().innerHTML = createHTML().p {
            +line
        }
    }

    fun updateBoard(line1: String, line2: String, line3: String) {
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

        for ((index, entry) in moves.entries.withIndex()) {
            val li = document.createElement("li") as HTMLLIElement

            li.innerHTML = createHTML().span {
                +("" + (1 + index) + " - " + entry.key + " to " + entry.value)
            }
            ul.appendChild(li)

            val button = document.createElement("button") as HTMLButtonElement
            button.innerHTML = "Go"
            button.addEventListener("click", {
                console.log("Clicked on button!")
                setLastChosen((index + 1).toString())

                // run continue function here
                continueFunction();
            })

            li.prepend(button)
        }
    }
}