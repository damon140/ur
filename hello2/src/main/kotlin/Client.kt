import com.damon140.ur.Dice
import com.damon140.ur.HorizontalDrawnBoard
import com.damon140.ur.PlayArea
import com.damon140.ur.Ur
import kotlinx.html.dom.append
import org.w3c.dom.Node
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import org.w3c.dom.HTMLDivElement


fun main() {
    window.onload = {
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

//        val button = document.getElementById("button1")!!
//        button.addEventListener("click", {console.log("Clicked on button!}")
//        })

        // FIXME: Damon code may be dying here??
//        var div1 = document.getElementById("board") as HTMLDivElement
//        if (div1 != null) {
//            div1.remove();
//        }

        //document.body?.sayHello()


        val smallBoard: List<String> = horizontalDrawnBoard.smallBoard()

        var root = document.getElementById("root")!! as HTMLDivElement
        root.sayHello()
        // root.drawWhiteCounters
        // root.drawBlackCounters
        root.drawBoard(smallBoard.get(0)!!, smallBoard.get(1)!!, smallBoard.get(2)!!)


//        root.wut("stupid")
//        root.wut("only append")
//        root.wut("no set")

        //draw(document, f)

        // last line must be a return value for some reason
        Unit
    }
}

private fun HTMLDivElement.drawBoard(get: String, get1: String, get2: String) {
    append {
        div {
            id = "board"
            p {
                + "board"
            }
            p {
                + get
            }
            p {
                + get1
            }
            p {
                + get2
            }
        }
    }
}

fun Node.sayHello() {
    append {
        div {
            +"Hello from JS"
        }

        div {
            id = "div1"
        }
        div {
            buttonInput {
                id = "button1"
                value = "a button"
            }
        }
    }
}
