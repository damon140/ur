package com.damon140.ur;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Dice {

    private final SecureRandom random;

    public Dice() throws NoSuchAlgorithmException {
        random = SecureRandom.getInstanceStrong();
    }

    public int roll() {
        return random.ints(4, 0, 2)
                .sum();
    }

}
