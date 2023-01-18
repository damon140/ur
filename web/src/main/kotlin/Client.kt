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


import kotlinx.browser.window
import web.WebGame

fun main() {
    window.onload = {
        val webGame = WebGame()

//        webGame.fakeGame("""
//            |wwwwww
//            *...  w.
//            ...*....
//            *..b  *.
//            b|bbbbb""".trimIndent())

        webGame.playPart1()
    }
}
