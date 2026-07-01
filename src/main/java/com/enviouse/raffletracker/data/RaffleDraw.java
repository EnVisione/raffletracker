package com.enviouse.raffletracker.data;

// one raffle draw pulled from the year 500 incredible raffle box.
// name is the display name like speed raffle 2, untilDrawMillis is the until draw value from when
// we read it, and type says which of the three draws it is.
public record RaffleDraw(String name, long untilDrawMillis, DrawType type) {
}
