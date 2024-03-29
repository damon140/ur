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

enum class Team {
    White, Black;

    val ch: String = name.substring(0, 1).lowercase()

    fun other(): Team {
        return values()[(ordinal + 1) % 2]
    }

    companion object {
        fun isTeamChar(value: String): Boolean {
            return value == White.ch || value == Black.ch
        }

        fun fromCh(value: String): Team {
            return if (White.ch == value) White else Black
        }

        fun random(): Team {
            return values()[(0..1).random()]
        }
    }
}