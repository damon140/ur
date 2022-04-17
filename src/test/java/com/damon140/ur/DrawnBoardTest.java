package com.damon140.ur;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.stream.Collectors;

import static com.damon140.ur.CounterPositions.Square.black_run_on_1;
import static com.damon140.ur.CounterPositions.Square.white_run_on_1;
import static com.damon140.ur.Team.black;
import static com.damon140.ur.Team.white;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DrawnBoardTest {


    @Test
    public void horizontalFullBoardStrings_givenCompleted_thenCorrectPadding() throws NoSuchAlgorithmException {
        CounterPositions b = new CounterPositions();
        b.getCounters().put(white_run_on_1, white);
        b.getCounters().put(black_run_on_1, black);
        b.getCompletedCounters().put(white, 1);
        b.getCompletedCounters().put(black, 1);

        DrawnBoard d = new DrawnBoard(b);
        String string = d.horizontalFullBoardStrings().stream().collect(Collectors.joining("\n"));
        assertThat(string, is("""
                                wwwww |w
                                *..w  *.
                                ...*....
                                *..b  *.
                                bbbbb |b"""));
    }


}