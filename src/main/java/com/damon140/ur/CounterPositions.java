package com.damon140.ur;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CounterPositions {

    private static final int COUNTERS_PER_PLAYER = 7;
    private static final String COUNTER_START_SEPARATOR = "|";
    private static final String COUNTER_START_SEPARATOR_PATTERN = Pattern.quote(COUNTER_START_SEPARATOR);

    private final Map<Square, Team> counters;

    private final Map<Team, Integer> completedCounters;

    private Team currentTeam = Team.white; // white starts

    public CounterPositions() throws NoSuchAlgorithmException {
        counters = new HashMap<>();
        completedCounters = new TreeMap<>();
        completedCounters.put(Team.black, 0);
        completedCounters.put(Team.white, 0);
    }

    // FIXME: Damon needs to be split
    public CounterPositions(String game) {
        // parse board
        Deque<String> deque = Arrays.stream(game.split("\n")).collect(Collectors.toCollection(ArrayDeque::new));
        String whiteLine = deque.removeFirst();
        String blackLine = deque.removeLast();
        completedCounters = new TreeMap<>();
        completedCounters.put(Team.black, completedCountersFromString(blackLine));
        completedCounters.put(Team.white, completedCountersFromString(whiteLine));

        // TODO: tidy & shrink
        List<Square> topBoard = Arrays.stream(DrawnBoard.HORIZONTAL_BOARD[0]).toList();
        String topHozRow = deque.removeFirst();
        List<Square> midBoard = Arrays.stream(DrawnBoard.HORIZONTAL_BOARD[1]).toList();
        String midHozRow = deque.removeFirst();
        List<Square> botBoard = Arrays.stream(DrawnBoard.HORIZONTAL_BOARD[2]).toList();
        String botHozRow = deque.removeFirst();

        counters = new HashMap<>();
        extracted(topBoard, topHozRow, counters);
        extracted(midBoard, midHozRow, counters);
        extracted(botBoard, botHozRow, counters);
    }

    private void extracted(List<Square> maybeSparseBoard, String row, Map<Square, Team> target) {
        List<Square> boardRow = maybeSparseBoard.stream().filter(Objects::nonNull).toList();
        List<String> chars = row.chars().mapToObj(c -> Character.toString(c))
                .filter(c -> {
                    boolean equals = DrawnBoard.BoardPart.space.ch.equals(c);
                    return !equals;
                })
                .toList();
        Map<Square, String> topRow = zipToMap(boardRow, chars);
        topRow.entrySet().stream()
                .filter(e -> Team.isTeamChar(e.getValue()))
                .forEach(e -> {
                    target.put(e.getKey(), Team.fromCh(e.getValue()));
                });
    }

    public static <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
        return IntStream.range(0, keys.size()).boxed()
                .collect(Collectors.toMap(keys::get, values::get));
    }

    private int completedCountersFromString(String countersString) {
        ArrayDeque<String> deque = Arrays.stream(countersString.replaceAll(" ", "").split(COUNTER_START_SEPARATOR_PATTERN))
                .collect(Collectors.toCollection(ArrayDeque::new));

        if (1 == deque.size()) {
            // no completed counters as only unstarted counters
            return 0;
        }

        return deque.getLast().length();

    }

    public String countersHorizontal(Team team) {
        int completed = completedCounters.get(team);
        int unstarted = COUNTERS_PER_PLAYER - (int) counters.values().stream().filter(t -> team == t).count() - completed;
        int padding = 1 + COUNTERS_PER_PLAYER - completed - unstarted;

        String teamCh = team.ch;
        return teamCh.repeat(unstarted) + " ".repeat(padding-1) + COUNTER_START_SEPARATOR + teamCh.repeat(completed);
    }

    public Set<String> state() {
        return new TreeSet<>(List.of(
                "counters: " + counters.toString(),
                "completedCounters: " + completedCounters.toString(),
                "currentTeam: " + currentTeam.toString()
        ));
    }

    public int completedCount(Team team) {
        return completedCounters.get(team);
    }

    // FIXME: push parts of this into Square class
    // TODO: switch to new illegal_sqaure square instead of opt
    public static Optional<Square> calculateNewSquare(Team team, Square square, int count) {
        // TODO: simplify
        if (square == Square.off_board_finished) {
            return Optional.of(Square.off_board_finished);
        }
        Square newSquare = square;
        for (int looper = 0; looper < count; looper++) {
            if (Square.off_board_finished == newSquare) {
                return Optional.empty();
            }
            newSquare = calculateNewSquare(team, newSquare);
        }
        return Optional.of(newSquare);
    }

    public Map<Square, Team> getCounters() {
        return this.counters;
    }

    public Team currentTeam() {
        return this.currentTeam;
    }

    public void swapTeam() {
        this.currentTeam = this.currentTeam.other();
    }

    public boolean allCountersStarted(Team team) {
        return COUNTERS_PER_PLAYER == completedCounters.get(team);
    }

    public Map<Team, Integer> getCompletedCounters() {
        return completedCounters;
    }

    // FIXME: Damon, this is counting wrong
    public int unstartedCount(Team team) {
        long inProgressCounters = this.counters.entrySet()
                .stream()
                .filter(e -> team == e.getValue())
                .count();
        int finishedCounters = this.completedCounters.get(team);

        return COUNTERS_PER_PLAYER - finishedCounters - (int) inProgressCounters;
    }

    public boolean allStartedOrComplete(Team team) {
        int completed = this.completedCount(team);
        int inProgress = this.counters.size();
        return COUNTERS_PER_PLAYER == completed + inProgress;
    }

    public enum Square {
        off_board_unstarted,

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

        off_board_finished,
        ;

        // FIXME: make non static
        public static Square calculateNewSquare(Team team, Square square) {
            return switch (square) {
                case black_run_on_4, white_run_on_4 -> Square.shared_1;
                case shared_8 -> team == Team.black ? Square.black_run_off_1 : Square.white_run_off_1;
                case black_run_off_2, white_run_off_2 -> Square.off_board_finished;
                case off_board_unstarted -> team == Team.black ? Square.black_run_on_1 : Square.white_run_on_1;
                default -> Square.values()[1 + square.ordinal()];
            };
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

}
