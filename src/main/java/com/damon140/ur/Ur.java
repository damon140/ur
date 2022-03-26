package com.damon140.ur;

import lombok.Data;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class Ur {

    public enum Square {
        off_board_unstarted,

        top_run_on_1,
        top_run_on_2,
        top_run_on_3,
        top_run_on_4,

        top_run_off_1,
        top_run_off_2,

        shared_1,
        shared_2,
        shared_3,
        shared_4,
        shared_5,
        shared_6,
        shared_7,
        shared_8,

        bottom_run_on_1,
        bottom_run_on_2,
        bottom_run_on_3,
        bottom_run_on_4,

        bottom_run_off_1,
        bottom_run_off_2,

        off_board_finished,
        ;
    }

    private final Map<Square, Team> counters;

    private final Map<Team, Integer> completedCounters;

    private Team currentTeam = Team.white; // white starts

    public Ur() throws NoSuchAlgorithmException {
        counters = new HashMap<>();
        completedCounters = new HashMap<>();
        completedCounters.put(Team.black, 0);
        completedCounters.put(Team.white, 0);
    }

    public String state() {

    }

    public static Square calculateNewSquare(Team team, Square square) {
        // special cases
        switch (square) {
            case top_run_on_4:
            case bottom_run_on_4:
                return Square.shared_1;
            case shared_8:
                return team == Team.black ? Square.top_run_off_1 : Square.bottom_run_off_1;
            case top_run_off_2:
            case bottom_run_off_2:
                return Square.off_board_finished;
                // FIXME: test here
            case off_board_unstarted:
                return team == Team.black ? Square.top_run_on_1 : Square.bottom_run_on_1;
        }
        // regular sequence
        return Square.values()[1 + square.ordinal()];
    }

    public static Square calculateNewSquare(Team team, Square square, int count) {
        if (square == Square.off_board_finished) {
            return Square.off_board_finished;
        }
        Square newSquare = square;
        for (int looper = 0; looper < count; looper++) {
            newSquare = calculateNewSquare(team, newSquare);
        }
        return newSquare;
    }

    private static final int COUNTERS_PER_PLAYER = 7;

    @Data
    public static class Move {
        private final Team team;
        private Square square;
        private int count;
    }

    // FIXME: upgrade to an enum of results, ok, underrun, counter not at start, collision with own counter
    /*
    success, success_takes_other, over_run, collision_self, collision_other, illegal
     */

    public Team currentTeam() {
        return this.currentTeam;
    }

    public boolean skipTurn(Move move) {
        if (0 != move.count) {
            return false;
        }

        this.currentTeam = this.currentTeam.other();
        return true;
    }

    public boolean moveCounter(Move move) {
        return moveCounter(move.team, move.square, move.count);
    }

    private boolean moveCounter(Team team, Square square, int count) {
        if (COUNTERS_PER_PLAYER == completedCounters.get(team)) {
            return false; // can't add any more counters
        }

        if (counters.containsKey(square) && counters.get(square) == team) {
            return false; // counter not on square
        }

        Square newSquare = calculateNewSquare(team, square, count);

        if (0 == count) {
            return false; // illegal move of zero
        }

        Team occupant = counters.get(newSquare);

        // FIXME: Damon is safe square logic needed here?
        if (null != occupant) {
            if (team == occupant) {
                return false; // clashes with own counter
            } else {
                // FIXME: impl take other counter here
            }
        }

        counters.remove(square);
        counters.put(newSquare, team);

        if (newSquare == Square.off_board_finished) {
            completedCounters.put(team, 1 + completedCounters.get(team));
        }

        currentTeam = this.currentTeam.other();

        return true;
    }

    public enum Team {
        white, black;
        public Team other() {
            return Team.values()[(this.ordinal() + 1) % 2];
        }
    };

    public static class Dice {

        private final SecureRandom random;

        public Dice() throws NoSuchAlgorithmException {
            random = SecureRandom.getInstanceStrong();
        }

        public int roll() {
            return random.ints(4, 0, 2)
                    .sum();
        }

    }

}
