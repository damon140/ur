package ur

import com.damon140.ur.*
import com.damon140.ur.Team.Black
import com.damon140.ur.Team.White
import com.damon140.ur.Ur.MoveResult
import kotlin.test.Test
import kotlin.test.assertEquals

class UrTest {

    private var moveResult: ArrayDeque<MoveResult> = ArrayDeque<MoveResult>()
    private var lastAskMoves: Map<Square, Square> = HashMap<Square, Square>()
    private var playArea: PlayArea = PlayArea(White)
    private var horizontalDrawnBoard: HorizontalDrawnBoard = HorizontalDrawnBoard(playArea)
    private var ur: Ur = Ur(playArea)

    @Test
    fun initialStateTest() {
        givenNewGame()
        thenStateIsInitial()
        thenAllMovesWereLegal()
    }

    @Test
    fun whiteFirstMove() {
        givenNewGame()
        whenAskMoves(White, 1)
        thenMovesAre(Square.White_run_on_1)
    }

    // FIXME: Damon copy to java
    @Test
    fun whiteFirstMoveWhenRolledZero() {
        givenNewGame()
        whenAskMoves(White, 0)
        thenThereArentAnyMoves()
    }

    @Test
    fun whiteRolls1() {
        givenNewGame()
        whenMove(White, Square.Off_board_unstarted, 1)
        thenWhiteHasCounterAt(Square.White_run_on_1)
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
        whenMove(White, Square.Off_board_unstarted, 1)
        thenMoveWasLegal()
        whenMove(Black, Square.Off_board_unstarted, 1)
        thenMoveWasLegal()
        thenWhiteHasCounterAt(Square.White_run_on_1)
        thenBlackHasCounterAt(Square.Black_run_on_1)
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
        whenMove(White, Square.Off_board_unstarted, 1)
        whenMove(Black, Square.Off_board_unstarted, 1)
        thenAllMovesWereLegal()
        whenMove(White, Square.Off_board_unstarted, 1)
        thenMoveWasIllegal()
        thenItsWhitesMove()
    }

    @Test
    fun move4ontoFlowerAndStillWhiteMove() {
        givenNewGame()
        whenMove(White, Square.Off_board_unstarted, 4)
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
        whenMove(White, Square.Off_board_unstarted, 3)
        printBoard()
        whenMove(Black, Square.Off_board_unstarted, 3)
        printBoard()
        whenMove(White, Square.White_run_on_3, 2)
        printBoard()
        whenMove(Black, Square.Black_run_on_3, 2)
        printBoard()
        thenItsWhitesMove()
        thenHorizontalFullBoardIs("""
              wwwwwww|
              *...  *.
              b..*....
              *...  *.
              bbbbbb |""".trimIndent())
        thenAllMovesWereLegal()
    }

    @Test
    fun blackTakesWhiteUsingDoubles() {
        givenNewGame()
        whenMove(White, Square.Off_board_unstarted, 4)
        whenMove(White, Square.White_run_on_4, 1)
        thenItsBlacksMove()
        whenMove(Black, Square.Off_board_unstarted, 4)
        whenMove(Black, Square.Black_run_on_4, 1)
        thenItsWhitesMove()
        thenHorizontalFullBoardIs("""
              wwwwwww|
              *...  *.
              b..*....
              *...  *.
              bbbbbb |""".trimIndent())
    }

    @Test
    fun whiteGetSafe() {
        givenNewGame()
        whenMove(White, Square.Off_board_unstarted, 4)
        whenMove(White, Square.White_run_on_4, 4)
        whenMove(White, Square.Shared_4, 4)
        whenMove(Black, Square.Off_board_unstarted, 4)
        whenMove(Black, Square.Black_run_on_4, 4)
        whenMove(Black, Square.Shared_4, 3)
        whenMove(White, Square.Shared_8, 3)
        thenWhiteCompletedCountIs(1)
        thenItsBlacksMove()
        thenHorizontalFullBoardIs("""
              wwwwww|w
              *...  *.
              ...*..b.
              *...  *.
              bbbbbb |""".trimIndent())
        thenAllMovesWereLegal()
    }

    @Test
    fun bothTeamsGetSafe() {
        givenNewGame()
        whenMove(White, Square.Off_board_unstarted, 4)
        whenMove(White, Square.White_run_on_4, 4)
        whenMove(White, Square.Shared_4, 4)
        printBoard()
        whenMove(Black, Square.Off_board_unstarted, 4)
        whenMove(Black, Square.Black_run_on_4, 4)
        whenMove(Black, Square.Shared_4, 3)
        printBoard()
        whenMove(White, Square.Shared_8, 3)
        printBoard()
        whenMove(Black, Square.Shared_7, 4)
        printBoard()
        thenItsWhitesMove()
        thenHorizontalFullBoardIs("""
              wwwwww|w
              *...  *.
              ...*....
              *...  *.
              bbbbbb|b""".trimIndent())

        thenAllMovesWereLegal()
    }

