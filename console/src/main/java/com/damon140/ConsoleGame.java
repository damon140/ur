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

package com.damon140;

import com.damon140.ur.*;
import com.damon140.ur.PlayerSetup.MoveSupplier;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.damon140.ur.Team.black;
import static com.damon140.ur.Team.white;

public class ConsoleGame {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println("Command line Ur");
        new ConsoleGame().run();
    }

    private final Scanner scanner;
    private final PlayArea playArea;
    private final Ur ur;
    private final Dice dice;
    private final HorizontalDrawnBoard horizontalDrawnBoard;
    private final PlayerSetup playerSetup;

    public ConsoleGame() throws NoSuchAlgorithmException {
        this.playArea = new PlayArea();
        this.horizontalDrawnBoard = new HorizontalDrawnBoard(playArea);
        this.ur = new Ur(playArea);
        this.dice = new Dice();

        this.scanner = new Scanner(System.in);
        this.playerSetup = new PlayerSetup(scanner);
    }

    public void run() {
        Map<Team, MoveSupplier> moveSuppliers = Map.of(
                white, playerSetup.getPlayer(white),
                black, playerSetup.getPlayer(black));

        while (true) {
            System.out.println();

            int roll = dice.roll();

            List<String> gameLines = horizontalDrawnBoard.fullBoard().stream().collect(Collectors.toCollection(ArrayList::new));

            AtomicInteger index = new AtomicInteger(1);
            Map<Square, Square> moves = ur.askMoves(playArea.currentTeam(), roll);

            List<String> instructionLines = new ArrayList<>();
            instructionLines.add(playArea.currentTeam() + " rolls " + roll);
            moves.entrySet()
                    .stream()
                    .map(entry -> index.getAndIncrement() + " " + entry.getKey() + " -> " + entry.getValue())
                    .collect(Collectors.toCollection(() -> instructionLines));

            instructionLines.add("x - quit ");
            instructionLines.add("");

            int maxGameLineLength =  gameLines.stream().mapToInt(s -> s.length()).max().getAsInt();

            String gameTurn = IntStream.range(0, Math.max(gameLines.size(), instructionLines.size()))
                    .mapToObj(i -> {
                        String gamePart = getIfPresentOrPad(gameLines, i, maxGameLineLength);
                        String instructionPart = getIfPresentOrPad(instructionLines, i, maxGameLineLength);

                        return gamePart + "      " + instructionPart;
                    })
                    .collect(Collectors.joining("\r\n"));
            System.out.println(gameTurn);

            if (0 == roll) {
                ur.skipTurn(roll);
                continue; // skip turn due to zero
            }

            if (moves.isEmpty()) {
                continue; // all moves blocked
            }

            // some player moves a counter
            String input = moveSuppliers.get(ur.currentTeam()).choose(moves);

            if ("x".equals(input)) {
                System.out.println("Done!!");
                return;
            }

            int moveIndex = Integer.parseInt(input);
            Square fromSquare = moves.keySet().stream().toList().get(moveIndex - 1);

            var result = ur.moveCounter(fromSquare, roll);
            if (result == Ur.MoveResult.gameOver) {
                System.out.println("Game won by " + playArea.currentTeam());
                break;
            }
        }
    }

    private String getIfPresentOrPad(List<String> arr, int i, int padLength) {
        return i < arr.size()
                ? arr.get(i)
                : " ".repeat(padLength);
    }

}
