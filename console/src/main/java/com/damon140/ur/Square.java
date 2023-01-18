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

import java.util.Optional;
import java.util.Set;

public enum Square {
    off_board_unstarted, // synthetic square, not a square on the board

    black_run_on_1,
    black_run_on_2,
    black_run_on_3,
    black_run_on_4,

    black_run_off_1,
    black_run_off_2,

    shared_1,
    shared_2,
    shared_3,
    shared_4,
    shared_5,
    shared_6,
    shared_7,
    shared_8,

    white_run_on_1,
    white_run_on_2,
    white_run_on_3,
    white_run_on_4,

    white_run_off_1,
    white_run_off_2,

    off_board_finished, // synthetic
    ;

    public Square calculateNewSquare(Team team) {
        return switch (this) {
            case black_run_on_4, white_run_on_4 -> Square.shared_1;
            case shared_8 -> team == Team.black ? Square.black_run_off_1 : Square.white_run_off_1;
            case black_run_off_2, white_run_off_2 -> Square.off_board_finished;
            case off_board_unstarted -> team == Team.black ? Square.black_run_on_1 : Square.white_run_on_1;
            default -> Square.values()[1 + this.ordinal()];
        };
    }

    // TODO: switch to new illegal_sqaure square instead of opt
    public Optional<Square> calculateNewSquare(Team team, int count) {
        if (this == Square.off_board_finished) {
            return Optional.of(Square.off_board_finished);
        }
        Square newSquare = this;
        for (int looper = 0; looper < count; looper++) {
            if (Square.off_board_finished == newSquare) {
                return Optional.empty();
            }
            newSquare = newSquare.calculateNewSquare(team);
        }
        return Optional.of(newSquare);
    }

    public boolean dontRollAgain() {
        return !rollAgain();
    }

    public boolean isSafeSquare() {
        return shared_4 == this;
    }

    public boolean rollAgain() {
        return Set.of(black_run_on_4, white_run_on_4, shared_4, black_run_off_2, white_run_off_2).contains(this);
    }
}
