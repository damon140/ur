import com.damon140.ur.Dice
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement

fun main() {
    console.log("Hello, ${greet()} from the console!!")

    val dice = Dice()

    //val e = HTMLParagraphElement()
    //val d = document.body!!.get

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
}

fun greet() = "world"
