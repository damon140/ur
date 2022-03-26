package com.damon140.ur;

import com.damon140.ur.Ur.Dice;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.IntSummaryStatistics;
import java.util.Set;
import java.util.stream.IntStream;

import static com.damon140.ur.Ur.Square.*;
import static com.damon140.ur.Ur.Square.off_board_unstarted;
import static com.damon140.ur.Ur.Team.black;
import static com.damon140.ur.Ur.Team.white;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UrTest {

    @Test
    public void initialStateTest() throws NoSuchAlgorithmException {
        givenNewGame();
        thenStateIsInitial();
    }

    @Test
    public void whiteRolls1() throws NoSuchAlgorithmException {
        givenNewGame();
        whenMove(white, off_board_unstarted, 1);
        thenWhiteHasCounterAt(white_run_on_1);
    }

    @Test
    public void whiteAndBlackRoll1() throws NoSuchAlgorithmException {
        givenNewGame();
    }

    // illegal move 1 on 1
    // 4 and double and still white's move
    // black takes white

    private Ur ur = null;

    // --------------------------------------
    // Given section
    // --------------------------------------

    public void givenNewGame() throws NoSuchAlgorithmException {
        ur = new Ur();
    }

    // --------------------------------------
    // When section
    // --------------------------------------

    private void whenMove(Ur.Team white, Ur.Square square, int i) {
        ur.moveCounter(new Ur.Move(white, square, i));
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
        assertThat(state.contains("completedCounters: {black=0, white=0}"), is(true));
    }

    private void thenWhiteHasCounterAt(Ur.Square square) {
        assertThat(ur.getCounters().get(square), is(white));
    }

    @Test
    public void dice() throws NoSuchAlgorithmException {
        Dice d = new Dice();

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
        assertThat(Ur.calculateNewSquare(black, off_board_unstarted, 2), is(top_run_on_2));
        assertThat(Ur.calculateNewSquare(white, off_board_unstarted, 2), is(bottom_run_on_2));

        assertThat(Ur.calculateNewSquare(black, top_run_on_1, 1), is(top_run_on_2));
        assertThat(Ur.calculateNewSquare(black, top_run_on_1, 3), is(top_run_on_4));
        assertThat(Ur.calculateNewSquare(black, top_run_on_1, 4), is(shared_1));

        assertThat(Ur.calculateNewSquare(black, shared_1, 7), is(shared_8));

        assertThat(Ur.calculateNewSquare(black, shared_8, 1), is(top_run_off_1));
        assertThat(Ur.calculateNewSquare(white, shared_8, 1), is(bottom_run_off_1));

        assertThat(Ur.calculateNewSquare(black, top_run_off_1, 2), is(off_board_finished));
        assertThat(Ur.calculateNewSquare(white, bottom_run_off_1, 2), is(off_board_finished));
    }

    @Test
    public void calculateNewSquare() {
        assertThat(Ur.calculateNewSquare(black, off_board_unstarted), is(top_run_on_1));
        assertThat(Ur.calculateNewSquare(white, off_board_unstarted), is(bottom_run_on_1));

        // FIXME: these should be illegal
        assertThat(Ur.calculateNewSquare(white, top_run_on_1), is(top_run_on_2));
        assertThat(Ur.calculateNewSquare(white, top_run_on_2), is(top_run_on_3));
        assertThat(Ur.calculateNewSquare(white, top_run_on_3), is(top_run_on_4));

        assertThat(Ur.calculateNewSquare(black, top_run_on_1), is(top_run_on_2));
        assertThat(Ur.calculateNewSquare(black, top_run_on_2), is(top_run_on_3));
        assertThat(Ur.calculateNewSquare(black, top_run_on_3), is(top_run_on_4));

        assertThat(Ur.calculateNewSquare(white, top_run_on_4), is(shared_1));
        assertThat(Ur.calculateNewSquare(white, shared_1), is(shared_2));
        assertThat(Ur.calculateNewSquare(white, shared_2), is(shared_3));
        assertThat(Ur.calculateNewSquare(white, shared_3), is(shared_4));
        assertThat(Ur.calculateNewSquare(white, shared_4), is(shared_5));
        assertThat(Ur.calculateNewSquare(white, shared_5), is(shared_6));
        assertThat(Ur.calculateNewSquare(white, shared_6), is(shared_7));
        assertThat(Ur.calculateNewSquare(white, shared_7), is(shared_8));

        assertThat(Ur.calculateNewSquare(white, shared_8), is(bottom_run_off_1));
        assertThat(Ur.calculateNewSquare(white, bottom_run_off_1), is(bottom_run_off_2));
        assertThat(Ur.calculateNewSquare(white, bottom_run_off_2), is(off_board_finished));

        assertThat(Ur.calculateNewSquare(black, shared_8), is(top_run_off_1));
        assertThat(Ur.calculateNewSquare(black, top_run_off_1), is(top_run_off_2));
        assertThat(Ur.calculateNewSquare(black, top_run_off_2), is(off_board_finished));
    }

}
