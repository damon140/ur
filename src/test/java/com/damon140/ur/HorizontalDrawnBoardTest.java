package com.damon140.ur;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

import static com.damon140.ur.Square.*;
import static com.damon140.ur.Team.black;
import static com.damon140.ur.Team.white;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class HorizontalDrawnBoardTest {

    @Test
    public void horizontalFullBoardStrings_givenCompleted_thenCorrectPadding() throws NoSuchAlgorithmException {
        PlayArea b = new PlayArea();
        b.move(off_board_unstarted, white_run_on_1, white);
        b.move(off_board_unstarted, black_run_on_1, black);
        b.move(off_board_unstarted, off_board_finished, white);
        b.move(off_board_unstarted, off_board_finished, black);

        HorizontalDrawnBoard d = new HorizontalDrawnBoard(b);
        String string = d.fullBoard().stream().collect(Collectors.joining("\n"));
        assertThat(string, is("""
                                wwwww |w
                                *..w  *.
                                ...*....
                                *..b  *.
                                bbbbb |b"""));
    }

}