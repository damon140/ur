package com.damon140.ur;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import static com.damon140.ur.Board.Square.*;
import static com.damon140.ur.Board.Team.black;
import static com.damon140.ur.Board.Team.white;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UrTest {

    private Deque<Boolean> moveResult = new ArrayDeque<>();
    private Map<Board.Square, Board.Square> lastAskMoves = Map.of();
    private Board board;
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
                ...w  ..
                ........
                ....  ..""");
        thenHorizontalFullBoardIs("""
                wwwwww  
                ...w  ..
                ........
                ....  ..
                bbbbbbb""");
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
                wwwwww  
                ...w  ..
                ........
                ...b  ..
                bbbbbb  """);
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
              wwwwww  
              w...  ..
              ........
              ....  ..
              bbbbbbb""");
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
              wwwwwww  
              ....  ..
              b.......
              ....  ..
              bbbbbb """);
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
              wwwwwww  
              ....  ..
              b.......
              ....  ..
              bbbbbb """);
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

        assertThat(board.countersHorizontal(white), is("wwwwww w"));

        thenItsBlacksMove();
        thenHorizontalFullBoardIs("""
              wwwwww w
              ....  ..
              ......b.
              ....  ..
              bbbbbb """);
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
              wwwwww w
              ....  ..
              ........
              ....  ..
              bbbbbb b""");
        thenAllMovesWereLegal();
    }

    @Test
    public void offBoardMove() {
        givenGame("""
                wwwww
              ....  w.
              ........
              ....  ..
               bbbbbbb""");
        whenAskMoves(white, 1);
        thenMovesAre(white_run_on_1, off_board_finished);
    }

    @Test
    public void noMovesForRoll() {
        givenGame("""
               wwwwww
              ....  w.
              ........
              ....  ..
               bbbbbbb""");
        whenAskMoves(white, 2);
        thenNoMovesAreLegal();
    }

    @Test
    public void manyMovesForWhite() {
        givenGame("""
              
              .w.w  w.
              w.w.w.w.
              ....  ..
               bbbbbbb""");
        whenAskMoves(white, 1);
        thenMovesAre(white_run_on_2, white_run_on_4, shared_2, shared_4, shared_6, shared_8, off_board_finished);
    }



    // --------------------------------------
    // Given section
    // --------------------------------------

    public void givenNewGame()  {
        try {
            board = new Board();
            ur = new Ur(board);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public void givenGame(String game) {
        try {
            board = new Board(game);
            ur = new Ur(board);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    // --------------------------------------
    // When section
    // --------------------------------------

    private void whenMove(Board.Team team, Board.Square square, int i) {
        boolean result = ur.moveCounter(new Ur.Move(team, square, i));
        moveResult.add(result);
    }

    private void whenAskMoves(Board.Team team, int roll) {
        this.lastAskMoves = ur.askMoves(team, roll);
    }

    // --------------------------------------
    // Then section
    // --------------------------------------


    private void thenNoMovesAreLegal() {
        assertThat(this.lastAskMoves, is(Map.of()));
    }

    public void thenStateIsInitial() {
        // FIXME: need a state function, use multi line strings
        Set<String> state = ur.state();
        System.out.println(state);

        // TODO: switch to contains helper & yaml??
        assertThat(state.contains("currentTeam: white"), is(true));
        assertThat(state.contains("counters: {}"), is(true));
        assertThat(state.contains("completedCounters: {white=0, black=0}"), is(true));
    }

    private void thenWhiteHasCounterAt(Board.Square square) {
        assertThat(ur.getCounters().get(square), is(white));
    }

    private void thenBlackHasCounterAt(Board.Square square) {
        assertThat(ur.getCounters().get(square), is(black));
    }

    private void thenMoveWasIllegal() {
        assertThat(moveResult.peekLast(), is(false));
    }

    private void thenSmallBoardIs(String s) {
        String smallBoard = this.board.horizontalSmallBoardStrings()
                .stream()
                .collect(Collectors.joining("\n"));
        assertThat(s, is(smallBoard));
    }

    private void thenMoveWasLegal() {
        assertThat(this.moveResult.peekLast(), is(true));
    }

    private void thenHorizontalFullBoardIs(String wantedBoard) {
        String board = this.board.horizontalFullBoardStrings()
                .stream()
                .map(l -> l.trim())
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
          if (!r) {
              assertThat("Move [" + (i + 1) + "] was illegal", false, is(true));
          }
        }
    }

    private void thenWhiteCompletedCountIs(int count) {
        assertThat(this.board.completedCount(white), is(count));
    }

    private void thenMovesAre(Board.Square... destinationSquares) {
        List<Board.Square> sortedInput = Arrays.asList(destinationSquares)
                .stream()
                .sorted()
                .toList();

        List<Board.Square> lastAskMovesDestinatinons = this.lastAskMoves
                    .values()
                    .stream()
                    .sorted()
                .toList();

        assertThat(lastAskMovesDestinatinons, is(sortedInput));
    }

    // TODO: add to all test failings
    public void printBoard() {
        this.board.horizontalFullBoardStrings()
                .stream()
                .map(l -> l.trim())
                .forEach(l -> System.out.println(l));
    }

}
