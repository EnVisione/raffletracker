package com.enviouse.raffletracker.data;

/**
 * The three raffle draws in the "Raffle Box", identified by their item. Each carries how often it
 * repeats, in real time, so a captured countdown can be rolled forward to the next real draw
 * instead of relying on game ticks (Hypixel draws on wall clock time, so it keeps counting down
 * even if the game lags, pauses, or crashes).
 */
public enum DrawType {
    /** Gold block, drawn every 2 hours. Raffle Tasks reset when this is drawn. */
    SPEED(2L * 60L * 60L * 1000L),
    /** Diamond block, drawn once a day. */
    DAILY(24L * 60L * 60L * 1000L),
    /** Firework rocket, "The Big One", drawn once at the end of the event so it never repeats. */
    BIG_ONE(0L);

    private final long periodMillis;

    DrawType(long periodMillis) {
        this.periodMillis = periodMillis;
    }

    /** How often this draw repeats in real time, or 0 if it happens only once. */
    public long periodMillis() {
        return periodMillis;
    }
}
