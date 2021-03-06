package web

import com.damon140.ur.PlayArea
import com.damon140.ur.Square
import com.damon140.ur.Square.*
import com.damon140.ur.Team
import com.damon140.ur.Team.*
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.*
import org.w3c.dom.events.MouseEvent
import web.UrCanvasView.CounterSize.big
import web.UrCanvasView.CounterSize.little
import kotlin.math.PI
import kotlin.math.floor

class UrCanvasView(lastMove: LastMove, pageObject: UrPageObject) {
    private val lastMove: LastMove
    private val pageObject: UrPageObject
    private val htmlCanvasElement: HTMLCanvasElement
    private val canvas: CanvasRenderingContext2D
    private var moveCounterIntervalHandle = 0
    private var animateMovesIntervalHandle = 0

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
        makeUnstartedOffboardCounterPairs(team, unstarted)
            .plus(makeFinishedOffboardCounterPairs(team, finished))
            .forEach { p -> drawCounterByCoordinates(p.first + 0.0, p.second + 0.0, team, big) }
    }

    private fun makeFinishedOffboardCounterPairs(team: Team, finished: Int)
        = makeOffboardCounterPairs(team, finished) { y: Int -> 400 - y }

    private fun makeUnstartedOffboardCounterPairs(team: Team, unstarted: Int)
        = makeOffboardCounterPairs(team, unstarted) { y: Int -> y }

    private fun makeOffboardCounterPairs(
        team: Team,
        count: Int,
        lambda: (Int) -> Int
    ): MutableList<Pair<Int, Int>> {
        val baseX = if (white == team) 0 else 250
        val twosCount = count / 2
        val counterLines: MutableList<Pair<Int, Int>> = mutableListOf()

        // TODO: need grid thingy class w/ offsets around border
        (0 until twosCount).forEach { i ->
            val y = i * 50 + 25

            val sideXOffsetOffset = 10
            val leftOffset = if (white == team) 25 else 25 + sideXOffsetOffset
            val rightOffset = if (white == team) 75 - sideXOffsetOffset else 75

            val sideYOffsetOffset = 19
            if (white == team) {
                counterLines.add(Pair(leftOffset + baseX, lambda(y)))
                counterLines.add(Pair(rightOffset + baseX, lambda(y + sideYOffsetOffset)))
            } else {
                counterLines.add(Pair(leftOffset + baseX, lambda(y + sideYOffsetOffset)))
                counterLines.add(Pair(rightOffset + baseX, lambda(y)))
            }
        }

        if (1 == count % 2) {
            val x = if (white == team) 25 else 75
            counterLines += Pair(x + baseX, lambda((twosCount * 50) + 25))
        }
        return counterLines
    }

    fun updateBoard(playArea: PlayArea) {
        drawBlanks()

        playArea.countersForTeam(white).forEach { s -> drawCounterByIndex(s, white, big) }
        playArea.countersForTeam(black).forEach { s -> drawCounterByIndex(s, black, big) }
    }

    private fun updateInstructions(currentTeam: Team, roll: Int, zeroMoves: Boolean, continueFunction: () -> Unit) {
        val spanToUpdate: HTMLDivElement
        val spanToBlank: HTMLDivElement
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

    private fun drawCounterByIndex(square: Square, team: Team, size: CounterSize) {
        drawCounterByIndex(squarePairMap[square]!!, team, size)
    }

    private fun drawCounterByIndex(pair: Pair<Int, Int>, team: Team, size: CounterSize) {
        val x = indexToCoordinate(pair.first)
        val y = indexToCoordinate(pair.second)

        drawCounterByCoordinates(x, y, team, size)
    }

    private fun drawCounterByIndex(pair: Pair<Double, Double>, team: Team, size: CounterSize) {
        val x = indexToCoordinate(pair.first)
        val y = indexToCoordinate(pair.second)

        drawCounterByCoordinates(x, y, team, size)
    }

    // TODO: need grid thingy class w/ offsets around border
    private fun indexToCoordinate(value: Double): Double = value * 50.0 + 25
    private fun indexToCoordinate(value: Int): Double = value * 50.0 + 25

    enum class CounterSize(val pixels: Double) { big(19.toDouble()), little(16.toDouble()) }

    private fun drawCounterByCoordinates(x: Double, y: Double, team: Team, size: CounterSize) {
        canvas.beginPath()
        canvas.arc(x, y, size.pixels, 0.0, 2 * PI)
        canvas.lineWidth = 3.0
        canvas.fillStyle = if (team == black) "pink" else "#abcedf"
        canvas.fill()
        canvas.stroke()
    }

    private fun drawGrid() {
        Square.drawableSquares().forEach { square ->
            drawSquare(square)
        }
    }

    private fun attachClickHandler(moves: Map<Square, Square>, continueFunction: () -> Unit) {

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

            var found = false

            if (null != clickedSquare) {
                for ((index, entry) in moves.entries.withIndex()) {
                    if (entry.key == clickedSquare) {
                        console.log("matched move of $clickedSquare")
                        lastMove.setLastChosen((index + 1).toString())
                        // run continue function here
                        continueFunction()
                        found = true
                    }
                }
            }
            if (!found) {
                playBaBowSound()
            }

            this
        }
    }

    private fun drawSquare(square: Square) {
        val pair = squarePairMap.get(square)!!
        canvas.beginPath()
        canvas.rect(50.0 * pair.first, 50.0 * pair.second, 50.0, 50.0)
        canvas.stroke()
    }

    private fun blank() {
        this.canvas.clearRect(0.0, 0.0, 350.0, 400.0)
    }

    private fun drawBlanks() {
        Square.drawableSquares()
            .map { s ->
                val square = squarePairMap.get(s)!!
                val x = 50.0 * square.first + 1
                val y = 50.0 * square.second + 1

                val rollAgain = s.rollAgain()
                val safeSquare = s.isSafeSquare
                val squareFillColour =
                    if (rollAgain) {
                        if (safeSquare) {
                            "orange"
                        } else {
                            "yellow"
                        }
                    } else {
                        "white"
                    }

                canvas.fillStyle = squareFillColour
                canvas.fillRect(x, y, 48.0, 48.0)

                if (rollAgain) {
                    val offset = 9
                    listOfNotNull(
                        Pair(25 - offset, 25),
                        Pair(25 + offset, 25),
                        Pair(25, 25 - offset),
                        Pair(25, 25 + offset)
                    )
                        .forEach { p ->
                            canvas.beginPath()
                            canvas.arc(x + p.first, y + p.second, 6.0, 0.0, 2 * PI)
                            canvas.lineWidth = if (safeSquare) 1.8 else 1.1
                            canvas.fillStyle = if (safeSquare) "red" else "orange"
                            canvas.fill()
                            canvas.stroke()
                        }
                }
            }
    }

    fun playDiceRoll() {
        pageObject.playDiceSound()
    }

    fun playHmm() {
        pageObject.playHmmSound()
    }

    fun playCounterTakenSound() {
        pageObject.playCounterTakenSound()
    }

    private fun playBaBowSound() {
        pageObject.playBaBowSound()
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
                    window.clearInterval(this.moveCounterIntervalHandle)
                    draw = false
                    drawMost(playArea)
                    continueFunction()
                } else {
                    oneSqaureAnim = 0
                    lastSquare = currentSquare
                    currentSquare = squares.removeFirst()
                }
                pageObject.playTicSound()
            }

            if (draw) {
                val square1Pos: Pair<Int, Int> = positionForAnimation(team, lastSquare)
                val square2Pos: Pair<Int, Int> = positionForAnimation(team, currentSquare)

                val pair = tween(oneSqaureAnim.toDouble(), 5.toDouble(), square1Pos, square2Pos)
                console.log("anim of $currentSquare step $oneSqaureAnim$pair")

                drawCounterByIndex(pair, team, big)
            }
        }

        this.moveCounterIntervalHandle = window.setInterval(handler, 85)
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

    private fun tween(
        step: Double,
        ofSteps: Double,
        square1Pos: Pair<Int, Int>,
        square2Pos: Pair<Int, Int>
    ): Pair<Double, Double> {
        val first = square1Pos.first + (square2Pos.first - square1Pos.first) * (step / ofSteps)
        val second = square1Pos.second + (square2Pos.second - square1Pos.second) * (step / ofSteps)

        return Pair(first, second)
    }

    fun startMovesAnimation(playArea: PlayArea, team: Team, movesFrom: Set<Square>) {
        val unstartedCount = playArea.unstartedCount(team)
        val pairs = movesFrom.map { square ->
            if (square == off_board_unstarted) {
                val f = makeUnstartedOffboardCounterPairs(team, unstartedCount).last()
                f
            } else {
                val indexPair = squarePairMap[square]!!
                Pair(indexToCoordinate(indexPair.first), indexToCoordinate(indexPair.second))
            }
        }.toList()

        var bigAnim = false
        val handler: () -> Unit = {
            pairs.forEach { pair -> drawCounterByCoordinates(pair.first.toDouble(), pair.second.toDouble(), team, if (bigAnim) big else little) }
            bigAnim = !bigAnim
            console.log("anim")
        }

        this.animateMovesIntervalHandle = window.setInterval(handler, 777)
    }

    fun endMovesAnimation() {
        window.clearInterval(this.animateMovesIntervalHandle)
    }

}
