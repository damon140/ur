package com.damon140.ur;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.damon140.ur.Square.*;
import static com.damon140.ur.Team.black;
import static com.damon140.ur.Team.white;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BoardTest {

    @Test
    public void constructor_givenString_thenBoard() {
        Counters b = new Counters("""
                wwwwww |
                ...w  ..
                ........
                ....  ..
                bbbbbbb|""");

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


}
