package com.damon140.ur;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.damon140.ur.Square.off_board_unstarted;

public class Ur {

    private final Counters counters;

    public Ur(Counters counters) throws NoSuchAlgorithmException {
        this.counters = counters;
    }

    public Team currentTeam() {
        return this.counters.currentTeam();
    }

    public boolean skipTurn(int count) {
        if (0 != count) {
            return false;
        }

        this.counters.swapTeam();
        return true;
    }

    public boolean moveCounter(Square square, int count) {
        return moveCounter(counters.currentTeam(), square, count);
    }

    // FIXME: upgrade to an enum of results, ok, underrun, counter not at start, collision with own counter
    // success, success_takes_other, over_run, collision_self, collision_other, illegal

    public boolean moveCounter(Team team, Square fromSquare, int count) {

        // FIXMME: check correct team,

        if (counters.allCountersStarted(team)) {
            return false; // can't add any more counters
        }

        if (fromSquare != off_board_unstarted
                && counters.occupied(fromSquare)
                && counters.get(fromSquare) != team) {
            return false; // teams counter not on square to move from
        }

        Optional<Square> newSquareOpt = fromSquare.calculateNewSquare(team, count);
        if (newSquareOpt.isEmpty()) {
            return false;
        }
        Square newSquare = newSquareOpt.get();

        if (0 == count) {
            return false; // illegal move of zero
        }

        Team occupant = counters.get(newSquare);

        // FIXME: Damon safe square logic needed here
        if (null != occupant) {
            if (team == occupant) {
                return false; // clashes with own counter
            } else {
                // FIXME: to an unstarted counter change??
            }
        }

        // move counter
        counters.move(fromSquare, newSquare, team);

        if (newSquare.dontRollAgain()) {
            counters.swapTeam();
        }

        return true;
    }

    public Map<Square, Square> askMoves(Team team, int roll) {
        Map<Square, Square> moves = new HashMap<>();

        if (!this.counters.allStartedOrComplete(team)) {
            // start a new counter
            moves.put(off_board_unstarted, canUseOrNull(team, off_board_unstarted.calculateNewSquare(team, roll)));
        }

        // current counters
        this.counters.countersForTeam(team)
                .forEach(startSquare -> {
                    Square endSquare = canUseOrNull(team, startSquare.calculateNewSquare(team, roll));
                    moves.put(startSquare, endSquare);
                });

        moves.values().removeIf(Objects::isNull);

        return moves;
    }

    private Square canUseOrNull(Team team, Optional<Square> squareOpt) {
        if (squareOpt.isEmpty()) {
            return null;
        }

        Square square = squareOpt.get();
        // if empty
        if (!this.counters.occupied(square)) {
            return square;
        }
        // or other counter and not a safe square
        Team occupantTeam = this.counters.get(square);
        if (occupantTeam != team && square.isSafeSquare()) {
            return square;
        }

        return null;
    }

}
