package web

class UrWebSound (pageObject: UrPageObject) {

    private val pageObject: UrPageObject

    init {
        this.pageObject = pageObject

        this.pageObject.bindAudioVolumeFromBase("counter-taken-audio")
        this.pageObject.bindAudioVolumeFromBase("dice-sound")
        this.pageObject.bindAudioVolumeFromBase("tic-sound")
        this.pageObject.bindAudioVolumeFromBase("ting-sound")
        this.pageObject.bindAudioVolumeFromBase("babow-sound")
        this.pageObject.bindAudioVolumeFromBase("claps-sound")
        this.pageObject.bindAudioVolumeFromBase("wah-wah1-sound")
        this.pageObject.bindAudioVolumeFromBase("wah-wah2-sound")
        this.pageObject.bindAudioVolumeFromBase("hmm-audio")
        this.pageObject.bindAudioVolumeFromBase("ai-wins-sound")
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

