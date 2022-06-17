package web

import com.damon140.ur.PlayArea
import com.damon140.ur.Square
import com.damon140.ur.Square.*
import com.damon140.ur.Team
import com.damon140.ur.Team.*
import kotlinx.browser.document
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.events.MouseEvent
import kotlin.math.PI
import kotlin.math.floor

class UrCanvasView(lastMove: LastMove, pageObject: UrPageObject) {
    private val lastMove: LastMove
    private val pageObject: UrPageObject
    private val htmlCanvasElement: HTMLCanvasElement
    private val canvas: CanvasRenderingContext2D

    private val squarePairMap: Map<Square, Pair<Int, Int>>

    init {
        this.lastMove = lastMove
        this.pageObject = pageObject
        this.htmlCanvasElement = pageObject.findCanvasBoard()

        this.canvas = htmlCanvasElement.getContext("2d") as CanvasRenderingContext2D

        this.squarePairMap = mapOf(
            white_run_on_1 to Pair(2, 3),
            white_run_on_2 to Pair(2, 2),
            white_run_on_3 to Pair(2, 1),
            white_run_on_4 to Pair(2, 0),
            white_run_off_2 to Pair(2, 6),
            white_run_off_1 to Pair(2, 7),

            black_run_on_1 to Pair(4, 3),
            black_run_on_2 to Pair(4, 2),
            black_run_on_3 to Pair(4, 1),
            black_run_on_4 to Pair(4, 0),
            black_run_off_2 to Pair(4, 6),
            black_run_off_1 to Pair(4, 7),

            shared_1 to Pair(3, 0),
            shared_2 to Pair(3, 1),
            shared_3 to Pair(3, 2),
            shared_4 to Pair(3, 3),
            shared_5 to Pair(3, 4),
            shared_6 to Pair(3, 5),
            shared_7 to Pair(3, 6),
            shared_8 to Pair(3, 7),

            off_board_unstarted to Pair(0, 0),
            off_board_finished to Pair(0, 0)
        )
    }

    fun updateWhiteCounters(unstartedCount: Int, completedCount: Int) {
        drawOffboardCounters(unstartedCount, completedCount, white)
    }

    fun updateBlackCounters(unstartedCount: Int, completedCount: Int) {
        drawOffboardCounters(unstartedCount, completedCount, black)
    }

    private fun drawOffboardCounters(unstarted: Int, finished: Int, team: Team) {
        var lambda: (Int) -> Int = { y: Int -> y }
        drawOffboardCounters(team, unstarted, lambda)

        lambda = { y: Int -> 400 - y }
        drawOffboardCounters(team, finished, lambda)
    }

    private fun drawOffboardCounters(team: Team, count: Int, lambda: (Int) -> Int) {
        val baseX = if (white == team) 0 else 250

        val twosCount = count / 2

        var counterLines: List<Pair<Int, Int>> = mutableListOf()

        // TODO: need grid thingy class w/ offsets around border
        (0..twosCount - 1).forEach { i ->
            val y = i * 50 + 25

            // vary by team
            val leftOffset = if (white == team) 25 else 50;
            val rightOffset = if (white == team) 50 else 75;

            counterLines += Pair(leftOffset + baseX, lambda(y))
            counterLines += Pair(rightOffset + baseX, lambda(y))
        }

        if (1 == count % 2) {
            val x = if (white == team) 25 else 75
            counterLines += Pair(x + baseX, lambda((twosCount * 50) + 25))
        }

        counterLines.forEach { p ->
            console.log("Drawing pair $p.first, $p.second")
            drawCounterByCoordinates(p.first + 0.0, p.second + 0.0, team)
        }
    }

    fun updateBoard(playArea: PlayArea) {
        drawBlanks()

        // TODO: add doubles and safe square

        playArea.countersForTeam(white).forEach { s -> drawOnBoardCounter(s, white) }
        playArea.countersForTeam(black).forEach { s -> drawOnBoardCounter(s, black) }

        // TODO: move to better place?
        pageObject.findDice().play()
        console.log("played sound")
    }

