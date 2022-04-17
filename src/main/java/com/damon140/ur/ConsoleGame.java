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
        System.out.println();
        new ConsoleGame().run();
    }

    private final CounterPositions counterPositions;
    private final Ur ur;
    private final Dice dice;
    private final DrawnBoard drawnBoard;
    //private final UrTextPrinter printer;

    public ConsoleGame() throws NoSuchAlgorithmException {
        this.counterPositions = new CounterPositions();
        this.drawnBoard = new DrawnBoard(counterPositions);
        this.ur = new Ur(counterPositions);
        this.dice = new Dice();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            int roll = dice.roll();

            List<String> gameLines = drawnBoard.horizontalFullBoardStrings().stream().collect(Collectors.toCollection(ArrayList::new));

            AtomicInteger index = new AtomicInteger(1);
            Map<CounterPositions.Square, CounterPositions.Square> moves = ur.askMoves(counterPositions.currentTeam(), roll);

            List<String> instructionLines = new ArrayList<>();
            instructionLines.add(counterPositions.currentTeam() + "'s turn roll is: " + roll);
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
                continue; // skip turn
            }

            System.out.print("input: ");
            String input = scanner.next();
            //System.out.println("got " + input);

            if ("x".equals(input)) {
                System.out.println("Done!!");
                return;
            }

            int moveIndex = Integer.parseInt(input);
            CounterPositions.Square fromSquare = moves.keySet().stream().toList().get(moveIndex - 1);

            ur.moveCounter(fromSquare, roll);
        }
    }

    private String getIfPresentOrPad(List<String> arr, int i, int padLength) {
        return i < arr.size()
                ? arr.get(i)
                : " ".repeat(padLength);
    }

}
