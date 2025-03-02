package web

class UrWebSound (pageObject: UrPageObject) {

    // FIXME: Damon upgrade to web apis

    /*
    const audioContext = new (window.AudioContext || window.webkitAudioContext)();

    // Function to load an audio file into an AudioBuffer
    function loadAudio(url) {
        return fetch(url)
            .then(response => response.arrayBuffer())
            .then(data => audioContext.decodeAudioData(data));
    }

    // Load the three sounds
    const soundUrls = ['ai-wins.mp3', 'babow.mp3', 'claps.mp3'];
    const sounds = [];

    Promise.all(soundUrls.map(url => loadAudio(url))).then(loadedSounds => {
        loadedSounds.forEach((sound, index) => {
            sounds[index] = sound;
        });
    });

    // Function to play all three sounds
    function playSounds() {
        sounds.forEach(sound => {
            const source = audioContext.createBufferSource();
            source.buffer = sound;
            source.connect(audioContext.destination);
            source.start();
        });
    }
     */


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

