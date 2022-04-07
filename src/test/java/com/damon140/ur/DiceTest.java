package com.damon140.ur;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.IntSummaryStatistics;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DiceTest {

    @Test
    public void dice()  {
        Board.Dice d;
        try {
            d = new Board.Dice();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }

        IntSummaryStatistics rolls = IntStream.range(0, 1000)
                .map(i -> d.roll())
                .summaryStatistics();

        assertThat(rolls.getMin(), is(0));
        assertThat(rolls.getMax(), is(4));
    }

}
