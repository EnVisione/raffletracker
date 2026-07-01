package com.enviouse.raffletracker.data;

import java.util.ArrayList;
import java.util.List;

// holds the latest raffle state we parsed. written on the client tick thread when you open the
// chests, read on the render thread. the lists get swapped out in one go and the fields are
// volatile so we dont need any locking.
//
// everything runs off real clock time, never game ticks, so the countdowns and resets stay right
// even if the game lags or pauses. we keep it in memory for the whole session so it sticks around
// when you hop servers.
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

    // marks the named task done so it drops straight off the tracker. we call this when the chat
    // line about finishing a task shows up. returns true if something actually changed.
    public static boolean markCompleted(String taskName) {
        List<RaffleTask> current = tasks;
        if (current.isEmpty()) {
            return false;
        }
        String wanted = taskName.trim();
        List<RaffleTask> updated = new ArrayList<>(current.size());
        boolean changed = false;
        for (RaffleTask task : current) {
            if (!task.completed() && task.name().equalsIgnoreCase(wanted)) {
                updated.add(new RaffleTask(task.name(), task.description(), task.tier(), true));
                changed = true;
            } else {
                updated.add(task);
            }
        }
        if (changed) {
            tasks = List.copyOf(updated);
        }
        return changed;
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

    // how many millis are left until the next real draw. for repeating draws we roll the saved
    // target forward by whole periods so it always points at the next one, straight off clock time.
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

    // the speed raffle draw, or null if we havent read the raffle box yet.
    public static RaffleDraw speedDraw() {
        for (RaffleDraw draw : draws) {
            if (draw.type() == DrawType.SPEED) {
                return draw;
            }
        }
        return null;
    }

    // tells us if the loaded tasks are stale, meaning a speed raffle drew since we read them. tasks
    // reset on every speed raffle draw. this runs off clock time so no ticking is needed. returns
    // false if we cant tell yet, like when the raffle box hasnt been read.
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
        // the first speed raffle draw at or after the moment we grabbed the tasks.
        long target = drawsCapturedAt + speed.untilDrawMillis();
        while (target <= tasksCapturedAt) {
            target += period;
        }
        return System.currentTimeMillis() >= target;
    }
}
