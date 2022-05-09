import com.damon140.ur.Dice
import com.damon140.ur.HorizontalDrawnBoard
import com.damon140.ur.PlayArea
import com.damon140.ur.Ur
import kotlinx.html.dom.append
import org.w3c.dom.Node
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*

fun main() {
    window.onload = {
        document.body?.sayHello()

        val dice = Dice()

        console.log("rolled " + dice.roll())

        val playArea = PlayArea()
        console.log("made play area")

        val ur = Ur(playArea)
        console.log("made ur")

        //ur.askMoves(Team.white, 1)
        //console.log("made ur")

        console.log("will make a board")
        val horizontalDrawnBoard = HorizontalDrawnBoard(playArea)

        console.log("made a board")

        val f = horizontalDrawnBoard.fullBoard()

        console.log("Full board: " + f)

//        val button = document.querySelector("#button")!!
        val button = document.getElementById("button1")!!
        button.addEventListener("click", {console.log("Clicked on button!}")
        })

    }
}

fun Node.sayHello() {
    append {
        div {
            +"Hello from JS"
        }
        div {
            buttonInput {
                id = "button1"
                value = "a button"
            }
        }
    }
}


