package com.enviouse.raffletracker.data;

/**
 * A raffle draw parsed from the "Year 500 Incredible Raffle Box".
 *
 * @param name             the display name, e.g. "Speed Raffle #2"
 * @param untilDrawMillis  the "Until draw" value as it was when captured
 * @param type             which of the three draws this is
 */
public record RaffleDraw(String name, long untilDrawMillis, DrawType type) {
}
