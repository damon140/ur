package ur

import com.damon140.ur.*
import com.damon140.ur.Team.black
import com.damon140.ur.Team.white
import com.damon140.ur.Ur.MoveResult
import kotlin.test.Test
import kotlin.test.assertEquals

class UrTest {

    private var moveResult: ArrayDeque<MoveResult> = ArrayDeque<MoveResult>()
    private var lastAskMoves: Map<Square, Square> = HashMap<Square, Square>()
    private var playArea: PlayArea? = null
    private var horizontalDrawnBoard: HorizontalDrawnBoard? = null
    private var ur: Ur? = null

    @Test
    fun initialStateTest() {
        givenNewGame()
        thenStateIsInitial()
        thenAllMovesWereLegal()
    }

    @Test
    fun whiteFirstMove() {
        givenNewGame()
        whenAskMoves(white, 1)
        thenMovesAre(Square.white_run_on_1)
    }

    // FIXME: Damon copy to java
    @Test
    fun whiteFirstMoveWhenRolledZero() {
        givenNewGame()
        whenAskMoves(white, 0)
        thenThereArentAnyMoves()
    }

    @Test
    fun whiteRolls1() {
        givenNewGame()
        whenMove(white, Square.off_board_unstarted, 1)
        thenWhiteHasCounterAt(Square.white_run_on_1)
        thenMoveWasLegal()
        thenSmallBoardIs(
            ""
                    + "*..w  *.\n"
                    + "...*....\n"
                    + "*...  *."
        )
        thenHorizontalFullBoardIs(
            ""
                    + "wwwwww |\n"
                    + "*..w  *.\n"
                    + "...*....\n"
                    + "*...  *.\n"
                    + "bbbbbbb|"
        )
        thenItsBlacksMove()
    }

    @Test
    fun whiteAndBlackRoll1() {
        givenNewGame()
        whenMove(white, Square.off_board_unstarted, 1)
        thenMoveWasLegal()
        whenMove(black, Square.off_board_unstarted, 1)
        thenMoveWasLegal()
        thenWhiteHasCounterAt(Square.white_run_on_1)
        thenBlackHasCounterAt(Square.black_run_on_1)
        thenMoveWasLegal()
        /*

         */
        thenHorizontalFullBoardIs(
            ""
                    + "wwwwww |\n"
                    + "*..w  *.\n"
                    + "...*....\n"
                    + "*..b  *.\n"
                    + "bbbbbb |")
        thenItsWhitesMove()
    }

    @Test
    fun illegalMoveOntoOwnCounter() {
        givenNewGame()
        whenMove(white, Square.off_board_unstarted, 1)
        whenMove(black, Square.off_board_unstarted, 1)
        thenAllMovesWereLegal()
        whenMove(white, Square.off_board_unstarted, 1)
        thenMoveWasIllegal()
        thenItsWhitesMove()
    }

    @Test
    fun move4ontoFlowerAndStillWhiteMove() {
        givenNewGame()
        whenMove(white, Square.off_board_unstarted, 4)
        thenMoveWasLegal()
        thenItsWhitesMove()
        thenHorizontalFullBoardIs(""
                    + "wwwwww |\n"
                    + "w...  *.\n"
                    + "...*....\n"
                    + "*...  *.\n"
                    + "bbbbbbb|")
    }

    @Test
    fun blackTakesWhite() {
        givenNewGame()
        whenMove(white, Square.off_board_unstarted, 3)
        printBoard()
        whenMove(black, Square.off_board_unstarted, 3)
        printBoard()
        whenMove(white, Square.white_run_on_3, 2)
        printBoard()
        whenMove(black, Square.black_run_on_3, 2)
        printBoard()
        thenItsWhitesMove()
        thenHorizontalFullBoardIs("""
              wwwwwww|
              *...  *.
              b..*....
              *...  *.
              bbbbbb |""".trimIndent());
        thenAllMovesWereLegal()
    }

