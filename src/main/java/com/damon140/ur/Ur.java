package com.damon140.ur;

import com.damon140.ur.Board.Square;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.damon140.ur.Board.Square.off_board_unstarted;
import static com.damon140.ur.Board.Team;
import static com.damon140.ur.Board.calculateNewSquare;

// FIXME: Rename to UrEngine??
public class Ur {

    private final Board board;

    public Ur(Board board) throws NoSuchAlgorithmException {
        // FIXME: switch to pass in && remove accessor
        this.board = board;
    }

    public Set<String> state() {
        return this.board.state();
    }

    public Map<Square, Team> getCounters() {
        return this.board.getCounters();
    }

    public Team currentTeam() {
        return this.board.currentTeam();
    }

    @AllArgsConstructor
    @Data
    public static class Move {
        private final Team team;
        private Square square;
        private int count;
    }


    // FIXME: upgrade to an enum of results, ok, underrun, counter not at start, collision with own counter

    public boolean skipTurn(Move move) {
        if (0 != move.count) {
            return false;
        }

        this.board.swapTeam();

        return true;
    }

    /*
    success, success_takes_other, over_run, collision_self, collision_other, illegal
     */

    public boolean moveCounter(Move move) {
        return moveCounter(move.team, move.square, move.count);
    }

    public boolean moveCounter(Square square, int count) {
        return moveCounter(board.currentTeam(), square, count);
    }

    private boolean moveCounter(Team team, Square fromSquare, int count) {

        // FIXMME: check correct team,
        // FIXME: + and x on board

        // FIXME: move condition to Board method allOut
        if (board.allCountersStarted(team)) {
            return false; // can't add any more counters
        }

        if (fromSquare != off_board_unstarted
                && board.getCounters().containsKey(fromSquare)
                && board.getCounters().get(fromSquare) != team) {
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

        Team occupant = board.getCounters().get(newSquare);

        // FIXME: Damon safe square logic needed here
        if (null != occupant) {
            if (team == occupant) {
                return false; // clashes with own counter
            } else {
                // FIXME: to an unstarted counter change??
            }
        }

        // move counter
        board.getCounters().remove(fromSquare);
        if (newSquare != Square.off_board_finished) {
            board.getCounters().put(newSquare, team);
        } else {
            board.getCompletedCounters().put(team, 1 + board.getCompletedCounters().get(team));
        }

        if (newSquare.dontRollAgain()) {
            board.swapTeam();
        }

        return true;
    }

    // FIXME: switch to to from map
    public Map<Square, Square> askMoves(Team team, int roll) {
        Map<Square, Square> moves = new HashMap<>();

        if (!this.board.allStartedOrComplete(team)) {
            // start a new counter
            moves.put(off_board_unstarted, canUseOrNull(team, calculateNewSquare(team, off_board_unstarted, roll)));
        }

        // current counters
        this.board.getCounters().entrySet()
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
        if (!this.board.getCounters().containsKey(square)) {
            return square;
        }
        // or other counter and not a safe square
        Team occupantTeam = this.board.getCounters().get(square);
        if (occupantTeam != team && square.isSafeSquare()) {
            return square;
        }

        return null;
    }

}
