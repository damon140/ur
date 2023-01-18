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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Dice {

    private final SecureRandom random;

    public Dice() throws NoSuchAlgorithmException {
        random = SecureRandom.getInstanceStrong();
    }

    public int roll() {
        return random.ints(4, 0, 2)
                .sum();
    }

}
