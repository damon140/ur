package com.damon140.ur

class Dice {
    private var lastValue = 0
    private var lastString = ""

    fun roll() {
        val d1 = (0..1).random()
        val d2 = (0..1).random()
        val d3 = (0..1).random()
        val d4 = (0..1).random()

        this.lastValue = d1 + d2 + d3 + d4
        this.lastString = ("" + d1) + ("+" + d2) + ("+" + d3) + ("+" + d4)
    }
    fun getLastValue(): Int {
        return lastValue
    }

    fun getLastString(): String {
        return lastString
    }
}