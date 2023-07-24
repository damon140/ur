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

class LastMove {

    private var lastChosen: String = ""

    fun hasLastChosen(): Boolean {
        console.log("lastChosen is [" + this.lastChosen + "]")
        console.log("lastChosen length is " + this.lastChosen.length)
        return this.lastChosen.isNotEmpty()
    }

    fun getLastChosen(): String {
        return lastChosen
    }

    fun setLastChosen(newValue: String) {
        // FIXME: migrate this state & fn to new object so we can use in the other view

        this.lastChosen = newValue
        console.log("Last chosen is now " + this.lastChosen)
    }

}