import kotlinx.browser.window
import web.WebGame


fun main() {
    // TODO: push almost all to new classes/ files
    window.onload = {
        WebGame()

        // last line must be a return value for some reason
        Unit
    }
}


