package com.damon140.ur;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static com.damon140.ur.Team.white;

@AllArgsConstructor
public class UrTextPrinter {

    public static String drawVerticle(HorizontalDrawnBoard board) {
        List<List<HorizontalDrawnBoard.BoardPart>> parts = board.verticalBoard();
        return "";
    }

    public static String join(String... stringSquaresArr) {
        List<List<String>> stringSquares = Arrays.stream(stringSquaresArr)
                .map(s -> List.of(s.split("\n")))
                .collect(Collectors.toList());

        // join by row
        // join adjacent edges
        StringJoiner joiner = new StringJoiner("\n");
        int bound = stringSquares.get(0).size();
        for (int j = 0; j < bound; j++) {
            int i = j;
            String s = stringSquares.stream()
                    .map(l -> l.get(i))
                    .collect(Collectors.joining(""));
            String replaceAll = s.replaceAll("\\|\\|", "\\|");
            joiner.add(replaceAll);
        }
        return joiner.toString();
    }

    public static String square(Team team) {
        String variable = team == white ? "w" : "b";
        return square(variable);
    }

    public static String square() {
        return square(" ");
    }

    public static String blank() {
        return "   \n".repeat(3);
    }

    private static String square(String variable) {
        return """
                |---|
                | %s | 
                |---|""".formatted(variable);
    }
}
