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

package com.damon140.ur;

public enum Team {
    white, black;

    public final String ch;

    Team() {
        ch = this.name().substring(0, 1);
    }

    public Team other() {
        return Team.values()[(this.ordinal() + 1) % 2];
    }

    public static boolean isTeamChar(String value) {
        return value.equals(white.ch) || value.equals(black.ch);
    }

    public static Team fromCh(String value) {
        return white.ch.equals(value) ? white : black;
    }
}
