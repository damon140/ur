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

class Dice {
    private var lastValue = 0
    private var lastString = ""

    fun roll() {
        val d1 = (0..1).random()
        val d2 = (0..1).random()
        val d3 = (0..1).random()
        val d4 = (0..1).random()

        this.lastValue = d1 + d2 + d3 + d4
        this.lastString = ("" + d1) + ("+" + d2) + ("+" + d3) + ("+" + d4)
    }
    fun getLastValue(): Int {
        return lastValue
    }

    fun getLastString(): String {
        return lastString
    }
}