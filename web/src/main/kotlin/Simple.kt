import com.damon140.ur.Dice
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement

import com.damon140.ur.*

fun main() {
    console.log("Hello, ${greet()} from the console!!")

    val email = document.getElementById("email") as HTMLInputElement
    email.value = "hadi@jetbrains.com"

    // TODO: makes weird import error
//    import kotlinx.html.*
//    import kotlinx.html.dom.*
//    document.body!!.append.div {
//        h1 {
//            +"Welcome to Kotlin/JS!"
//        }
//        p {
//            +"Fancy joining this year's "
//            a("https://kotlinconf.com/") {
//                +"KotlinConf"
//            }
//            +"?"
//        }
//    }

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

    console.log("Full board: " + f);

}

fun greet() = "world"
