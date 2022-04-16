package com.damon140.ur;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Game {


    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println("Hello world!!");
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
            // need loop
            System.out.println(board.horizontalFullBoardStrings().stream().collect(Collectors.joining("\r\n")));
            System.out.println(board.currentTeam() + "'s turn");
            System.out.println("");

            int roll = dice.roll();
            System.out.println("Roll is: " + roll);

            AtomicInteger index = new AtomicInteger(1);
            Map<Board.Square, Board.Square> moves = ur.askMoves(board.currentTeam(), roll);
            String moveDesc = moves.entrySet()
                    .stream()
                    .map(entry -> index.getAndIncrement() + " " + entry.getKey() + " -> " + entry.getValue())
                    .collect(Collectors.joining("\r\n"));
            System.out.println(moveDesc);
            System.out.println("x - quit ");
            System.out.println("");
            System.out.println("input: ");
            String input = scanner.nextLine();

            System.out.println("got " + input);

            if ("x".equals(input)) {
                System.out.println("Done!!");
                return;
            }

            int moveIndex = Integer.parseInt(input.trim());
            Board.Square fromSquare = moves.keySet().stream().toList().get(moveIndex - 1);

            ur.moveCounter(fromSquare, roll);
        }
    }

}
