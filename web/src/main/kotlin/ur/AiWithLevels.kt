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

package ur

import com.damon140.ur.PlayArea
import com.damon140.ur.Square
import com.damon140.ur.Team
import web.PlayerSetup

class AiWithLevels(private val playArea: PlayArea) : PlayerSetup.InputSupplier {

    override fun waitForPlayer(): Boolean {
        return false
    }

    override fun choose(level: Int, moves: Map<Square, Square>): Int {
        val first = moves.entries
            .sortedWith(AiCompare(playArea, level))
            .first()
        console.log("AI best move is $first given level $level")

        return 1 + moves.keys.indexOf(first.key)
    }

    override fun isHuman(): Boolean {
        return false
    }

    class AiCompare(private val playArea: PlayArea, private val level: Int): Comparator<Map.Entry<Square, Square>> {

        override fun compare(a: Map.Entry<Square, Square>, b: Map.Entry<Square, Square>): Int {
            return score(b.key, b.value) - score(a.key, a.value)
        }

        private fun score(fromSquare: Square, toSquare: Square) : Int {
            // FIXME: need a better rule here
            if (level > 3 && fromSquare.isSafeSquare && fromSquare.rollAgain()) {
                return -100 // don't move great piece
            }
            if (level > 2 && playArea.moveIsOnShareRace(Team.Black, fromSquare, toSquare)) {
                return 3
            }
            if (level > 1 && playArea.moveTakes(Team.Black, fromSquare, toSquare)) {
                return 2
            }
            if (level > 0 && toSquare.rollAgain()) {
                return 1
            }

            return 0
        }
    }

}