    @Test
    fun blackTakesWhiteUsingDoubles() {
        givenNewGame()
        whenMove(white, Square.off_board_unstarted, 4)
        whenMove(white, Square.white_run_on_4, 1)
        thenItsBlacksMove()
        whenMove(black, Square.off_board_unstarted, 4)
        whenMove(black, Square.black_run_on_4, 1)
        thenItsWhitesMove()
        thenHorizontalFullBoardIs("""
              wwwwwww|
              *...  *.
              b..*....
              *...  *.
              bbbbbb |""".trimIndent());
    }

    @Test
    fun whiteGetSafe() {
        givenNewGame()
        whenMove(white, Square.off_board_unstarted, 4)
        whenMove(white, Square.white_run_on_4, 4)
        whenMove(white, Square.shared_4, 4)
        whenMove(black, Square.off_board_unstarted, 4)
        whenMove(black, Square.black_run_on_4, 4)
        whenMove(black, Square.shared_4, 3)
        whenMove(white, Square.shared_8, 3)
        thenWhiteCompletedCountIs(1)
        thenItsBlacksMove()
        thenHorizontalFullBoardIs("""
              wwwwww|w
              *...  *.
              ...*..b.
              *...  *.
              bbbbbb |""".trimIndent());
        thenAllMovesWereLegal()
    }

    @Test
    fun bothTeamsGetSafe() {
        givenNewGame()
        whenMove(white, Square.off_board_unstarted, 4)
        whenMove(white, Square.white_run_on_4, 4)
        whenMove(white, Square.shared_4, 4)
        printBoard()
        whenMove(black, Square.off_board_unstarted, 4)
        whenMove(black, Square.black_run_on_4, 4)
        whenMove(black, Square.shared_4, 3)
        printBoard()
        whenMove(white, Square.shared_8, 3)
        printBoard()
        whenMove(black, Square.shared_7, 4)
        printBoard()
        thenItsWhitesMove()
        thenHorizontalFullBoardIs("""
              wwwwww|w
              *...  *.
              ...*....
              *...  *.
              bbbbbb|b""".trimIndent());

        thenAllMovesWereLegal()
    }

    @Test
    fun offBoardMove() {
        givenGame("""
            |wwwwww 
            *...  w.
            ...*....
            *...  *.
            |bbbbbbb""".trimIndent());

        thenHorizontalFullBoardIs("""
            |wwwwww
            *...  w.
            ...*....
            *...  *.
            |bbbbbbb""".trimIndent());

        whenAskMoves(white, 1)
        thenMovesAre(Square.off_board_finished)
    }

    @Test
    fun askMoves_givenNoMoreCountes_thenNoOffboardSuggested() {
        givenGame("""
                    |w
              ww.w  *.
              wwb*wb..
              b..b  *.
              b    |bb
              """.trimIndent());
        whenAskMoves(white, 2);
        thenMovesAre(Square.shared_3, Square.shared_4, Square.shared_7);
    }

    @Test
    fun noMovesForRoll() {
        givenGame("""
              |wwwwww
              *...  w.
              ...*....
              *...  *.
              |bbbbbbb""".trimIndent());
        whenAskMoves(white, 2);
        thenNoMovesAreLegal();
    }

    @Test
    fun manyMovesForWhite() {
        givenGame("""
              |
              .w.w  w.
              w.w.w.w.
              *...  *.
              bbbbbbb|""".trimIndent());
        whenAskMoves(white, 1)
        thenMovesAre(
            Square.white_run_on_2,
            Square.white_run_on_4,
            Square.shared_2,
            Square.shared_4,
            Square.shared_6,
            Square.shared_8,
            Square.off_board_finished
        )
    }

    @Test
    fun whiteWon() {
        givenGame("""
              |wwwwww 
              *...  w.
              ...*....
              *...  *.
              bbbbbbb|""".trimIndent());
        whenMove(white, Square.white_run_off_2, 1)
        thenWhiteWon()
    }

