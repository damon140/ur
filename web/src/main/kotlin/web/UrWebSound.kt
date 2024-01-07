package web

class UrWebSound (pageObject: UrPageObject) {

    // FIXME: Damon, want to upgrade this to web audiop APIs for multiple sound playing etc
    // https://stackoverflow.com/questions/67784820/js-play-multile-audio-sources-simultaneously-when-loaded

    private val pageObject: UrPageObject

    init {
        this.pageObject = pageObject

        listOf("counter-taken-audio", "dice-sound", "tic-sound", "ting-sound",
            "babow-sound", "claps-sound", "wah-wah1-sound", "wah-wah2-sound",
            "hmm-audio", "ai-wins-sound").forEach {
            this.pageObject.bindAudioVolumeFromBase(it)
        }
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

