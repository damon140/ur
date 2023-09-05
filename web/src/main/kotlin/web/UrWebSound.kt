package web

class UrWebSound (pageObject: UrPageObject) {

    private val pageObject: UrPageObject

    init {
        this.pageObject = pageObject
    }

    fun playDiceRoll() {
        pageObject.playDiceSound()
    }

    fun playHmm() {
        pageObject.playHmmSound()
    }

    fun playCounterTakenSound() {
        pageObject.playCounterTakenSound()
    }

    fun playCantMoveSound() {
        pageObject.playCantMoveSound()
    }

    fun playCounterMovedHomeSound() {
        pageObject.playCounterMovedHomeSound()
    }

    fun playTicSound() {
        pageObject.playTicSound()
    }

    fun playClapsSound() {
        pageObject.playClapsSound()
    }

    fun playAiWinsSound() {
        pageObject.playAiWinsSound()
    }

    fun playBaBowSound() {
        pageObject.playBaBowSound()
    }

}
