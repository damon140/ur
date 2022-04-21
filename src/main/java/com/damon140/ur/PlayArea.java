package com.damon140.ur;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PlayArea {

    public static final int COUNTERS_PER_PLAYER = 7;

    private final Map<Square, Team> counters;

    private final Map<Team, Integer> completedCounters;

    private Team currentTeam = Team.white; // white starts

    public PlayArea() throws NoSuchAlgorithmException {
        counters = new HashMap<>();
        completedCounters = new TreeMap<>();
        completedCounters.put(Team.black, 0);
        completedCounters.put(Team.white, 0);
    }

    public int completedCount(Team team) {
        return completedCounters.get(team);
    }

    public Set<Square> countersForTeam(Team team) {
        return this.counters.entrySet()
                .stream()
                .filter(entry -> team == entry.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Team get(Square square) {
        return this.counters.get(square);
    }

    public boolean occupied(Square square) {
        return this.counters.containsKey(square);
    }

    public Team currentTeam() {
        return this.currentTeam;
    }

    public void swapTeam() {
        this.currentTeam = this.currentTeam.other();
    }

    public boolean allCountersStarted(Team team) {
        return COUNTERS_PER_PLAYER == completedCounters.get(team);
    }

    public int unstartedCount(Team team) {
        long inProgressCounters = this.counters.entrySet()
                .stream()
                .filter(e -> team == e.getValue())
                .count();
        int finishedCounters = this.completedCounters.get(team);

        return COUNTERS_PER_PLAYER - finishedCounters - (int) inProgressCounters;
    }

    public boolean allStartedOrComplete(Team team) {
        int completed = this.completedCount(team);
        int inProgress = this.counters.size();
        return COUNTERS_PER_PLAYER == completed + inProgress;
    }

    public int inPlayCount(Team team) {
        return (int) counters.values().stream().filter(t -> team == t).count();
    }

    /** All counters currently on the board from both teams. */
    public int inPlayCount() {
        return this.counters.size();
    }

    public void move(Square fromSquare, Square newSquare, Team team) {
        // FIXME: Damon add a bit here about collisions, that is known only
        // to the counters

        counters.remove(fromSquare);
        if (newSquare != Square.off_board_finished) {
            counters.put(newSquare, team);
        } else {
            completedCounters.put(team, 1 + completedCounters.get(team));
        }
    }

    public boolean allCompleted(Team team) {
        return PlayArea.COUNTERS_PER_PLAYER == this.completedCounters.get(team);
    }

}
