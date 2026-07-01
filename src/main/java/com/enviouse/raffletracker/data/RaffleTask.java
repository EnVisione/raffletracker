package com.enviouse.raffletracker.data;

/** A single Raffle Task parsed from the "Raffle Tasks" chest. */
public record RaffleTask(String name, String description, TaskTier tier, boolean completed) {
}
