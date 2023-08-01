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

package com.damon140.ur

class Ur(private val playArea: PlayArea) {

    // FIXME: Damon attempt to make private, does this tidy code?
    fun currentTeam(): Team {
        return playArea.currentTeam()
    }

    fun skipTurn(): Boolean {
        playArea.swapTeam()
        return true
    }

    enum class MoveResult {
        Illegal, Legal, CounterTaken, CounterOffboard, GameOver
    }

    fun moveCounter(square: Square, count: Int): MoveResult {
        return moveCounter(playArea.currentTeam(), square, count)
    }

    fun moveCounter(team: Team, fromSquare: Square, count: Int): MoveResult {
        // FIXME: check correct team
        if (playArea.allCountersStarted(team)) {
            return MoveResult.Illegal // can't add any more counters
        }
        if (fromSquare !== Square.Off_board_unstarted && playArea.occupied(fromSquare)
            && playArea[fromSquare] !== team) {
            return MoveResult.Illegal // teams counter not on square to move from
        }
        val newSquare: Square = fromSquare.calculateNewSquare(team, count) ?: return MoveResult.Illegal

        if (0 == count) {
            return MoveResult.Illegal // illegal move of zero
        }
        val occupant = playArea[newSquare]

        if (null != occupant) {
            if (team === occupant) {
                return MoveResult.Illegal // clashes with own counter
            }
        }

        // move counter
        playArea.move(fromSquare, newSquare, team)
        if (playArea.allCompleted(team)) {
            return MoveResult.GameOver
        }
        if (newSquare.dontRollAgain()) {
            playArea.swapTeam()
        }
        console.log("after move current team is " + playArea.currentTeam().name)

        return if (null != occupant) {
            MoveResult.CounterTaken
        } else {
            return if (Square.Off_board_finished == newSquare) {
                MoveResult.CounterOffboard
            } else {
                MoveResult.Legal
            }
        }
    }

    fun askMoves(team: Team, roll: Int): Map<Square, Square> {
        val moves: MutableMap<Square, Square> = HashMap()
        if (0 == roll) {
            return moves
        }

        if (!playArea.allStartedOrComplete(team)) {
            // start a new counter
            val v = canUseOrNull(team, Square.Off_board_unstarted.calculateNewSquare(team, roll))
            if (null != v) {
                moves[Square.Off_board_unstarted] = v
            }
        }

        playArea.countersForTeam(team)
            .forEach { startSquare ->
                val maybeEndSquare = startSquare.calculateNewSquare(team, roll)
                val endSquare = canUseOrNull(team, maybeEndSquare)
                if (null != endSquare) {
                    moves[startSquare] = endSquare
                }
            }

        return moves
    }

    private fun canUseOrNull(team: Team, square: Square?): Square? {
        if (null == square) {
            return null
        }
        // if empty
        if (!playArea.occupied(square)) {
            return square
        }
        // or other counter and not a safe square
        val occupantTeam = playArea[square]
        val otherTeamsSquare = occupantTeam != null && occupantTeam != team
        val notASafeSquare = !square.isSafeSquare
        return if (otherTeamsSquare && notASafeSquare)
            square else null
    }
}

