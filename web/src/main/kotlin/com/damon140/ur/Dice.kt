package com.damon140.ur

class Dice {

    fun roll(): Int {
        return (0..1).random() + (0..1).random() + (0..1).random() + (0..1).random()
    }

}