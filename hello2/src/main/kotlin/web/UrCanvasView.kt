package web

import com.damon140.ur.Square
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.events.MouseEvent
import kotlin.math.PI

class UrCanvasView(pageObject: UrPageObject) {
    private val pageObject: UrPageObject

    init {
        this.pageObject = pageObject
    }

    fun drawSquare() {
        var htmlCanvasElement = pageObject.findCanvasBoard()
        var canvas: CanvasRenderingContext2D = htmlCanvasElement.getContext("2d") as CanvasRenderingContext2D

        val squares: List<Pair<Int, Int>> = listOf(
            Square.black_run_on_1,
            Square.black_run_on_2,
            Square.black_run_on_3,
            Square.black_run_on_4,
            Square.black_run_off_1,
            Square.black_run_off_2,
            Square.shared_1,
            Square.shared_2,
            Square.shared_3,
            Square.shared_4,
            Square.shared_5,
            Square.shared_6,
            Square.shared_7,
            Square.shared_8,
            Square.white_run_on_1,
            Square.white_run_on_2,
            Square.white_run_on_3,
            Square.white_run_on_4,
            Square.white_run_off_1,
            Square.white_run_off_2
        ).map { s -> squareToCoordinates(s) }

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
            console.log("x: " + e.clientX);
            console.log("y: " + e.clientY);
            this
        }
    }

    private fun squareToCoordinates(square: Square): Pair<Int, Int> {
        return when(square) {
            Square.white_run_on_1 -> Pair(2, 3)
            Square.white_run_on_2 -> Pair(2, 2)
            Square.white_run_on_3 -> Pair(2, 1)
            Square.white_run_on_4 -> Pair(2, 0)
            Square.white_run_off_1 ->Pair(2, 7)
            Square.white_run_off_2 -> Pair(2, 8)

            Square.black_run_on_1 -> Pair(4, 3)
            Square.black_run_on_2 -> Pair(4, 2)
            Square.black_run_on_3 -> Pair(4, 1)
            Square.black_run_on_4 -> Pair(4, 0)
            Square.black_run_off_1 -> Pair(4, 7)
            Square.black_run_off_2 -> Pair(4, 8)

            Square.shared_1 -> Pair(3, 0)
            Square.shared_2 -> Pair(3, 1)
            Square.shared_3 -> Pair(3, 2)
            Square.shared_4 -> Pair(3, 3)
            Square.shared_5 -> Pair(3, 4)
            Square.shared_6 -> Pair(3, 5)
            Square.shared_7 -> Pair(3, 6)
            Square.shared_8 -> Pair(3, 7)

            Square.off_board_unstarted -> Pair(0, 0)
            Square.off_board_finished -> Pair(0, 0)
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
