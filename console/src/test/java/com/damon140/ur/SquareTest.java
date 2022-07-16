package com.damon140.ur;

import org.junit.jupiter.api.Test;

import static com.damon140.ur.Square.*;
import static com.damon140.ur.Team.black;
import static com.damon140.ur.Team.white;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class SquareTest {


    @Test
    public void calculateNewSquareWithRoll() {
        assertThat(off_board_unstarted.calculateNewSquare(black, 2).get(), is(black_run_on_2));
        assertThat(off_board_unstarted.calculateNewSquare(white, 2).get(), is(white_run_on_2));

        assertThat(black_run_on_1.calculateNewSquare(black, 1).get(), is(black_run_on_2));
        assertThat(black_run_on_1.calculateNewSquare(black, 3).get(), is(black_run_on_4));
        assertThat(black_run_on_1.calculateNewSquare(black, 4).get(), is(shared_1));

        assertThat(shared_1.calculateNewSquare(black, 7).get(), is(shared_8));

        assertThat(shared_8.calculateNewSquare(black, 1).get(), is(black_run_off_1));
        assertThat(shared_8.calculateNewSquare(white, 1).get(), is(white_run_off_1));

        assertThat(black_run_off_1.calculateNewSquare(black, 2).get(), is(off_board_finished));
        assertThat(white_run_off_1.calculateNewSquare(white, 2).get(), is(off_board_finished));
    }


    @Test
    public void calculateNewSquareNoArgs() {
        assertThat(off_board_unstarted.calculateNewSquare(black), is(black_run_on_1));
        assertThat(off_board_unstarted.calculateNewSquare(white), is(white_run_on_1));

        // FIXME: these should be illegal
        assertThat(black_run_on_1.calculateNewSquare(white), is(black_run_on_2));
        assertThat(black_run_on_2.calculateNewSquare(white), is(black_run_on_3));
        assertThat(black_run_on_3.calculateNewSquare(white), is(black_run_on_4));

        assertThat(black_run_on_1.calculateNewSquare(black), is(black_run_on_2));
        assertThat(black_run_on_2.calculateNewSquare(black), is(black_run_on_3));
        assertThat(black_run_on_3.calculateNewSquare(black), is(black_run_on_4));

        assertThat(black_run_on_4.calculateNewSquare(white), is(shared_1));
        assertThat(shared_1.calculateNewSquare(white), is(shared_2));
        assertThat(shared_2.calculateNewSquare(white), is(shared_3));
        assertThat(shared_3.calculateNewSquare(white), is(shared_4));
        assertThat(shared_4.calculateNewSquare(white), is(shared_5));
        assertThat(shared_5.calculateNewSquare(white), is(shared_6));
        assertThat(shared_6.calculateNewSquare(white), is(shared_7));
        assertThat(shared_7.calculateNewSquare(white), is(shared_8));

        assertThat(shared_8.calculateNewSquare(white), is(white_run_off_1));
        assertThat(white_run_off_1.calculateNewSquare(white), is(white_run_off_2));
        assertThat(white_run_off_2.calculateNewSquare(white), is(off_board_finished));

        assertThat(shared_8.calculateNewSquare(black), is(black_run_off_1));
        assertThat(black_run_off_1.calculateNewSquare(black), is(black_run_off_2));
        assertThat(black_run_off_2.calculateNewSquare(black), is(off_board_finished));
    }



}