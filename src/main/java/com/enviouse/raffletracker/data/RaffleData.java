package com.enviouse.raffletracker.data;

import java.util.List;

/**
 * Holds the most recently parsed raffle state. Written from the client tick thread when the
 * player opens the relevant chests, read from the render thread. Lists are replaced atomically
 * and fields are volatile so no locking is required.
 *
 * <p>Everything is derived from real wall clock time ({@link System#currentTimeMillis()}), never
 * from game ticks, so countdowns and resets stay correct even if the game lags or pauses. State is
 * kept in memory for the whole game session, so it survives server hops.
 */
public final class RaffleData {

    private static volatile List<RaffleTask> tasks = List.of();
    private static volatile long tasksCapturedAt = 0L;
    private static volatile List<RaffleDraw> draws = List.of();
    private static volatile long drawsCapturedAt = 0L;

    private RaffleData() {
    }

    public static void setTasks(List<RaffleTask> newTasks) {
        tasks = List.copyOf(newTasks);
        tasksCapturedAt = System.currentTimeMillis();
    }

    public static List<RaffleTask> tasks() {
        return tasks;
    }

    public static boolean hasTasks() {
        return !tasks.isEmpty();
    }

    public static void setDraws(List<RaffleDraw> newDraws) {
        draws = List.copyOf(newDraws);
        drawsCapturedAt = System.currentTimeMillis();
    }

    public static List<RaffleDraw> draws() {
        return draws;
    }

    public static boolean hasDraws() {
        return !draws.isEmpty();
    }

    /**
     * Milliseconds remaining until the next real draw. For repeating draws the captured target time
     * is rolled forward by whole periods so the countdown keeps pointing at the next real draw,
     * purely from wall clock time.
     */
    public static long remainingMillis(RaffleDraw draw) {
        long now = System.currentTimeMillis();
        long target = drawsCapturedAt + draw.untilDrawMillis();
        long period = draw.type().periodMillis();
        if (period > 0L) {
            while (target <= now) {
                target += period;
            }
        }
        return target - now;
    }

    /** The Speed Raffle draw, or {@code null} if the raffle box hasn't been read yet. */
    public static RaffleDraw speedDraw() {
        for (RaffleDraw draw : draws) {
            if (draw.type() == DrawType.SPEED) {
                return draw;
            }
        }
        return null;
    }

    /**
     * Whether the loaded tasks are stale, meaning a Speed Raffle has been drawn since we read them
     * (tasks reset on every Speed Raffle draw). Worked out from wall clock time, so no ticking is
     * needed. Returns false if we cannot tell yet, for example when the raffle box has not been read.
     */
    public static boolean tasksStale() {
        if (tasks.isEmpty()) {
            return false;
        }
        RaffleDraw speed = speedDraw();
        if (speed == null) {
            return false;
        }
        long period = speed.type().periodMillis();
        if (period <= 0L) {
            return false;
        }
        // The first Speed Raffle draw at or after the moment we captured the tasks.
        long target = drawsCapturedAt + speed.untilDrawMillis();
        while (target <= tasksCapturedAt) {
            target += period;
        }
        return System.currentTimeMillis() >= target;
    }
}
