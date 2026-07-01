package com.enviouse.raffletracker.data;

import net.minecraft.ChatFormatting;

/**
 * The three Raffle Task tiers, keyed by the item type Hypixel uses for them in the
 * "Raffle Tasks" chest, plus how often each tier resets.
 */
public enum TaskTier {
    EASY("Easy", "2h", ChatFormatting.GREEN),
    MEDIUM("Medium", "24h", ChatFormatting.YELLOW),
    HARD("Hard", "event", ChatFormatting.RED);

    public final String label;
    public final String resetLabel;
    public final ChatFormatting color;

    TaskTier(String label, String resetLabel, ChatFormatting color) {
        this.label = label;
        this.resetLabel = resetLabel;
        this.color = color;
    }
}
