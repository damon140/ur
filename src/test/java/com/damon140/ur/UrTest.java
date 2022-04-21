package com.damon140.ur;

import com.damon140.ur.Ur.MoveResult;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import static com.damon140.ur.Square.*;
import static com.damon140.ur.Team.black;
import static com.damon140.ur.Team.white;
import static com.damon140.ur.Ur.MoveResult.illegal;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UrTest {

    private Deque<MoveResult> moveResult = new ArrayDeque<>();
    private Map<Square, Square> lastAskMoves = Map.of();
    private PlayArea playArea;
    private DrawnBoard drawnBoard;
    private Ur ur;

    @Test
    public void initialStateTest()  {
        givenNewGame();
        thenStateIsInitial();
        thenAllMovesWereLegal();
    }

    @Test
    public void whiteFirstMove() {
        givenNewGame();
        whenAskMoves(white, 1);
        thenMovesAre(white_run_on_1);
    }

    @Test
    public void whiteRolls1()  {
        givenNewGame();
        whenMove(white, off_board_unstarted, 1);
        thenWhiteHasCounterAt(white_run_on_1);
        thenMoveWasLegal();
        thenSmallBoardIs("""
                *..w  *.
                ...*....
                *...  *.""");
        thenHorizontalFullBoardIs("""
                wwwwww | 
                *..w  *.
                ...*....
                *...  *.
                bbbbbbb|""");
        thenItsBlacksMove();
    }

    @Test
    public void whiteAndBlackRoll1()  {
        givenNewGame();
        whenMove(white, off_board_unstarted, 1);
        thenMoveWasLegal();
        whenMove(black, off_board_unstarted, 1);
        thenMoveWasLegal();
        thenWhiteHasCounterAt(white_run_on_1);
        thenBlackHasCounterAt(black_run_on_1);
        thenMoveWasLegal();
        thenHorizontalFullBoardIs("""
                wwwwww |
                *..w  *.
                ...*....
                *..b  *.
                bbbbbb |""");
        thenItsWhitesMove();
    }

    @Test public void illegalMoveOntoOwnCounter() {
        givenNewGame();
        whenMove(white, off_board_unstarted, 1);
        whenMove(black, off_board_unstarted, 1);
        thenAllMovesWereLegal();
        whenMove(white, off_board_unstarted, 1);
        thenMoveWasIllegal();
        thenItsWhitesMove();
    }

    @Test
    public void move4ontoFlowerAndStillWhiteMove() {
       givenNewGame();
       whenMove(white, off_board_unstarted, 4);
       thenMoveWasLegal();
       thenItsWhitesMove();
       thenHorizontalFullBoardIs("""
              wwwwww |
              w...  *.
              ...*....
              *...  *.
              bbbbbbb|""");
    }

    @Test
    public void blackTakesWhite() {
        givenNewGame();
        whenMove(white, off_board_unstarted, 3);
        printBoard();
        whenMove(black, off_board_unstarted, 3);
        printBoard();
        whenMove(white, white_run_on_3, 2);
        printBoard();
        whenMove(black, black_run_on_3, 2);
        printBoard();

        thenItsWhitesMove();
        thenHorizontalFullBoardIs("""
              wwwwwww| 
              *...  *.
              b..*....
              *...  *.
              bbbbbb |""");
        thenAllMovesWereLegal();
    }

    @Test
    public void blackTakesWhiteUsingDoubles() {
        givenNewGame();
        whenMove(white, off_board_unstarted, 4);
        whenMove(white, white_run_on_4, 1);
        thenItsBlacksMove();
        whenMove(black, off_board_unstarted, 4);
        whenMove(black, black_run_on_4, 1);
        thenItsWhitesMove();
        thenHorizontalFullBoardIs("""
              wwwwwww| 
              *...  *.
              b..*....
              *...  *.
              bbbbbb |""");
        thenAllMovesWereLegal();
    }

    @Test
    public void whiteGetSafe() {
        givenNewGame();
        whenMove(white, off_board_unstarted, 4);
        whenMove(white, white_run_on_4, 4);
        whenMove(white, shared_4, 4);

        whenMove(black, off_board_unstarted, 4);
        whenMove(black, black_run_on_4, 4);
        whenMove(black, shared_4, 3);

        whenMove(white, shared_8, 3);
        thenWhiteCompletedCountIs(1);

        thenItsBlacksMove();
        thenHorizontalFullBoardIs("""
              wwwwww|w
              *...  *.
              ...*..b.
              *...  *.
              bbbbbb |""");
        thenAllMovesWereLegal();
    }

    @Test
    public void bothTeamsGetSafe() {
        givenNewGame();
        whenMove(white, off_board_unstarted, 4);
        whenMove(white, white_run_on_4, 4);
        whenMove(white, shared_4, 4);
        printBoard();

        whenMove(black, off_board_unstarted, 4);
        whenMove(black, black_run_on_4, 4);
        whenMove(black, shared_4, 3);
        printBoard();

        whenMove(white, shared_8, 3);
        printBoard();

        whenMove(black, shared_7, 4);
        printBoard();

        thenItsWhitesMove();
        thenHorizontalFullBoardIs("""
              wwwwww|w
              *...  *.
              ...*....
              *...  *.
              bbbbbb|b""");
        thenAllMovesWereLegal();
    }

    @Test
    public void offBoardMove() {
        givenGame("""
                wwwww
              *...  w.
              ...*....
              *...  *.
               bbbbbbb""");
        whenAskMoves(white, 1);
        thenMovesAre(white_run_on_1, off_board_finished);
    }

    @Test
    public void noMovesForRoll() {
        // FIXME: Damon fix parsing, is not recognising as white all finished
        givenGame("""
              |wwwwww
              *...  w.
              ...*....
              *...  *.
              |bbbbbbb""");
        whenAskMoves(white, 2);
        thenNoMovesAreLegal();
    }

    @Test
    public void manyMovesForWhite() {
        givenGame("""
              
              .w.w  w.
              w.w.w.w.
              *...  *.
               bbbbbbb""");
        whenAskMoves(white, 1);
        thenMovesAre(white_run_on_2, white_run_on_4, shared_2, shared_4, shared_6, shared_8, off_board_finished);
    }

    @Test
    public void whiteWon() {
        givenGame("""
              |wwwwww
              *...  w.
              ...*....
              *...  *.
               bbbbbbb""");

        whenMove(white, white_run_off_2, 1);
        thenWhiteWon();
    }

    // --------------------------------------
    // Given section
    // --------------------------------------

    public void givenNewGame()  {
        try {
            moveResult = new ArrayDeque<>();
            playArea = new PlayArea();
            drawnBoard = new DrawnBoard(playArea);
            ur = new Ur(playArea);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public void givenGame(String game) {
        try {
            moveResult = new ArrayDeque<>();
            playArea = DrawnBoard.parseCounters(game);
            drawnBoard = new DrawnBoard(playArea);
            ur = new Ur(playArea);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    // --------------------------------------
    // When section
    // --------------------------------------

    private void whenMove(Team team, Square square, int i) {
        var result = ur.moveCounter(team, square, i);
        moveResult.add(result);
    }

    private void whenAskMoves(Team team, int roll) {
        this.lastAskMoves = ur.askMoves(team, roll);
    }

    // --------------------------------------
    // Then section
    // --------------------------------------


    private void thenNoMovesAreLegal() {
        assertThat(this.lastAskMoves, is(Map.of()));
    }
    private void thenMoveWasIllegal() {
        assertThat(moveResult.peekLast(), is(illegal));
    }

    private void thenMoveWasLegal() {
        assertThat(this.moveResult.peekLast(), is(MoveResult.valid));
    }

    public void thenStateIsInitial() {
        assertThat(playArea.currentTeam(), is(white));
        assertThat(playArea.inPlayCount(), is(0));
        assertThat(playArea.completedCount(white), is(0));
        assertThat(playArea.completedCount(black), is(0));
    }

    private void thenWhiteHasCounterAt(Square square) {
        assertThat(playArea.get(square), is(white));
    }

    private void thenBlackHasCounterAt(Square square) {
        assertThat(playArea.get(square), is(black));
    }


    private void thenSmallBoardIs(String s) {
        String smallBoard = this.drawnBoard.horizontalSmallBoardStrings()
                .stream()
                .collect(Collectors.joining("\n"));
        assertThat(s, is(smallBoard));
    }

    private void thenHorizontalFullBoardIs(String wantedBoard) {
        String board = this.drawnBoard.horizontalFullBoardStrings()
                .stream()
                .map(String::trim)
                .collect(Collectors.joining("\n"));
        assertThat(board, is(wantedBoard));
    }

    private void thenItsWhitesMove() {
        assertThat(ur.currentTeam(), is(white));
    }

    private void thenItsBlacksMove() {
        assertThat(ur.currentTeam(), is(black));
    }

    private void thenAllMovesWereLegal() {
        for (int i = 0; i < this.moveResult.size(); i++) {
          var r = this.moveResult.getFirst();
          if (r == illegal) {
              assertThat("Move [" + (i + 1) + "] was illegal", false, is(true));
          }
        }
    }

    private void thenWhiteCompletedCountIs(int count) {
        assertThat(this.playArea.completedCount(white), is(count));
    }

    private void thenMovesAre(Square... destinationSquares) {
        List<Square> sortedInput = Arrays.stream(destinationSquares)
                .sorted()
                .toList();

        List<Square> lastAskMovesDestinatinons = this.lastAskMoves
                    .values()
                    .stream()
                    .sorted()
                .toList();

        assertThat(lastAskMovesDestinatinons, is(sortedInput));
    }

    private void thenWhiteWon() {
        assertThat(this.moveResult.peekLast(), is(MoveResult.gameWon));
    }

    // TODO: add to all test failings
    public void printBoard() {
        this.drawnBoard.horizontalFullBoardStrings()
                .stream()
                .map(String::trim)
                .forEach(System.out::println);
    }

}
