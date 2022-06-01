package web

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import kotlin.math.PI

class UrCanvasView(pageObject: UrPageObject) {
    private val pageObject: UrPageObject

    init {
        this.pageObject = pageObject
    }

    fun drawSquare() {
        var htmlCanvasElement = pageObject.findCanvasBoard()

        // Super mega overworked example here, is not very easy to follow, too OO, shapes encapsulate canvas
        // https://play.kotlinlang.org/byExample/09_Kotlin_JS/05_Canvas

        var canvas: CanvasRenderingContext2D = htmlCanvasElement.getContext("2d") as CanvasRenderingContext2D

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

        htmlCanvasElement.onclick = { e: MouseEvent ->
            console.log("x: " + e.clientX);
            console.log("y: " + e.clientY);
            this
        }
    }

}
