package com.damon140.ur

class PlayArea {
    private val counters: MutableMap<Square, Team>
    private val completedCounters: MutableMap<Team, Int>
    private var currentTeam = Team.white // white starts

    init {
        counters = HashMap()
        completedCounters = HashMap<Team, Int>()
        completedCounters[Team.black] = 0
        completedCounters[Team.white] = 0
    }

    fun moveTakes(team: Team, fromSquare: Square, toSquare: Square): Boolean {
        return teamHasCounterOn(team, fromSquare)
                && teamHasCounterOn(team.other(), toSquare);
    }

    fun moveIsOnShareRace(team: Team, fromSquare: Square, toSquare: Square): Boolean {
        return teamHasCounterOn(team, fromSquare)
                && empty(toSquare)
                && fromSquare.isRaceSquare()
    }

    fun empty(square: Square): Boolean {
        return !counters.containsKey(square)
    }

    fun teamHasCounterOn(team: Team, square: Square): Boolean {
        return counters[square] == team
    }

    fun completedCount(team: Team): Int {
        return completedCounters[team]!!
    }

    fun countersForTeam(team: Team): Set<Square> {
        return counters.entries
            .filter {e -> team === e.value }
            .map {e -> e.key}
            .toSet()
    }

    operator fun get(square: Square): Team? {
        return counters[square]
    }

    fun occupied(square: Square): Boolean {
        return counters.containsKey(square)
    }

    fun currentTeam(): Team {
        return currentTeam
    }

    fun swapTeam() {
        currentTeam = currentTeam.other()
    }

    fun allCountersStarted(team: Team): Boolean {
        return COUNTERS_PER_PLAYER == completedCounters[team]
    }

    fun unstartedCount(team: Team): Int {
        val inProgressCounters: Int = counters.entries
            .filter {e -> team === e.value }
            .count()
        val finishedCounters = completedCounters[team]!!
        return COUNTERS_PER_PLAYER - (finishedCounters + inProgressCounters.toInt())
    }

    fun allStartedOrComplete(team: Team): Boolean {
        return 0 == unstartedCount(team)
    }

    fun inPlayCount(team: Team): Int {
        return counters.values
            .filter { t: Team -> team === t }
            .count()
    }

    /** All counters currently on the board from both teams.  */
    fun inPlayCount(): Int {
        return counters.size
    }

    fun move(fromSquare: Square, newSquare: Square, team: Team) {
        counters.remove(fromSquare)
        if (newSquare !== Square.off_board_finished) {
            counters[newSquare] = team
        } else {
            completedCounters[team] = 1 + completedCounters[team]!!
        }
    }

    fun allCompleted(team: Team): Boolean {
        return COUNTERS_PER_PLAYER == completedCounters[team]
    }

    companion object {
        const val COUNTERS_PER_PLAYER = 7
    }
}