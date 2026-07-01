package com.enviouse.raffletracker.data;

// one raffle task pulled from the raffle tasks chest.
public record RaffleTask(String name, String description, TaskTier tier, boolean completed) {
}
