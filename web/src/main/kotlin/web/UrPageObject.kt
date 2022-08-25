package web

import org.w3c.dom.*

class UrPageObject(document: Document) {

    // iteration 1
    private val document: Document

    // iteration 3
    private var canvasBoard: HTMLCanvasElement
    private var rollWhite: HTMLDivElement
    private var rollBlack: HTMLDivElement
    private var rollSpace: HTMLDivElement

    // iteration 4
    private var hmm:  HTMLAudioElement
    private var counterTakenSound:  HTMLAudioElement
    private var babowSound: HTMLAudioElement
    private var diceSound: HTMLAudioElement
    private var ticSound: HTMLAudioElement

    // iteration 5
    private var rollPartsWhite: HTMLDivElement
    private var rollPartsBlack: HTMLDivElement
    private var levelSlider: HTMLInputElement

    // iteration 6
    private var aiWinsSound: HTMLAudioElement
    private var clapsSound: HTMLAudioElement

    init {
        this.document = document

        // iteration 3
        this.canvasBoard = document.getElementById("canvas-board")!! as HTMLCanvasElement
        this.rollWhite = document.getElementById("roll-white")!! as HTMLDivElement
        this.rollBlack = document.getElementById("roll-black")!! as HTMLDivElement
        this.rollSpace = document.getElementById("roll-space2")!! as HTMLDivElement

        // iteration 4
        this.hmm = document.getElementById("hmm-audio")!! as HTMLAudioElement
        this.counterTakenSound = document.getElementById("counter-taken-audio")!! as HTMLAudioElement
        this.babowSound = document.getElementById("babow-sound")!! as HTMLAudioElement
        this.diceSound = document.getElementById("dice-sound")!! as HTMLAudioElement
        this.ticSound = document.getElementById("tic-sound")!! as HTMLAudioElement

        // iteration 5
        this.rollPartsWhite = document.getElementById("roll-parts-white")!! as HTMLDivElement
        this.rollPartsBlack = document.getElementById("roll-parts-black")!! as HTMLDivElement
        this.levelSlider = document.getElementById("ai-slider")!! as HTMLInputElement

        // iteration 6
        this.aiWinsSound = document.getElementById("ai-wins-sound")!! as HTMLAudioElement
        this.clapsSound = document.getElementById("claps-sound")!! as HTMLAudioElement
    }

    // Iteration 3
    fun findCanvasBoard(): HTMLCanvasElement {
        return this.canvasBoard
    }

    fun findRollWhite(): HTMLDivElement {
        return this.rollWhite
    }

    fun findRollSpace(): HTMLDivElement {
        return this.rollSpace
    }

    fun findRollBlack(): HTMLDivElement {
        return this.rollBlack
    }

    fun playHmmSound() {
        hmm.play()
    }

    fun playCounterTakenSound() {
        counterTakenSound.play()
    }

    fun playBaBowSound() {
        babowSound.currentTime = 0.15
        babowSound.play()
    }

    fun playDiceSound() {
        diceSound.volume = 0.7
        diceSound.currentTime = 0.65
        diceSound.play()
    }

    fun playTicSound() {
        ticSound.volume = 0.4
        ticSound.currentTime = 0.65
        ticSound.play()
    }

    // Iteration 5
    fun findRollPartsBlack(): HTMLDivElement {
        return this.rollPartsBlack
    }

    fun findRollPartsWhite(): HTMLDivElement {
        return this.rollPartsWhite
    }

    fun readLevel(): Int {
      return this.levelSlider.value.toInt()
    }

    // iteration 6
    fun playAiWinsSound() {
        this.aiWinsSound.play()
    }

    fun playClapsSound() {
        this.clapsSound.play()
    }

}
