package com.damon140.ur;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.damon140.ur.Board.Square.*;
import static com.damon140.ur.Board.Team.black;
import static com.damon140.ur.Board.Team.white;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BoardTest {

    @Test
    public void constructor_givenString_thenBoard() {
        Board b = new Board("""
                wwwwww  
                ...w  ..
                ........
                ....  ..
                bbbbbbb """);

        assertThat(b.getCompletedCounters().size(), is(2));
        assertThat(b.getCompletedCounters().get(white), is(0));
        assertThat(b.getCompletedCounters().get(black), is(0));

        assertThat(b.getCounters(), is(Map.of(white_run_on_1, white)));

        assertThat(b.unstartedCount(white), is(6));
        assertThat(b.unstartedCount(black), is(7));
    }

    @Test
    public void other() {
        assertThat(black.other(), is(white));
        assertThat(white.other(), is(black));
    }

    @Test
    public void calculateNewSquareNoArgs() {
        assertThat(Board.calculateNewSquare(black, off_board_unstarted, 2), is(black_run_on_2));
        assertThat(Board.calculateNewSquare(white, off_board_unstarted, 2), is(white_run_on_2));

        assertThat(Board.calculateNewSquare(black, black_run_on_1, 1), is(black_run_on_2));
        assertThat(Board.calculateNewSquare(black, black_run_on_1, 3), is(black_run_on_4));
        assertThat(Board.calculateNewSquare(black, black_run_on_1, 4), is(shared_1));

        assertThat(Board.calculateNewSquare(black, shared_1, 7), is(shared_8));

        assertThat(Board.calculateNewSquare(black, shared_8, 1), is(black_run_off_1));
        assertThat(Board.calculateNewSquare(white, shared_8, 1), is(white_run_off_1));

        assertThat(Board.calculateNewSquare(black, black_run_off_1, 2), is(off_board_finished));
        assertThat(Board.calculateNewSquare(white, white_run_off_1, 2), is(off_board_finished));
    }

    @Test
    public void calculateNewSquare() {
        assertThat(Board.calculateNewSquare(black, off_board_unstarted), is(black_run_on_1));
        assertThat(Board.calculateNewSquare(white, off_board_unstarted), is(white_run_on_1));

        // FIXME: these should be illegal
        assertThat(Board.calculateNewSquare(white, black_run_on_1), is(black_run_on_2));
        assertThat(Board.calculateNewSquare(white, black_run_on_2), is(black_run_on_3));
        assertThat(Board.calculateNewSquare(white, black_run_on_3), is(black_run_on_4));

        assertThat(Board.calculateNewSquare(black, black_run_on_1), is(black_run_on_2));
        assertThat(Board.calculateNewSquare(black, black_run_on_2), is(black_run_on_3));
        assertThat(Board.calculateNewSquare(black, black_run_on_3), is(black_run_on_4));

        assertThat(Board.calculateNewSquare(white, black_run_on_4), is(shared_1));
        assertThat(Board.calculateNewSquare(white, shared_1), is(shared_2));
        assertThat(Board.calculateNewSquare(white, shared_2), is(shared_3));
        assertThat(Board.calculateNewSquare(white, shared_3), is(shared_4));
        assertThat(Board.calculateNewSquare(white, shared_4), is(shared_5));
        assertThat(Board.calculateNewSquare(white, shared_5), is(shared_6));
        assertThat(Board.calculateNewSquare(white, shared_6), is(shared_7));
        assertThat(Board.calculateNewSquare(white, shared_7), is(shared_8));

        assertThat(Board.calculateNewSquare(white, shared_8), is(white_run_off_1));
        assertThat(Board.calculateNewSquare(white, white_run_off_1), is(white_run_off_2));
        assertThat(Board.calculateNewSquare(white, white_run_off_2), is(off_board_finished));

        assertThat(Board.calculateNewSquare(black, shared_8), is(black_run_off_1));
        assertThat(Board.calculateNewSquare(black, black_run_off_1), is(black_run_off_2));
        assertThat(Board.calculateNewSquare(black, black_run_off_2), is(off_board_finished));
    }

}
