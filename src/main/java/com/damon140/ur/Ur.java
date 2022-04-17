package com.damon140.ur;

import com.damon140.ur.CounterPositions.Square;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.damon140.ur.CounterPositions.Square.off_board_unstarted;

import static com.damon140.ur.CounterPositions.calculateNewSquare;

// FIXME: Rename to UrEngine??
public class Ur {

    private final CounterPositions counterPositions;

    public Ur(CounterPositions counterPositions) throws NoSuchAlgorithmException {
        // FIXME: switch to pass in && remove accessor
        this.counterPositions = counterPositions;
    }

    // FIXME: use drawn baord instead
    @Deprecated
    public Set<String> state() {
        return this.counterPositions.state();
    }

    public Map<Square, Team> getCounters() {
        return this.counterPositions.getCounters();
    }

    public Team currentTeam() {
        return this.counterPositions.currentTeam();
    }

    // FIXME: call here
    public boolean skipTurn(int count) {
        if (0 != count) {
            return false;
        }

        this.counterPositions.swapTeam();

        return true;
    }

    public boolean moveCounter(Square square, int count) {
        return moveCounter(counterPositions.currentTeam(), square, count);
    }

    // FIXME: upgrade to an enum of results, ok, underrun, counter not at start, collision with own counter
    // success, success_takes_other, over_run, collision_self, collision_other, illegal

    public boolean moveCounter(Team team, Square fromSquare, int count) {

        // FIXMME: check correct team,
        // FIXME: + and x on board

        // FIXME: move condition to Board method allOut
        if (counterPositions.allCountersStarted(team)) {
            return false; // can't add any more counters
        }

        if (fromSquare != off_board_unstarted
                && counterPositions.getCounters().containsKey(fromSquare)
                && counterPositions.getCounters().get(fromSquare) != team) {
            return false; // teams counter not on square to move from
        }

        Optional<Square> newSquareOpt = calculateNewSquare(team, fromSquare, count);
        if (newSquareOpt.isEmpty()) {
            return false;
        }
        Square newSquare = newSquareOpt.get();

        if (0 == count) {
            return false; // illegal move of zero
        }

        Team occupant = counterPositions.getCounters().get(newSquare);

        // FIXME: Damon safe square logic needed here
        if (null != occupant) {
            if (team == occupant) {
                return false; // clashes with own counter
            } else {
                // FIXME: to an unstarted counter change??
            }
        }

        // move counter
        counterPositions.getCounters().remove(fromSquare);
        if (newSquare != Square.off_board_finished) {
            counterPositions.getCounters().put(newSquare, team);
        } else {
            counterPositions.getCompletedCounters().put(team, 1 + counterPositions.getCompletedCounters().get(team));
        }

        if (newSquare.dontRollAgain()) {
            counterPositions.swapTeam();
        }

        return true;
    }

    public Map<Square, Square> askMoves(Team team, int roll) {
        Map<Square, Square> moves = new HashMap<>();

        if (!this.counterPositions.allStartedOrComplete(team)) {
            // start a new counter
            moves.put(off_board_unstarted, canUseOrNull(team, calculateNewSquare(team, off_board_unstarted, roll)));
        }

        // current counters
        this.counterPositions.getCounters().entrySet()
                .stream()
                .filter(entry -> team == entry.getValue())
                .map(Map.Entry::getKey)
                .forEach(startSquare -> {
                    Square endSquare = canUseOrNull(team, calculateNewSquare(team, startSquare, roll));
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
        if (!this.counterPositions.getCounters().containsKey(square)) {
            return square;
        }
        // or other counter and not a safe square
        Team occupantTeam = this.counterPositions.getCounters().get(square);
        if (occupantTeam != team && square.isSafeSquare()) {
            return square;
        }

        return null;
    }

}
