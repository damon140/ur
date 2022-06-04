package web

import com.damon140.ur.PlayArea
import com.damon140.ur.Square
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.events.MouseEvent
import kotlin.math.PI

class UrCanvasView(pageObject: UrPageObject) {
    private val pageObject: UrPageObject
    private val squarePairMap: Map<Square, Pair<Int, Int>>

    init {
        this.pageObject = pageObject
        squarePairMap = mapOf(
            Square.white_run_on_1 to Pair(2, 3),
            Square.white_run_on_2 to Pair(2, 2),
            Square.white_run_on_3 to Pair(2, 1),
            Square.white_run_on_4 to Pair(2, 0),
            Square.white_run_off_1  to Pair(2, 7),
            Square.white_run_off_2 to Pair(2, 8),

            Square.black_run_on_1 to Pair(4, 3),
            Square.black_run_on_2 to Pair(4, 2),
            Square.black_run_on_3 to Pair(4, 1),
            Square.black_run_on_4 to Pair(4, 0),
            Square.black_run_off_1 to Pair(4, 7),
            Square.black_run_off_2 to Pair(4, 8),

            Square.shared_1 to Pair(3, 0),
            Square.shared_2 to Pair(3, 1),
            Square.shared_3 to Pair(3, 2),
            Square.shared_4 to Pair(3, 3),
            Square.shared_5 to Pair(3, 4),
            Square.shared_6 to Pair(3, 5),
            Square.shared_7 to Pair(3, 6),
            Square.shared_8 to Pair(3, 7),

            Square.off_board_unstarted to Pair(0, 0),
            Square.off_board_finished to Pair(0, 0)
        )
    }

    fun updateWhiteCounters(unstartedCount: Int, completedCount: Int) {

    }

    fun updateBlackCounters(unstartedCount: Int, completedCount: Int) {

    }

    // TODO: switch to other input type
    fun updateBoard(playArea: PlayArea) {

    }

    fun updateInstructions() {

    }

    fun drawSquare() {
        var htmlCanvasElement = pageObject.findCanvasBoard()
        var canvas: CanvasRenderingContext2D = htmlCanvasElement.getContext("2d") as CanvasRenderingContext2D

        val squares: List<Pair<Int, Int>> = Square.drawableSquares()
            .map { s -> squarePairMap.get(s)!! }

        drawSquares(canvas, squares)

        // filled circle
        canvas.beginPath();
        canvas.arc(130.0, 100.0, 50.0, 0.0, 2 * PI);
        canvas.lineWidth = 3.0
        canvas.fill();
        // how in Kotlin?
        //canvas.fillStyle = 'green';
        canvas.stroke();

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
