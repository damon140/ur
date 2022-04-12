package com.damon140.ur;

import java.security.NoSuchAlgorithmException;
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

        // need loop
        System.out.println(board.horizontalFullBoardStrings().stream().collect(Collectors.joining("\r\n")));
        System.out.println("");

        int roll = dice.roll();
        System.out.println("Roll is: " + roll);

        AtomicInteger index = new AtomicInteger(1);
        String moveDesc = ur.askMoves(board.currentTeam(), roll)
                .stream()
                .map(square -> index.getAndIncrement() + " " + square)
                .collect(Collectors.joining("\r\n"));
        System.out.println(moveDesc);

        System.out.println("input: ");
        String name = scanner.nextLine();

        System.out.println("got " + name);
    }

}
