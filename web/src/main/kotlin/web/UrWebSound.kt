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

    // FIXME: Damon, call this
    fun playCounterMovedHomeSound() {
        pageObject.playCounterMovedHomeSound()
    }

}

