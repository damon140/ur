import kotlinx.browser.window
import web.WebGame

fun main() {
    window.onload = {
        val webGame = WebGame()

//        webGame.fakeGame("""
//            |wwwwww
//            *...  w.
//            ...*....
//            *..b  *.
//            b|bbbbb""".trimIndent())

        webGame.playPart1()
    }
}
