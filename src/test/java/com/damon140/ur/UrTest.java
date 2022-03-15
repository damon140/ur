package com.damon140.ur;


import static com.damon140.ur.Ur.Square.*;
import static com.damon140.ur.Ur.Team.white;
import static com.damon140.ur.Ur.Team.black;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UrTest {

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
