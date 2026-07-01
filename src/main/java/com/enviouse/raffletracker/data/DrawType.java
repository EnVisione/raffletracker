package com.enviouse.raffletracker.data;

// the three raffle draws in the raffle box, told apart by their item. each one knows how often it
// repeats in real time, so a saved countdown can roll forward to the next draw instead of leaning
// on game ticks. hypixel runs on real clock time so it keeps counting even if the game lags,
// pauses, or crashes.
public enum DrawType {
    // gold block, drawn every 2 hours. the raffle tasks reset when this one draws.
    SPEED(2L * 60L * 60L * 1000L),
    // diamond block, drawn once a day.
    DAILY(24L * 60L * 60L * 1000L),
    // firework rocket, the big one, drawn once at the end of the event so it never repeats.
    BIG_ONE(0L);

    private final long periodMillis;

    DrawType(long periodMillis) {
        this.periodMillis = periodMillis;
    }

    // how often this draw repeats in real time, or 0 if it only happens once.
    public long periodMillis() {
        return periodMillis;
    }
}
