package com.damon140.ur

enum class Team {
    white, black;

    val ch: String

    init {
        ch = name.substring(0, 1)
    }

    fun other(): Team {
        return values()[(ordinal + 1) % 2]
    }

    companion object {
        fun isTeamChar(value: String): Boolean {
            return value == white.ch || value == black.ch
        }

        fun fromCh(value: String): Team {
            return if (white.ch == value) white else black
        }
    }
}