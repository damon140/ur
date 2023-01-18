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

import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Scanner;

@RequiredArgsConstructor
public class PlayerSetup {

    private final Scanner scanner;

    @RequiredArgsConstructor
    private class ConsoleSupplier implements MoveSupplier {
        private final Scanner scanner;

        @Override
        public String choose(Map<Square, Square> moves) {
            System.out.print("input: ");
            return scanner.next();
        }

    }

    private class BadAi1 implements MoveSupplier {
        @Override
        public String choose(Map<Square, Square> unused) {
            // always take the first move
            return "1";
        }
    }

    public interface MoveSupplier {
        String choose(Map<Square, Square> moves);
    }

    public MoveSupplier getPlayer(Team team) {
        System.out.println("Player for " + team + " will be");
        System.out.println("1 human via console");
        System.out.println("2 computer bad ai 1");

        return switch (scanner.next()) {
            case "1" -> new ConsoleSupplier(scanner);
            case "2" -> new BadAi1();
            default -> null;
        };
    }

}
