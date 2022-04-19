package com.damon140.ur;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static com.damon140.ur.Square.*;
import static com.damon140.ur.Team.black;
import static com.damon140.ur.Team.white;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BoardTest {

    // FIXME: Damon more tests here??  Not much tested??

    @Test
    public void constructor_givenString_thenBoard() throws NoSuchAlgorithmException {

        Counters counters = DrawnBoard.parseCounters("""
                wwww |ww
                ...w  ..
                ........
                ..b.  ..
                bbbbb|b""");

        assertThat(counters.completedCount(white), is(2));
        assertThat(counters.completedCount(black), is(1));

        assertThat(counters.get(white_run_on_1), is(white));
        assertThat(counters.get(black_run_on_2), is(black));

        assertThat(counters.unstartedCount(white), is(4));
        assertThat(counters.unstartedCount(black), is(5));
    }

}
