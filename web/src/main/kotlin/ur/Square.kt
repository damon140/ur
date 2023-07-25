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

enum class Square {
    Off_board_unstarted,  // synthetic square, not a square on the board
    Black_run_on_1, Black_run_on_2, Black_run_on_3, Black_run_on_4, Black_run_off_1, Black_run_off_2,
    Shared_1, Shared_2, Shared_3, Shared_4, Shared_5, Shared_6, Shared_7, Shared_8,
    White_run_on_1, White_run_on_2, White_run_on_3, White_run_on_4, White_run_off_1, White_run_off_2, Off_board_finished;

    companion object {
        fun drawableSquares(): List<Square> {
            return Square.values().toList().filter { v -> v != Off_board_finished && v!= Off_board_unstarted }
        }

        // FIXME: Damon unit test
        fun calculateSquaresBetween(team: Team, startSquare: Square, endSquare: Square): MutableList<Square> {
            var currentSquare = startSquare
            val squares = ArrayList<Square>()
            squares.add(startSquare)

            while(currentSquare != endSquare && currentSquare != Off_board_finished) {
                currentSquare = currentSquare.calculateNewSquare(team)
                squares.add(currentSquare)
            }
            return squares
        }
    }

    // TODO: switch to new illegal_square square instead of opt??
    fun calculateNewSquare(team: Team, count: Int): Square? {
        if (this == Off_board_finished) {
            return Off_board_finished
        }
        var newSquare = this
        for (looper in 0 until count) {
            if (Off_board_finished == newSquare) {
                return null
            }
            newSquare = newSquare.calculateNewSquare(team)
        }
        return newSquare
    }

    // synthetic
    fun calculateNewSquare(team: Team): Square {
        return when (this) {
            Black_run_on_4, White_run_on_4 -> Shared_1
            Shared_8 -> if (team === Team.Black) Black_run_off_1 else White_run_off_1
            Black_run_off_2, White_run_off_2 -> Off_board_finished
            Off_board_unstarted -> if (team === Team.Black) Black_run_on_1 else White_run_on_1
            else -> values()[1 + ordinal]
        }
    }

    fun dontRollAgain(): Boolean {
        return !rollAgain()
    }

    val isSafeSquare: Boolean
        get() = Shared_4 == this

    fun rollAgain(): Boolean {
        return setOf(Black_run_on_4, White_run_on_4, Shared_4, Black_run_off_2, White_run_off_2)
            .contains(this)
    }

    fun isRaceSquare(): Boolean {
        return setOf(Shared_1, Shared_2, Shared_3, Shared_4, Shared_5, Shared_6, Shared_7, Shared_8)
            .contains(this)
    }
}