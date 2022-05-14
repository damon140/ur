package com.damon140.ur;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

import static com.damon140.ur.Square.*;
import static com.damon140.ur.Team.black;
import static com.damon140.ur.Team.white;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class HorizontalDrawnBoardTest {

    @Test
    public void parse_only5WhiteCounters_shouldThrow() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                HorizontalDrawnBoard.parsePlayAreaFromHorizontal("""
                |wwwww
                *...  w.
                ...*....
                *...  *.
                |bbbbbbb""")
        );
    }

    @Test
    public void parse_missingCounterDivider_shouldThrow() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                HorizontalDrawnBoard.parsePlayAreaFromHorizontal("""
                wwwwww
                *...  w.
                ...*....
                *...  b.
                bbbbbb""")
        );
    }

    @Test
    public void roundTrip_whiteAllOnBoard() throws NoSuchAlgorithmException {
        PlayArea playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal("""
                |
                .w.w  w.
                w.w.w.w.
                *...  *.
                bbbbbbb|""");
        thenFullBoardAreaIs(playArea, """
                       |
                *w.w  w.
                w.w*w.w.
                *...  *.
                bbbbbbb|""");
    }

    @Test
    public void roundTrip1_noneFinished() throws NoSuchAlgorithmException {
        PlayArea playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal("""
                wwwwww |
                ...w  ..
                ........
                ...b  ..
                bbbbbb |""");
        thenFullBoardAreaIs(playArea, """
                wwwwww |
                *..w  *.
                ...*....
                *..b  *.
                bbbbbb |""");
    }

    @Test
    public void roundTrip1_mixed2() throws NoSuchAlgorithmException {
        PlayArea playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal("""
                wwww |ww
                ...w  ..
                ........
                ..b.  ..
                bbbbbb |""");
        thenFullBoardAreaIs(playArea, """
                wwww |ww
                *..w  *.
                ...*....
                *.b.  *.
                bbbbbb |""");
    }

    @Test
    public void roundTrip3_mixed2() throws NoSuchAlgorithmException {
        PlayArea playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal("""
                wwww |ww
                ...w  ..
                ........
                ..b.  ..
                bbbbb|b""");
        thenFullBoardAreaIs(playArea, """
                wwww |ww
                *..w  *.
                ...*....
                *.b.  *.
                bbbbb |b""");
    }

    @Test
    public void roundTrip5_allFinished() throws NoSuchAlgorithmException {
        PlayArea playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal("""
                |wwwwww 
                ....  w.
                ........
                ....  b.
                |bbbbbb """);
        thenFullBoardAreaIs(playArea, """
                 |wwwwww
                *...  w.
                ...*....
                *...  b.
                 |bbbbbb""");
    }

    @Test
    public void roundTrip_withStars_thenParsed() throws NoSuchAlgorithmException {
        PlayArea playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal("""
                wwwww|w 
                *..w  *.
                ...*....
                *..b  *.
                bbbbb|b """);
        thenFullBoardAreaIs(playArea, """
                wwwww |w
                *..w  *.
                ...*....
                *..b  *.
                bbbbb |b""");
    }

    private void thenFullBoardAreaIs(PlayArea playArea, String correct) {
        List<String> strings = new HorizontalDrawnBoard(playArea).fullBoard();
        String string = strings.stream().collect(Collectors.joining("\n"));

        assertThat(string, is(correct));
    }

    @Test
    public void constructor_givenString_thenBoard() throws NoSuchAlgorithmException {

        PlayArea playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal("""
                wwww |ww
                ...w  ..
                ........
                ..b.  ..
                bbbbb|b""");

        assertThat(playArea.completedCount(white), is(2));
        assertThat(playArea.completedCount(black), is(1));

        assertThat(playArea.get(white_run_on_1), is(white));
        assertThat(playArea.get(black_run_on_2), is(black));

        assertThat(playArea.unstartedCount(white), is(4));
        assertThat(playArea.unstartedCount(black), is(5));
    }

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