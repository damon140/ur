package com.damon140.ur;

import org.junit.jupiter.api.Test;

import static com.damon140.ur.Team.black;
import static com.damon140.ur.Team.white;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TeamTest {

    @Test
    public void other() {
        assertThat(black.other(), is(white));
        assertThat(white.other(), is(black));
    }

}
