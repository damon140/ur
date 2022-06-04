package web

import com.damon140.ur.PlayArea
import com.damon140.ur.Square
import com.damon140.ur.Square.*
import com.damon140.ur.Team
import com.damon140.ur.Team.*
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.MouseEvent
import kotlin.math.PI

class UrCanvasView(pageObject: UrPageObject) {
    private val pageObject: UrPageObject
    private val htmlCanvasElement: HTMLCanvasElement
    private val canvas: CanvasRenderingContext2D

    private val squarePairMap: Map<Square, Pair<Int, Int>>

    init {
        this.pageObject = pageObject
        this.htmlCanvasElement = pageObject.findCanvasBoard()
        this.canvas = htmlCanvasElement.getContext("2d") as CanvasRenderingContext2D

        this.squarePairMap = mapOf(
            white_run_on_1 to Pair(2, 3),
            white_run_on_2 to Pair(2, 2),
            white_run_on_3 to Pair(2, 1),
            white_run_on_4 to Pair(2, 0),
            // FIXME: too deep
            white_run_off_1  to Pair(2, 7),
            white_run_off_2 to Pair(2, 8),

            black_run_on_1 to Pair(4, 3),
            black_run_on_2 to Pair(4, 2),
            black_run_on_3 to Pair(4, 1),
            black_run_on_4 to Pair(4, 0),
            // FIXME: too deep
            black_run_off_1 to Pair(4, 7),
            black_run_off_2 to Pair(4, 8),

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

    }

    fun updateBlackCounters(unstartedCount: Int, completedCount: Int) {

    }

    // TODO: switch to other input type
    fun updateBoard(playArea: PlayArea) {
        drawCounter(black_run_on_1, black)
        drawCounter(white_run_on_1, white)
    }

    private fun drawCounter(square: Square, team: Team) {
        val filled: Boolean = team == black;
        val s1 = squarePairMap.get(square)!!

        // TODO: push to fn

        // TODO: need grid thingy class
        val x = s1.first * 50.0 + 25;
        val y = s1.second * 50.0 + 25;

        // filled circle
        canvas.beginPath();
        canvas.arc(x, y, 20.0, 0.0, 2 * PI);
        canvas.lineWidth = 3.0

        if (filled) {
            canvas.fill();
        }

        // how in Kotlin?
        //canvas.fillStyle = 'green';
        canvas.stroke();
    }


    fun updateInstructions() {

    }

    fun drawCounter() {

        val squares: List<Pair<Int, Int>> = Square.drawableSquares()
            .map { s -> squarePairMap.get(s)!! }

        drawSquares(canvas, squares)

        htmlCanvasElement.onclick = { e: MouseEvent ->

            //squarePairMap.values.filter {  }


            val clientX = e.clientX
            val clientY = e.clientY
            console.log("x: $clientX")
            console.log("y: $clientY")

            val xIndex = clientX / 50
            val yIndex = clientY / 50
            console.log("x ind: $xIndex")
            console.log("y ind: $yIndex")

            this
        }
    }

    private fun drawSquares(ctx: CanvasRenderingContext2D, squares: List<Pair<Int, Int>>) {
        squares.forEach {
                e ->
            ctx.beginPath();
            ctx.rect(50.0 * e.first, 50.0 * e.second, 50.0, 50.0);
            ctx.stroke();
        }
    }

}