    @Test
    fun offBoardMove() {
        givenGame("""
            |wwwwww 
            *...  w.
            ...*....
            *...  *.
            |bbbbbbb""".trimIndent())

        thenHorizontalFullBoardIs("""
            |wwwwww
            *...  w.
            ...*....
            *...  *.
            |bbbbbbb""".trimIndent())

        whenAskMoves(White, 1)
        thenMovesAre(Square.Off_board_finished)
    }

    @Test
    fun askMoves_givenNoMoreCountes_thenNoOffboardSuggested() {
        givenGame("""
                    |w
              ww.w  *.
              wwb*wb..
              b..b  *.
              b    |bb
              """.trimIndent())
        whenAskMoves(White, 2)
        thenMovesAre(Square.Shared_3, Square.Shared_4, Square.Shared_7)
    }

    @Test
    fun noMovesForRoll() {
        givenGame("""
              |wwwwww
              *...  w.
              ...*....
              *...  *.
              |bbbbbbb""".trimIndent())
        whenAskMoves(White, 2)
        thenNoMovesAreLegal()
    }

    @Test
    fun manyMovesForWhite() {
        givenGame("""
              |
              .w.w  w.
              w.w.w.w.
              *...  *.
              bbbbbbb|""".trimIndent())
        whenAskMoves(White, 1)
        thenMovesAre(
            Square.White_run_on_2,
            Square.White_run_on_4,
            Square.Shared_2,
            Square.Shared_4,
            Square.Shared_6,
            Square.Shared_8,
            Square.Off_board_finished
        )
    }

    @Test
    fun whiteWon() {
        givenGame("""
              |wwwwww 
              *...  w.
              ...*....
              *...  *.
              bbbbbbb|""".trimIndent())
        whenMove(White, Square.White_run_off_2, 1)
        thenWhiteWon()
    }

    // --------------------------------------
    // Given section
    // --------------------------------------
    private fun givenNewGame() {
        moveResult = ArrayDeque<MoveResult>()
        playArea = PlayArea(White)
        horizontalDrawnBoard = HorizontalDrawnBoard(playArea)
        ur = Ur(playArea)
    }

    private fun givenGame(game: String) {
        moveResult = ArrayDeque<MoveResult>()
        playArea = HorizontalDrawnBoard.parsePlayAreaFromHorizontal(game)
        horizontalDrawnBoard = HorizontalDrawnBoard(playArea)
        ur = Ur(playArea)
    }

    // --------------------------------------
    // When section
    // --------------------------------------
    private fun whenMove(team: Team, square: Square, i: Int) {
        val result = ur.moveCounter(team, square, i)
        moveResult.add(result)
    }

    private fun whenAskMoves(team: Team, roll: Int) {
        lastAskMoves = ur.askMoves(team, roll)
    }

    // --------------------------------------
    // Then section
    // --------------------------------------
    private fun thenNoMovesAreLegal() {
        assertEquals(lastAskMoves, mapOf())
    }

    private fun thenMoveWasIllegal() {
        assertEquals(moveResult.last(), MoveResult.Illegal)
    }

    private fun thenMoveWasLegal() {
        assertEquals(moveResult.last(), MoveResult.Legal)
    }

    private fun thenStateIsInitial() {
        assertEquals(playArea.currentTeam(), White)
        assertEquals(playArea.inPlayCount(), 0)
        assertEquals(playArea.completedCount(White), 0)
        assertEquals(playArea.completedCount(Black), 0)
    }

    private fun thenWhiteHasCounterAt(square: Square) {
        assertEquals(playArea[square], White)
    }

    private fun thenBlackHasCounterAt(square: Square) {
        assertEquals(playArea[square], Black)
    }

    private fun thenSmallBoardIs(s: String) {
        val smallBoard: String = horizontalDrawnBoard.smallBoard()
            .joinToString("\n")
        assertEquals(s, smallBoard)
    }

    private fun thenHorizontalFullBoardIs(wantedBoard: String) {
        val board: String =
            horizontalDrawnBoard.fullBoard().joinToString("\n") { obj: String -> obj.trim { it <= ' ' } }
        assertEquals(board, wantedBoard)
    }

    private fun thenItsWhitesMove() {
        assertEquals(ur.currentTeam(), White)
    }

    private fun thenItsBlacksMove() {
        assertEquals(ur.currentTeam(), Black)
    }

    private fun thenAllMovesWereLegal() {
        for (i in moveResult.indices) {
            val r: MoveResult = moveResult.first()
            if (r === MoveResult.Illegal) {
                assertEquals(expected = false, actual = true, message = "Move [" + (i + 1) + "] was illegal")
            }
        }
    }

    private fun thenWhiteCompletedCountIs(count: Int) {
        assertEquals(playArea.completedCount(White), count)
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
        assertEquals(moveResult.last(), MoveResult.GameOver)
    }

    // TODO: add to all test failings
    private fun printBoard() {
//        horizontalDrawnBoard.fullBoard()
//            .map { obj: String -> obj.trim { it <= ' ' } }
//            .forEach(java.lang.System.out::println)
    }
}