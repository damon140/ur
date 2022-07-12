package web

import com.damon140.ur.PlayArea
import com.damon140.ur.Square
import com.damon140.ur.Square.*
import com.damon140.ur.Team
import com.damon140.ur.Team.*
import kotlinx.browser.document
import kotlinx.browser.window
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
    private var animateIntervalHandle = 0

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

    fun drawShowRollButton(playArea: PlayArea, continueFunction: () -> Unit) {
        blank()
        // TODO: can replace with draw most?
        drawGrid()

        updateWhiteCounters(playArea.unstartedCount(white), playArea.completedCount(white))
        updateBlackCounters(playArea.unstartedCount(black), playArea.completedCount(black))
        updateBoard(playArea)

        // instructions
        val findRollSpace = pageObject.findRollSpace()
        findRollSpace.innerText = ""

        val button = document.createElement("button") as HTMLButtonElement
        button.innerHTML = "Click to roll"

        // blank out last roll
        pageObject.findRollBlack().innerText = ""
        pageObject.findRollWhite().innerText = ""

        button.addEventListener("click", {
            console.log("Clicked on button!")

            // TODO: play sound here and add timeout fn to run callback, wrap callbacks

            // run continue function here
            continueFunction()
        })
        findRollSpace.append(button)
    }

    fun drawRobotThinking(playArea: PlayArea) {
        drawMost(playArea)
        pageObject.findRollSpace().innerText = "AI is thinking"

        // blank out last roll
        pageObject.findRollBlack().innerText = ""
        pageObject.findRollWhite().innerText = ""
    }

    // TODO: make private??
    fun drawMost(playArea: PlayArea) {
        blank()
        drawGrid()

        updateWhiteCounters(playArea.unstartedCount(white), playArea.completedCount(white))
        updateBlackCounters(playArea.unstartedCount(black), playArea.completedCount(black))
        updateBoard(playArea)
    }

    fun drawAll(
        currentTeam: Team,
        roll: Int,
        moves: Map<Square, Square>,
        playArea: PlayArea,
        continueFunction: () -> Unit
    ) {
        drawMost(playArea)

        attachClickHandler(moves, continueFunction)

        updateInstructions(currentTeam, roll, moves.isEmpty(), continueFunction)
    }


    private fun updateWhiteCounters(unstartedCount: Int, completedCount: Int) {
        drawOffboardCounters(unstartedCount, completedCount, white)
    }

    private fun updateBlackCounters(unstartedCount: Int, completedCount: Int) {
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

        var counterLines: MutableList<Pair<Int, Int>> = mutableListOf()

        // TODO: need grid thingy class w/ offsets around border
        (0 until twosCount).forEach { i ->
            val y = i * 50 + 25

            // vary by team
            val leftOffset = if (white == team) 25 else 50
            val rightOffset = if (white == team) 50 else 75

            counterLines.add(Pair(leftOffset + baseX, lambda(y)))
            counterLines.add(Pair(rightOffset + baseX, lambda(y)))
        }

        if (1 == count % 2) {
            val x = if (white == team) 25 else 75
            counterLines += Pair(x + baseX, lambda((twosCount * 50) + 25))
        }

        counterLines.forEach { p ->
            //console.log("Drawing pair $p.first, $p.second")
            drawCounterByCoordinates(p.first + 0.0, p.second + 0.0, team)
        }
    }

    fun updateBoard(playArea: PlayArea) {
        drawBlanks()

        playArea.countersForTeam(white).forEach { s -> drawOnBoardCounter(s, white) }
        playArea.countersForTeam(black).forEach { s -> drawOnBoardCounter(s, black) }
    }

    private fun updateInstructions(currentTeam: Team, roll: Int, zeroMoves: Boolean, continueFunction: () -> Unit) {
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
        findRollSpace.innerText = ""

        if (roll == 0) {
            val button = document.createElement("button") as HTMLButtonElement
            button.innerHTML = "You rolled zero, can't move, bummer!!"

            button.addEventListener("click", {
                console.log("Clicked on button!")
                // run continue function here
                continueFunction()
            })
            findRollSpace.append(button)
        } else if (zeroMoves) {
            val button = document.createElement("button") as HTMLButtonElement
            button.innerHTML = "All moves blocked, bummer!!"

            button.addEventListener("click", {
                console.log("Clicked on button!")
                // run continue function here
                continueFunction()
            })
            findRollSpace.append(button)
        }
    }

    fun gameWon(currentTeam: Team) {
        // black rolls
        pageObject.findRollWhite().innerHTML = ""
        pageObject.findRollBlack().innerHTML = ""

        val button = document.createElement("button") as HTMLButtonElement
        button.innerHTML = "Game won by $currentTeam. Click to restart"

        button.addEventListener("click", {
            console.log("reloading")
            window.location.reload()
            // FIXME: or playArea.reset??
        })

        val findRollSpace = pageObject.findRollSpace()
        findRollSpace.innerText = ""
        findRollSpace.append(button)
    }

    private fun drawOnBoardCounter(square: Square, team: Team) {
        val pair = squarePairMap[square]!!

        drawOnBoardCounter(pair, team)
    }

    private fun drawOnBoardCounter(pair: Pair<Int, Int>, team: Team) {
        // TODO: need grid thingy class w/ offsets around border
        val x = pair.first * 50.0 + 25
        val y = pair.second * 50.0 + 25

        drawCounterByCoordinates(x, y, team)
    }

    private fun drawOnBoardCounter(pair: Pair<Double, Double>, team: Team) {
        // TODO: need grid thingy class w/ offsets around border
        val x = pair.first * 50.0 + 25
        val y = pair.second * 50.0 + 25

        drawCounterByCoordinates(x, y, team)
    }

    private fun drawCounterByCoordinates(x: Double, y: Double, team: Team) {
        canvas.beginPath()
        canvas.arc(x, y, 19.0, 0.0, 2 * PI)
        canvas.lineWidth = 3.0


        if (team == black) {
            canvas.fillStyle = "green"
        } else {
            canvas.fillStyle = "white"
        }

        canvas.fill()
        canvas.stroke()
    }



    private fun drawGrid() {
        val squares: List<Pair<Int, Int>> = Square.drawableSquares()
            .map { s -> squarePairMap.get(s)!! }

        drawSquares(squares)
    }

    fun attachClickHandler(moves: Map<Square, Square>, continueFunction: () -> Unit) {

        htmlCanvasElement.onclick = { event: MouseEvent ->
            val clientX = event.clientX
            val clientY = event.clientY

            val rect = this.htmlCanvasElement.getBoundingClientRect()
            val xIndex = floor((clientX - rect.left) / 50).toInt()
            val yIndex = floor((clientY - rect.top) / 50).toInt()
            //console.log("x ind: $xIndex, y ind: $yIndex")

            val matchingSquairPairsList =
                squarePairMap.entries.filter { s -> s.value.first == xIndex && s.value.second == yIndex }
            val size = matchingSquairPairsList.size
            //console.log("matching list size " + size);

            var clickedSquare: Square? = null
            if (0 != size) {
                clickedSquare = matchingSquairPairsList
                    .map { entry -> entry.key }
                    .first()
            } else if (yIndex < 4) {
                // off board unstarted
                clickedSquare = off_board_unstarted
            }

            console.log("move for clicked square $clickedSquare: ${moves.containsKey(clickedSquare)}")
            console.log(moves.keys.joinToString(","))

            if (null != clickedSquare) {
                for ((index, entry) in moves.entries.withIndex()) {
                    if (entry.key == clickedSquare) {
                        console.log("matched move of $clickedSquare")
                        lastMove.setLastChosen((index + 1).toString())
                        // run continue function here
                        continueFunction()
                    }
                }
            }

            this
        }
    }

    private fun drawSquares(pairs: List<Pair<Int, Int>>) {
        pairs.forEach { pair ->
            canvas.beginPath()
            canvas.rect(50.0 * pair.first, 50.0 * pair.second, 50.0, 50.0)
            canvas.stroke()
        }
    }

    private fun blank() {
        this.canvas.clearRect(0.0, 0.0, 350.0, 400.0)
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

    fun playDiceRoll() {
        pageObject.findDice().play()
        console.log("playing sound")

        // TODO: clear and set event listener
        pageObject.findDice().addEventListener("ended", {
            console.log("sound play ended")
        })
    }

    fun playHmm() {
        pageObject.findHmm().play()
        console.log("playing sound")

        // TODO: clear and set event listener
        pageObject.findDice().addEventListener("ended", {
            console.log("sound play ended")
        })
    }

    fun animate(playArea: PlayArea, team: Team, fromSquare: Square, toSquare: Square, continueFunction: () -> Unit) {
        val squares = Square.calculateSquaresBetween(team, fromSquare, toSquare)
        console.log("Will animate counter path $squares")

        var oneSqaureAnim = 0

        var lastSquare = squares.removeFirst()
        var currentSquare = squares.removeFirst()

        // Hmm, this sort of works
        val handler: () -> Unit = {
            val countCompleted = oneSqaureAnim++ == 5
            var draw = true
            if (countCompleted) {
                if (squares.isEmpty()) {
                    window.clearInterval(this.animateIntervalHandle)
                    draw = false
                    drawMost(playArea)
                    continueFunction()
                } else {
                    oneSqaureAnim = 0
                    lastSquare = currentSquare
                    currentSquare = squares.removeFirst()
                }
            }

            if (draw) {
                val square1Pos: Pair<Int, Int> = positionForAnimation(team, lastSquare)
                val square2Pos: Pair<Int, Int> = positionForAnimation(team, currentSquare)

                val pair = tween(oneSqaureAnim.toDouble(), 5.toDouble(), square1Pos, square2Pos)
                console.log("anim of $currentSquare step $oneSqaureAnim" + pair)

                drawOnBoardCounter(pair, team)
            }
        }

        this.animateIntervalHandle = window.setInterval(handler, 85)
    }

    private fun positionForAnimation(team: Team, square: Square): Pair<Int, Int> {
        // use special positions for each team for finishing and starting squares
        return when {
            square == off_board_unstarted && team == white -> Pair(2, 4)
            square == off_board_unstarted && team == black -> Pair(4, 4)
            square == off_board_finished && team == white -> Pair(2, 5)
            square == off_board_finished && team == black -> Pair(4, 5)
            else -> squarePairMap.get(square)!!
        }
    }

    private fun tween(step: Double, ofSteps: Double, square1Pos: Pair<Int, Int>, square2Pos: Pair<Int, Int>) :Pair<Double, Double> {
        val first = square1Pos.first + (square2Pos.first - square1Pos.first) * (step / ofSteps)
        val second = square1Pos.second + (square2Pos.second - square1Pos.second) * (step / ofSteps)

        return Pair(first, second)
    }

}
