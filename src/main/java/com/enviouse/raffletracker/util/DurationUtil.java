package com.enviouse.raffletracker.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// parses and formats the readable durations hypixel uses, like 20h 11m 6s or 7 days.
public final class DurationUtil {

    private static final Pattern TOKEN =
            Pattern.compile("(\\d+)\\s*(days?|d|hours?|h|minutes?|mins?|m|seconds?|secs?|s)");

    private DurationUtil() {
    }

    // turns a string like 20h 11m 6s or 7 days into millis. returns 0 if nothing matched.
    public static long parse(String text) {
        if (text == null) {
            return 0L;
        }
        long total = 0L;
        Matcher m = TOKEN.matcher(text.toLowerCase());
        while (m.find()) {
            long value = Long.parseLong(m.group(1));
            char unit = m.group(2).charAt(0);
            total += switch (unit) {
                case 'd' -> value * 86_400_000L;
                case 'h' -> value * 3_600_000L;
                case 'm' -> value * 60_000L;
                default -> value * 1_000L; // seconds
            };
        }
        return total;
    }

    // formats millis into a short countdown like 2d 4h or 11m 6s, or ready when its run out.
    public static String format(long millis) {
        if (millis <= 0L) {
            return "Ready!";
        }
        long seconds = millis / 1000L;
        long days = seconds / 86_400L;
        seconds %= 86_400L;
        long hours = seconds / 3_600L;
        seconds %= 3_600L;
        long minutes = seconds / 60L;
        seconds %= 60L;

        if (days > 0) {
            return days + "d " + hours + "h";
        }
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        if (minutes > 0) {
            return minutes + "m " + seconds + "s";
        }
        return seconds + "s";
    }
}
