package com.damon140.ur;

import org.junit.jupiter.api.Test;

import static com.damon140.ur.Board.Team.black;
import static com.damon140.ur.Board.Team.white;
import static com.damon140.ur.UrTextPrinter.blank;
import static com.damon140.ur.UrTextPrinter.square;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class UrTextPrinterTest {

// TODO
//    @Test
//    public void printBoard() {
//        assertThat(UrTextPrinter.board(ur), is("""
//                """));
//    }

    @Test
    public void join() {
        assertThat(UrTextPrinter.join(square(white), blank(), square(black)),is("""
                |---|   |---|
                | w |   | b |
                |---|   |---|"""));
    }

    @Test
    public void printBlankSquare() {
        assertThat(blank(), is("" +
                "   \n" +
                "   \n" +
                "   \n"));
    }

    @Test
    public void printEmptySquare() {
        assertThat(square(), is("""
                |---|
                |   |
                |---|"""));
    }

    @Test
    public void printWhiteSquare() {
        assertThat(square(white), is("""
                |---|
                | w |
                |---|"""));
    }

    @Test
    public void printBlackSquare() {
        assertThat(square(black), is("""
                |---|
                | b |
                |---|"""));
    }


}