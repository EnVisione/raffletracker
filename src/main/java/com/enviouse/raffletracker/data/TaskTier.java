package com.enviouse.raffletracker.data;

import net.minecraft.ChatFormatting;

// the three task tiers. we tell them apart by the item hypixel uses for them in the raffle tasks
// chest, and each tier knows how often it resets.
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