    // --------------------------------------
    // Given section
    // --------------------------------------
    private fun givenNewGame() {
        moveResult = ArrayDeque<MoveResult>()
        playArea = PlayArea()
        horizontalDrawnBoard = HorizontalDrawnBoard(playArea!!)
        ur = Ur(playArea!!)
    }

    private fun givenGame(game: String) {
        moveResult = ArrayDeque<MoveResult>()
        playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal(game)
        horizontalDrawnBoard = HorizontalDrawnBoard(playArea!!)
        ur = Ur(playArea!!)
    }

    // --------------------------------------
    // When section
    // --------------------------------------
    private fun whenMove(team: Team, square: Square, i: Int) {
        val result = ur!!.moveCounter(team, square, i)
        moveResult.add(result)
    }

    private fun whenAskMoves(team: Team, roll: Int) {
        lastAskMoves = ur!!.askMoves(team, roll)
    }

    // --------------------------------------
    // Then section
    // --------------------------------------
    private fun thenNoMovesAreLegal() {
        assertEquals(lastAskMoves, mapOf())
    }

    private fun thenMoveWasIllegal() {
        assertEquals(moveResult.last(), MoveResult.illegal)
    }

    private fun thenMoveWasLegal() {
        assertEquals(moveResult.last(), MoveResult.legal)
    }

    fun thenStateIsInitial() {
        assertEquals(playArea!!.currentTeam(), white)
        assertEquals(playArea!!.inPlayCount(), 0)
        assertEquals(playArea!!.completedCount(white), 0)
        assertEquals(playArea!!.completedCount(black), 0)
    }

    private fun thenWhiteHasCounterAt(square: Square) {
        assertEquals(playArea!![square], white)
    }

    private fun thenBlackHasCounterAt(square: Square) {
        assertEquals(playArea!![square], black)
    }

    private fun thenSmallBoardIs(s: String) {
        val smallBoard: String = horizontalDrawnBoard!!.smallBoard()
            .joinToString("\n")
        assertEquals(s, smallBoard)
    }

    private fun thenHorizontalFullBoardIs(wantedBoard: String) {
        val board: String = horizontalDrawnBoard!!.fullBoard()
            .map { obj: String -> obj.trim { it <= ' ' } }
            .joinToString("\n")
        assertEquals(board, wantedBoard)
    }

    private fun thenItsWhitesMove() {
        assertEquals(ur!!.currentTeam(), white)
    }

    private fun thenItsBlacksMove() {
        assertEquals(ur!!.currentTeam(), black)
    }

    private fun thenAllMovesWereLegal() {
        for (i in moveResult.indices) {
            val r: MoveResult = moveResult.first()
            if (r === MoveResult.illegal) {
                assertEquals(false, true, "Move [" + (i + 1) + "] was illegal")
            }
        }
    }

    private fun thenWhiteCompletedCountIs(count: Int) {
        assertEquals(playArea!!.completedCount(white), count)
    }

    private fun thenMovesAre(vararg destinationSquares: Square) {
        val sortedInput: List<Square> = destinationSquares
            .sorted()
            .toList()
        val lastAskMovesDestinatinons: List<Square> = lastAskMoves
            .values
            .sorted()
            .toList()
        assertEquals(sortedInput, lastAskMovesDestinatinons)
    }

    private fun thenThereArentAnyMoves() {
        assertEquals(0, lastAskMoves.size)
    }

    private fun thenWhiteWon() {
        assertEquals(moveResult.last(), MoveResult.gameOver)
    }

    // TODO: add to all test failings
    fun printBoard() {
//        horizontalDrawnBoard!!.fullBoard()
//            .map { obj: String -> obj.trim { it <= ' ' } }
//            .forEach(java.lang.System.out::println)
    }
}