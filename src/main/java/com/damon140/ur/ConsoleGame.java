package com.damon140.ur;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConsoleGame {


    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println("Command line Ur");
        new ConsoleGame().run();
    }

    private final Counters counters;
    private final Ur ur;
    private final Dice dice;
    private final HorizontalDrawnBoard horizontalDrawnBoard;
    private final PlayerSetup playerSetup;

    public ConsoleGame() throws NoSuchAlgorithmException {
        this.counters = new Counters();
        this.horizontalDrawnBoard = new HorizontalDrawnBoard(counters);
        this.ur = new Ur(counters);
        this.dice = new Dice();

        this.scanner = new Scanner(System.in);
        this.playerSetup = new PlayerSetup(scanner);
    }

    public void run() {
        Map<Team, MoveSupplier> moveSuppliers = Map.of(
                white, playerSetup.getPlayer(white),
                black, playerSetup.getPlayer(black));

        // need a play method

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
                continue; // skip turn
            }

            // some player moves a counter
            String input = moveSuppliers.get(ur.currentTeam()).choose(moves);

            if ("x".equals(input)) {
                System.out.println("Done!!");
                return;
            }

            int moveIndex = Integer.parseInt(input);
            Square fromSquare = moves.keySet().stream().toList().get(moveIndex - 1);

            // FIXME: Damon need game won detection here
            boolean result = ur.moveCounter(fromSquare, roll);
        }
    }

    private String getIfPresentOrPad(List<String> arr, int i, int padLength) {
        return i < arr.size()
                ? arr.get(i)
                : " ".repeat(padLength);
    }

}