    fun updateInstructions(currentTeam: Team, roll: Int, continueFunction: () -> Unit) {
        val spanToUpdate: HTMLSpanElement
        val spanToBlank: HTMLSpanElement
        if (white == currentTeam) {
            spanToUpdate = pageObject.findRollWhite()
            spanToBlank = pageObject.findRollBlack()
        } else {
            spanToUpdate = pageObject.findRollBlack()
            spanToBlank = pageObject.findRollWhite()
        }

        spanToUpdate.innerText = "" + roll
        spanToBlank.innerText = ""
        val findRollSpace = pageObject.findRollSpace()
        findRollSpace.innerText = "";

        if (roll == 0) {

            val button = document.createElement("button") as HTMLButtonElement
            button.innerHTML = "You rolled zero, can't move, bummer!!"

            button.addEventListener("click", {
                console.log("Clicked on button!")
                // run continue function here
                continueFunction()
            })
            findRollSpace.append(button)
        }
    }

    private fun drawOnBoardCounter(square: Square, team: Team) {
        val s1 = squarePairMap.get(square)!!

        // TODO: need grid thingy class w/ offsets around border
        val x = s1.first * 50.0 + 25;
        val y = s1.second * 50.0 + 25;

        drawCounterByCoordinates(x, y, team)
    }

    private fun drawCounterByCoordinates(x: Double, y: Double, team: Team) {
        canvas.beginPath();
        canvas.arc(x, y, 19.0, 0.0, 2 * PI);
        canvas.lineWidth = 3.0


        if (team == black) {
            canvas.fillStyle = "green"
        } else {
            canvas.fillStyle = "white"
        }

        canvas.fill();
        canvas.stroke();
    }

    // TODO: add moves
    fun drawGrid(moves: Map<Square, Square>, continueFunction: () -> Unit) {

        val squares: List<Pair<Int, Int>> = Square.drawableSquares()
            .map { s -> squarePairMap.get(s)!! }

        drawSquares(canvas, squares)

        htmlCanvasElement.onclick = { event: MouseEvent ->
            val clientX = event.clientX
            val clientY = event.clientY

            var rect = this.htmlCanvasElement.getBoundingClientRect()
            val xIndex = floor((clientX - rect.left) / 50).toInt()
            val yIndex = floor((clientY - rect.top) / 50).toInt()
            console.log("x ind: $xIndex, y ind: $yIndex")

            val matchingSquairPairsList =
                squarePairMap.entries.filter { s -> s.value.first == xIndex && s.value.second == yIndex }
            val size = matchingSquairPairsList.size
            console.log("matching list size " + size);

            var clickedSquare: Square? = null;
            if (0 != size) {
                clickedSquare = matchingSquairPairsList
                    .map { entry -> entry.key }
                    .first()
            } else if (yIndex < 4) {
                // off board unstarted
                clickedSquare = off_board_unstarted
            }

            console.log("clicked square is $clickedSquare")
            console.log("move for clicked square: " + moves.containsKey(clickedSquare))

            if (null != clickedSquare) {
                console.log("trying to match move")
                for ((index, entry) in moves.entries.withIndex()) {
                    if (entry.key == clickedSquare) {
                        lastMove.setLastChosen((index + 1).toString())
                        // run continue function here
                        continueFunction()
                    }
                }
            }

            this
        }
    }

    private fun drawSquares(ctx: CanvasRenderingContext2D, squares: List<Pair<Int, Int>>) {
        squares.forEach { e ->
            ctx.beginPath();
            ctx.rect(50.0 * e.first, 50.0 * e.second, 50.0, 50.0);
            ctx.stroke();
        }
    }

    fun blank() {
        this.canvas.clearRect(0.0, 0.0, 350.0, 400.0);
    }

    private fun drawBlanks() {
        Square.drawableSquares()
            .map { s ->
                val square = squarePairMap.get(s)!!

                // TODO: find correct colour via new fn
                if (s.rollAgain()) {
                    canvas.fillStyle = if (s.isSafeSquare) "green" else "yellow"
                } else {
                    canvas.fillStyle = "white"
                }

                canvas.fillRect(50.0 * square.first + 1, 50.0 * square.second + 1, 48.0, 48.0)
            }
    }

}
