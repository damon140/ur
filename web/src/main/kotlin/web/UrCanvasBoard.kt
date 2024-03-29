/*
 * Copyright 2023 Damon van Opdorp
 *
 * Licensed under GNU General Public License v3.0.
 *
 * https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package web

import com.damon140.ur.PlayArea
import com.damon140.ur.Square
import com.damon140.ur.Square.*
import com.damon140.ur.Team
import com.damon140.ur.Team.*
import org.w3c.dom.*
import org.w3c.dom.events.MouseEvent
import web.UrCanvasBoard.CounterSize.Big
import web.UrCanvasBoard.CounterSize.Little
import kotlin.math.PI
import kotlin.math.floor

class UrCanvasBoard(pageObject: UrPageObject) {

    private val htmlCanvasElement: HTMLCanvasElement
    private val canvas: CanvasRenderingContext2D

    private val squarePairMap: Map<Square, Pair<Int, Int>>

    init {
        this.htmlCanvasElement = pageObject.findCanvasBoard()
        this.canvas = htmlCanvasElement.getContext("2d") as CanvasRenderingContext2D

        this.squarePairMap = mapOf(
            White_run_on_1 to Pair(2, 3),
            White_run_on_2 to Pair(2, 2),
            White_run_on_3 to Pair(2, 1),
            White_run_on_4 to Pair(2, 0),
            White_run_off_2 to Pair(2, 6),
            White_run_off_1 to Pair(2, 7),

            Black_run_on_1 to Pair(4, 3),
            Black_run_on_2 to Pair(4, 2),
            Black_run_on_3 to Pair(4, 1),
            Black_run_on_4 to Pair(4, 0),
            Black_run_off_2 to Pair(4, 6),
            Black_run_off_1 to Pair(4, 7),

            Shared_1 to Pair(3, 0),
            Shared_2 to Pair(3, 1),
            Shared_3 to Pair(3, 2),
            Shared_4 to Pair(3, 3),
            Shared_5 to Pair(3, 4),
            Shared_6 to Pair(3, 5),
            Shared_7 to Pair(3, 6),
            Shared_8 to Pair(3, 7),

            Off_board_unstarted to Pair(0, 0),
            Off_board_finished to Pair(0, 0)
        )
    }

    fun draw(playArea: PlayArea) {
        blank()
        drawGrid()

        updateWhiteCounters(playArea.unstartedCount(White), playArea.completedCount(White))
        updateBlackCounters(playArea.unstartedCount(Black), playArea.completedCount(Black))
        updateBoard(playArea)
    }

    fun click(squareClicked: (Square) -> Unit) {
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
                clickedSquare = Off_board_unstarted
            }

            squareClicked.invoke(clickedSquare!!)
            this
        }
    }

    private fun updateWhiteCounters(unstartedCount: Int, completedCount: Int) {
        drawOffboardCounters(unstartedCount, completedCount, White)
    }

    private fun updateBlackCounters(unstartedCount: Int, completedCount: Int) {
        drawOffboardCounters(unstartedCount, completedCount, Black)
    }

    private fun drawOffboardCounters(unstarted: Int, finished: Int, team: Team) {
        makeUnstartedOffboardCounterPairs(team, unstarted)
            .plus(makeFinishedOffboardCounterPairs(team, finished))
            .forEach { p -> drawCounterByCoordinates(p.first + 0.0, p.second + 0.0, team, Big) }
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
        val baseX = if (White == team) 0 else 250
        val twosCount = count / 2
        val counterLines: MutableList<Pair<Int, Int>> = mutableListOf()

        // TODO: need grid thingy class w/ offsets around border
        (0 until twosCount).forEach { i ->
            val y = i * 50 + 25

            val sideYOffsetOffset = 25
            if (White == team) {
                counterLines.add(Pair(24 + baseX, lambda(y)))
                counterLines.add(Pair(69 - 8 + baseX, lambda(y + sideYOffsetOffset)))
            } else {
                counterLines.add(Pair(38 + baseX, lambda(y + sideYOffsetOffset)))
                counterLines.add(Pair(75 + baseX, lambda(y)))
            }
        }

        if (1 == count % 2) {
            val x = if (White == team) 24 else 75
            counterLines += Pair(x + baseX, lambda((twosCount * 50) + 25))
        }
        return counterLines
    }

    fun updateBoard(playArea: PlayArea) {
        drawBlanks()

        playArea.countersForTeam(White).forEach { s -> drawCounterByIndex(s, White, Big) }
        playArea.countersForTeam(Black).forEach { s -> drawCounterByIndex(s, Black, Big) }
    }

    private fun drawCounterByIndex(square: Square, team: Team, size: CounterSize) {
        drawCounterByIndex(squarePairMap.getValue(square), team, size)
    }

    private fun drawCounterByIndex(pair: Pair<Int, Int>, team: Team, size: CounterSize) {
        val x = indexToCoordinate(pair.first)
        val y = 2 + indexToCoordinate(pair.second)

        drawCounterByCoordinates(x, y, team, size)
    }

    private fun drawCounterByIndex(pair: Pair<Double, Double>, team: Team, size: CounterSize) {
        val x = indexToCoordinate(pair.first)
        val y = 2 + indexToCoordinate(pair.second)

        drawCounterByCoordinates(x, y, team, size)
    }

    // TODO: need grid thingy class w/ offsets around border
    private fun indexToCoordinate(value: Double): Double = value * 50.0 + 25
    private fun indexToCoordinate(value: Int): Double = value * 50.0 + 25

    enum class CounterSize(val pixels: Double) { Big(19.toDouble()), Little(16.toDouble()) }

    private fun drawCounterByCoordinates(x: Double, y: Double, team: Team, size: CounterSize) {
        canvas.beginPath()
        canvas.arc(x, y, size.pixels, 0.0, 2 * PI)
        canvas.lineWidth = 3.0
        canvas.fillStyle = if (team == Black) "pink" else "#abcedf"
        canvas.fill()
        canvas.stroke()

//        // FIXME: add lines for greyscale play
//        if (team == black) {
//            canvas.beginPath()
//            canvas.moveTo(x - 15, y + 10)
//            canvas.lineTo(x, y + 3)
//            canvas.stroke()
//        }
    }

    private fun drawGrid() {
        Square.drawableSquares().forEach(::drawSquare)
    }



    private fun drawSquare(square: Square) {
        val pair = squarePairMap.getValue(square)

        //canvas.clearRect(50.0 * pair.first, 50.0 * pair.second, 50.0, 50.0)

        // TODO: after animate it goes black, why??
        canvas.beginPath()
        canvas.rect(50.0 * pair.first, 2 + 50.0 * pair.second, 50.0, 50.0)
        canvas.stroke()
    }

    private fun blank() {
        this.canvas.clearRect(0.0, 0.0, 350.0, 404.0)
    }

    private fun drawBlanks() {
        Square.drawableSquares()
            .map { s ->
                val square = squarePairMap.getValue(s)
                val x = 50.0 * square.first + 1
                val y = 2 + 50.0 * square.second + 1

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


    private fun positionForAnimation(team: Team, square: Square): Pair<Int, Int> {
        // use special positions for each team for finishing and starting squares
        return when {
            square == Off_board_unstarted && team == White -> Pair(2, 4)
            square == Off_board_unstarted && team == Black -> Pair(4, 4)
            square == Off_board_finished && team == White -> Pair(2, 5)
            square == Off_board_finished && team == Black -> Pair(4, 5)
            else -> squarePairMap.getValue(square)
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

    fun startMovesAnimation(playArea: PlayArea, team: Team, movesFrom: Set<Square>): () -> Unit {
        val unstartedCount = playArea.unstartedCount(team)
        val pairs = movesFrom.map { square ->
            if (square == Off_board_unstarted) {
                val f = makeUnstartedOffboardCounterPairs(team, unstartedCount).last()
                f
            } else {
                val indexPair = squarePairMap.getValue(square)
                Pair(indexToCoordinate(indexPair.first), 2 + indexToCoordinate(indexPair.second))
            }
        }.toList()

        var bigAnim = false
        val handler: () -> Unit = {
            pairs.forEach { pair -> drawCounterByCoordinates(pair.first.toDouble(), pair.second.toDouble(), team, if (bigAnim) Big else Little) }
            bigAnim = !bigAnim
            console.log("anim")
        }
        return handler;
    }

    fun drawCounterAnimationFrame(team: Team, lastSquare: Square, currentSquare: Square, frameNumber: Int) {
        val square1Pos: Pair<Int, Int> = positionForAnimation(team, lastSquare)
        val square2Pos: Pair<Int, Int> = positionForAnimation(team, currentSquare)

        val pair = tween(frameNumber.toDouble(), 5.toDouble(), square1Pos, square2Pos)
        console.log("anim of $currentSquare step $frameNumber$pair")

        drawCounterByIndex(pair, team, UrCanvasBoard.CounterSize.Big)
    }

}
