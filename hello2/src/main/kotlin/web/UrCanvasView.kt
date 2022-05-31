package web

import org.w3c.dom.CanvasRenderingContext2D

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

        var ctx: CanvasRenderingContext2D = c.getContext("2d") as CanvasRenderingContext2D

        ctx.beginPath();
        ctx.rect(20.0, 20.0, 150.0, 100.0);
        ctx.stroke();
    }

}