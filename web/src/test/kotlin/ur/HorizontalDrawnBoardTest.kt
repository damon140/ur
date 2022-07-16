package com.damon140.ur

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class HorizontalDrawnBoardTest {

    @Test
    fun parse_only5WhiteCounters_shouldThrow() {
        assertFailsWith<Exception> {
            HorizontalDrawnBoard.parsePlayAreaFromHorizontal(
                """
                |wwwww
                *...  w.
                ...*....
                *...  *.
                |bbbbbbb""".trimIndent()
            )
        }
    }

    @Test
    fun parse_missingCounterDivider_shouldThrow() {
        assertFailsWith<Exception> {
            HorizontalDrawnBoard.parsePlayAreaFromHorizontal(
                """
                wwwwww
                *...  w.
                ...*....
                *...  b.
                bbbbbb""".trimIndent()
            )
        }
    }

    @Test
    fun roundTrip_whiteAllOnBoard() {
        val  playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal("""
                |
                .w.w  w.
                w.w.w.w.
                *...  *.
                bbbbbbb|""".trimIndent());
        thenFullBoardAreaIs(playArea, """
                       |
                *w.w  w.
                w.w*w.w.
                *...  *.
                bbbbbbb|""".trimIndent());
    }

    @Test
    fun roundTrip1_noneFinished() {
        val playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal("""
                wwwwww |
                ...w  ..
                ........
                ...b  ..
                bbbbbb |""".trimIndent());
        thenFullBoardAreaIs(playArea, """
                wwwwww |
                *..w  *.
                ...*....
                *..b  *.
                bbbbbb |""".trimIndent())
    }

    @Test
    fun roundTrip1_mixed2() {
        val playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal("""
                wwww |ww
                ...w  ..
                ........
                ..b.  ..
                bbbbbb |""".trimIndent());
        thenFullBoardAreaIs(playArea, """
                wwww |ww
                *..w  *.
                ...*....
                *.b.  *.
                bbbbbb |""".trimIndent())
    }

    @Test
    fun roundTrip3_mixed2() {
        val playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal("""
                wwww |ww
                ...w  ..
                ........
                ..b.  ..
                bbbbb|b""".trimIndent());
        thenFullBoardAreaIs(playArea, """
                wwww |ww
                *..w  *.
                ...*....
                *.b.  *.
                bbbbb |b""".trimIndent())
    }

    @Test
    fun roundTrip5_allFinished() {
        val playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal("""
                |wwwwww 
                ....  w.
                ........
                ....  b.
                |bbbbbb """.trimIndent());
        thenFullBoardAreaIs(playArea, """
                 |wwwwww
                *...  w.
                ...*....
                *...  b.
                 |bbbbbb""".trimIndent())
    }

    @Test
    fun roundTrip_withStars_thenParsed() {
        val playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal("""
                wwwww|w 
                *..w  *.
                ...*....
                *..b  *.
                bbbbb|b """.trimIndent());
        thenFullBoardAreaIs(playArea, """
                wwwww |w
                *..w  *.
                ...*....
                *..b  *.
                bbbbb |b""".trimIndent())
    }

    private fun thenFullBoardAreaIs(playArea: PlayArea, correct: String) {
        val strings: List<String> = HorizontalDrawnBoard(playArea).fullBoard()
        val string = strings.joinToString("\n")

        assertEquals(correct, string)
    }

    @Test
    fun constructor_givenString_thenBoard() {
        val playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal("""
                wwww |ww
                ...w  ..
                ........
                ..b.  ..
                bbbbb|b """.trimIndent());
        assertEquals(playArea.completedCount(Team.white), 2)
        assertEquals(playArea.completedCount(Team.black), 1)
        assertEquals(playArea[Square.white_run_on_1], Team.white)
        assertEquals(playArea[Square.black_run_on_2], Team.black)
        assertEquals(playArea.unstartedCount(Team.white), 4)
        assertEquals(playArea.unstartedCount(Team.black), 5)
    }

    @Test
    fun horizontalFullBoardStrings_givenCompleted_thenCorrectPadding() {
        val b = PlayArea()
        b.move(Square.off_board_unstarted, Square.white_run_on_1, Team.white)
        b.move(Square.off_board_unstarted, Square.black_run_on_1, Team.black)
        b.move(Square.off_board_unstarted, Square.off_board_finished, Team.white)
        b.move(Square.off_board_unstarted, Square.off_board_finished, Team.black)
        val d = HorizontalDrawnBoard(b)
        val string: String = d.fullBoard().joinToString("\n")
        assertEquals(string, """
            wwwww |w
            *..w  *.
            ...*....
            *..b  *.
            bbbbb |b""".trimIndent())
    }

}