package com.damon140.ur;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Game {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println("Command line Ur");
        System.out.println();
        new Game().run();
    }

    private final Board board;
    private final Ur ur;
    private final Dice dice;
    //private final UrTextPrinter printer;

    public Game() throws NoSuchAlgorithmException {
        this.board = new Board();
        this.ur = new Ur(board);
        this.dice = new Dice();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            int roll = dice.roll();

            List<String> gameLines = board.horizontalFullBoardStrings().stream().collect(Collectors.toCollection(ArrayList::new));

            AtomicInteger index = new AtomicInteger(1);
            Map<Board.Square, Board.Square> moves = ur.askMoves(board.currentTeam(), roll);

            List<String> instructionLines = new ArrayList<>();
            instructionLines.add(board.currentTeam() + "'s turn roll is: " + roll);
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
            Board.Square fromSquare = moves.keySet().stream().toList().get(moveIndex - 1);

            ur.moveCounter(fromSquare, roll);
        }
    }

    private String getIfPresentOrPad(List<String> arr, int i, int padLength) {
        return i < arr.size()
                ? arr.get(i)
                : " ".repeat(padLength);
    }

}
