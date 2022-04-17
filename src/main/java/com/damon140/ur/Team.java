package com.damon140.ur;

// TODO: light & dark as less racist names??
public enum Team {
    white, black;

    public final String ch;

    Team() {
        ch = this.name().substring(0, 1);
    }

    public Team other() {
        return Team.values()[(this.ordinal() + 1) % 2];
    }

    public static boolean isTeamChar(String value) {
        return value.equals(white.ch) || value.equals(black.ch);
    }

    public static Team fromCh(String value) {
        return white.ch.equals(value) ? white : black;
    }
}
