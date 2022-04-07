package com.damon140.ur;

import com.damon140.ur.Ur.Dice;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.damon140.ur.Ur.Square.*;
import static com.damon140.ur.Ur.Team.black;
import static com.damon140.ur.Ur.Team.white;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UrTest {

    private Deque<Boolean> moveResult = new ArrayDeque<>();
    private List<Ur.Square> lastAskMoves;

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

        assertThat(ur.countersHorizontal(white), is("wwwwww w"));

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


    private Ur ur = null;

    // --------------------------------------
    // Given section
    // --------------------------------------

    public void givenNewGame()  {
        try {
            ur = new Ur();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    // --------------------------------------
    // When section
    // --------------------------------------

    private void whenMove(Ur.Team team, Ur.Square square, int i) {
        boolean result = ur.moveCounter(new Ur.Move(team, square, i));
        moveResult.add(result);
    }

    private void whenAskMoves(Ur.Team team, int roll) {
        this.lastAskMoves = ur.askMoves(team, roll);
    }


    // --------------------------------------
    // Then section
    // --------------------------------------

    public void thenStateIsInitial() {
        // FIXME: need a state function, use multi line strings
        Set<String> state = ur.state();
        System.out.println(state);

        // TODO: switch to contains helper & yaml??
        assertThat(state.contains("currentTeam: white"), is(true));
        assertThat(state.contains("counters: {}"), is(true));
        assertThat(state.contains("completedCounters: {white=0, black=0}"), is(true));
    }

    private void thenWhiteHasCounterAt(Ur.Square square) {
        assertThat(ur.getCounters().get(square), is(white));
    }

    private void thenBlackHasCounterAt(Ur.Square square) {
        assertThat(ur.getCounters().get(square), is(black));
    }

    private void thenMoveWasIllegal() {
        assertThat(moveResult.peekLast(), is(false));
    }

    private void thenSmallBoardIs(String s) {
        String smallBoard = this.ur.horizontalSmallBoardStrings()
                .stream()
                .collect(Collectors.joining("\n"));
        assertThat(s, is(smallBoard));
    }

    private void thenMoveWasLegal() {
        assertThat(this.moveResult.peekLast(), is(true));
    }

    private void thenHorizontalFullBoardIs(String wantedBoard) {
        String board = this.ur.horizontalFullBoardStrings()
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
        assertThat(ur.completedCount(white), is(count));
    }


    private void thenMovesAre(Ur.Square square) {
        assertThat(this.lastAskMoves, is(List.of(square)));
    }

    // TODO: add to all test failings
    public void printBoard() {
        this.ur.horizontalFullBoardStrings()
                .stream()
                .map(l -> l.trim())
                .forEach(l -> System.out.println(l));
    }

    @Test
    public void dice()  {
        Dice d;
        try {
            d = new Dice();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }

        IntSummaryStatistics rolls = IntStream.range(0, 1000)
                .map(i -> d.roll())
                .summaryStatistics();

        assertThat(rolls.getMin(), is(0));
        assertThat(rolls.getMax(), is(4));
    }

    @Test
    public void other() {
        assertThat(black.other(), is(white));
        assertThat(white.other(), is(black));
    }

    @Test
    public void calculateNewSquareNoArgs() {
        assertThat(Ur.calculateNewSquare(black, off_board_unstarted, 2), is(black_run_on_2));
        assertThat(Ur.calculateNewSquare(white, off_board_unstarted, 2), is(white_run_on_2));

        assertThat(Ur.calculateNewSquare(black, black_run_on_1, 1), is(black_run_on_2));
        assertThat(Ur.calculateNewSquare(black, black_run_on_1, 3), is(black_run_on_4));
        assertThat(Ur.calculateNewSquare(black, black_run_on_1, 4), is(shared_1));

        assertThat(Ur.calculateNewSquare(black, shared_1, 7), is(shared_8));

        assertThat(Ur.calculateNewSquare(black, shared_8, 1), is(black_run_off_1));
        assertThat(Ur.calculateNewSquare(white, shared_8, 1), is(white_run_off_1));

        assertThat(Ur.calculateNewSquare(black, black_run_off_1, 2), is(off_board_finished));
        assertThat(Ur.calculateNewSquare(white, white_run_off_1, 2), is(off_board_finished));
    }

    @Test
    public void calculateNewSquare() {
        assertThat(Ur.calculateNewSquare(black, off_board_unstarted), is(black_run_on_1));
        assertThat(Ur.calculateNewSquare(white, off_board_unstarted), is(white_run_on_1));

        // FIXME: these should be illegal
        assertThat(Ur.calculateNewSquare(white, black_run_on_1), is(black_run_on_2));
        assertThat(Ur.calculateNewSquare(white, black_run_on_2), is(black_run_on_3));
        assertThat(Ur.calculateNewSquare(white, black_run_on_3), is(black_run_on_4));

        assertThat(Ur.calculateNewSquare(black, black_run_on_1), is(black_run_on_2));
        assertThat(Ur.calculateNewSquare(black, black_run_on_2), is(black_run_on_3));
        assertThat(Ur.calculateNewSquare(black, black_run_on_3), is(black_run_on_4));

        assertThat(Ur.calculateNewSquare(white, black_run_on_4), is(shared_1));
        assertThat(Ur.calculateNewSquare(white, shared_1), is(shared_2));
        assertThat(Ur.calculateNewSquare(white, shared_2), is(shared_3));
        assertThat(Ur.calculateNewSquare(white, shared_3), is(shared_4));
        assertThat(Ur.calculateNewSquare(white, shared_4), is(shared_5));
        assertThat(Ur.calculateNewSquare(white, shared_5), is(shared_6));
        assertThat(Ur.calculateNewSquare(white, shared_6), is(shared_7));
        assertThat(Ur.calculateNewSquare(white, shared_7), is(shared_8));

        assertThat(Ur.calculateNewSquare(white, shared_8), is(white_run_off_1));
        assertThat(Ur.calculateNewSquare(white, white_run_off_1), is(white_run_off_2));
        assertThat(Ur.calculateNewSquare(white, white_run_off_2), is(off_board_finished));

        assertThat(Ur.calculateNewSquare(black, shared_8), is(black_run_off_1));
        assertThat(Ur.calculateNewSquare(black, black_run_off_1), is(black_run_off_2));
        assertThat(Ur.calculateNewSquare(black, black_run_off_2), is(off_board_finished));
    }

}
