package com.damon140.ur;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.damon140.ur.Square.off_board_unstarted;
import static com.damon140.ur.Ur.MoveResult.illegal;
import static com.damon140.ur.Ur.MoveResult.legal;

public class Ur {

    private final PlayArea playArea;

    public Ur(PlayArea playArea) throws NoSuchAlgorithmException {
        this.playArea = playArea;
    }

    public Team currentTeam() {
        return this.playArea.currentTeam();
    }

    public boolean skipTurn(int count) {
        if (0 != count) {
            return false;
        }

        this.playArea.swapTeam();
        return true;
    }

    // FIXME: upgrade to an enum of results, ok, underrun, counter not at start, collision with own counter
    // success, success_takes_other, over_run, collision_self, collision_other, illegal, game_won

    public enum MoveResult{ illegal, legal, gameOver };

    public MoveResult moveCounter(Square square, int count) {
        return moveCounter(playArea.currentTeam(), square, count);
    }

    public MoveResult moveCounter(Team team, Square fromSquare, int count) {
        // FIXMME: check correct team,
        if (playArea.allCountersStarted(team)) {
            return illegal; // can't add any more counters
        }

        if (fromSquare != off_board_unstarted
                && playArea.occupied(fromSquare)
                && playArea.get(fromSquare) != team) {
            return illegal; // teams counter not on square to move from
        }

        Optional<Square> newSquareOpt = fromSquare.calculateNewSquare(team, count);
        if (newSquareOpt.isEmpty()) {
            return illegal;
        }
        Square newSquare = newSquareOpt.get();

        if (0 == count) {
            return illegal; // illegal move of zero
        }

        Team occupant = playArea.get(newSquare);

        // FIXME: Damon safe square logic needed here
        if (null != occupant) {
            if (team == occupant) {
                return illegal; // clashes with own counter
            } else {
                // FIXME: to an unstarted counter change??
            }
        }

        // move counter
        playArea.move(fromSquare, newSquare, team);

        if (playArea.allCompleted(team)) {
            return MoveResult.gameOver;
        }

        if (newSquare.dontRollAgain()) {
            playArea.swapTeam();
        }

        return legal;
    }

    public Map<Square, Square> askMoves(Team team, int roll) {
        Map<Square, Square> moves = new HashMap<>();

        if (!this.playArea.allStartedOrComplete(team)) {
            // start a new counter
            moves.put(off_board_unstarted, canUseOrNull(team, off_board_unstarted.calculateNewSquare(team, roll)));
        }

        // current counters
        this.playArea.countersForTeam(team)
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
        if (!this.playArea.occupied(square)) {
            return square;
        }
        // or other counter and not a safe square
        Team occupantTeam = this.playArea.get(square);
        if (occupantTeam != team && square.isSafeSquare()) {
            return square;
        }

        return null;
    }

}
