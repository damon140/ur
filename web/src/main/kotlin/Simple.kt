import com.damon140.ur.Dice

fun main() {
    console.log("Hello, ${greet()} from the console!!")

    val dice = Dice()

    console.log("rolled " + dice.roll())

    console.log("rolled " + dice.roll())

    console.log("rolled " + dice.roll())

    console.log("rolled " + dice.roll())

    console.log("rolled " + dice.roll())

    console.log("rolled " + dice.roll())

    console.log("rolled " + dice.roll())

    console.log("rolled " + dice.roll())

    console.log("rolled " + dice.roll())

    console.log("rolled " + dice.roll())
}

// https://kotlinlang.org/docs/browser-api-dom.html#interaction-with-the-dom

fun greet() = "world"
