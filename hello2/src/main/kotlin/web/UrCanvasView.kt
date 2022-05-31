package web

import org.w3c.dom.CanvasRenderingContext2D
import kotlin.math.PI

class UrCanvasView(pageObject: UrPageObject) {
    private val pageObject: UrPageObject

    init {
        this.pageObject = pageObject
    }

    // TODO: move to new class
    fun drawSquare() {
        var c = pageObject.findCanvasBoard()

        // Super mega overworked example here, is not very easy to follow, too OO, shapes encapsulate canvas
        // https://play.kotlinlang.org/byExample/09_Kotlin_JS/05_Canvas

        var canvas: CanvasRenderingContext2D = c.getContext("2d") as CanvasRenderingContext2D

        canvas.beginPath();
        canvas.rect(20.0, 20.0, 150.0, 100.0);
        canvas.stroke();

        // empty circle
        canvas.beginPath();
        canvas.arc(100.0, 75.0, 50.0, 0.0, 2 * PI);
        canvas.lineWidth = 3.0
        canvas.stroke();

        // filled circle
        canvas.beginPath();
        canvas.arc(130.0, 100.0, 50.0, 0.0, 2 * PI);
        canvas.lineWidth = 3.0
        canvas.fill();
        // how in Kotlin?
        //canvas.fillStyle = 'green';
        canvas.stroke();


    }

}