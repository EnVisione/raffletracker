package com.enviouse.raffletracker.render;

import com.enviouse.raffletracker.config.ConfigManager;
import com.enviouse.raffletracker.config.RaffleConfig;
import com.enviouse.raffletracker.data.RaffleData;
import com.enviouse.raffletracker.data.RaffleDraw;
import com.enviouse.raffletracker.data.RaffleTask;
import com.enviouse.raffletracker.data.TaskTier;
import com.enviouse.raffletracker.util.DurationUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// draws the raffle tracker overlay. both the in game hud element and the edit screen use this same
// drawing so it lines up in both. completed tasks are hidden. we only show the ones you still need,
// with the how to text unless you turned it off, plus the live raffle countdowns. when theres no
// data yet the overlay tells you which command to run.
public final class RaffleRenderer {

    private static final int TEXT_FALLBACK = 0xFFFFFFFF; // components carry their own colors
    private static final int EDIT_HINT_BACKGROUND = 0x66000000;
    private static final int EDIT_BORDER = 0xFF55FFFF;
    private static final int PADDING = 2;

    private RaffleRenderer() {
    }

    // draws the tracker at the given gui position.
    // editMode true draws a grab outline, plus a faint fill if no backdrop is on.
    // returns the on screen width and height of the content so the edit screen can do hit testing.
    public static int[] render(GuiGraphicsExtractor ctx, Font font, int x, int y, float scale, boolean editMode) {
        if (scale <= 0f) {
            scale = 1f;
        }
        RaffleConfig config = ConfigManager.get();
        List<Component> lines = buildLines(config.showDescriptions);
        int lineHeight = font.lineHeight + 1;
        int maxWidth = 0;
        for (Component line : lines) {
            maxWidth = Math.max(maxWidth, font.width(line));
        }
        int contentHeight = lines.size() * lineHeight;

        Matrix3x2fStack pose = ctx.pose();
        pose.pushMatrix();
        pose.translate(x, y);
        pose.scale(scale);

        if (config.backgroundEnabled) {
            int alpha = Math.round(config.backgroundOpacity * 255f);
            if (alpha > 0) {
                ctx.fill(-PADDING, -PADDING, maxWidth + PADDING, contentHeight, alpha << 24);
            }
        }
        if (editMode) {
            if (!config.backgroundEnabled) {
                ctx.fill(-PADDING, -PADDING, maxWidth + PADDING, contentHeight, EDIT_HINT_BACKGROUND);
            }
            ctx.outline(-PADDING, -PADDING, maxWidth + PADDING * 2, contentHeight + PADDING, EDIT_BORDER);
        }

        int cursorY = 0;
        for (Component line : lines) {
            ctx.text(font, line, 0, cursorY, TEXT_FALLBACK, true);
            cursorY += lineHeight;
        }

        pose.popMatrix();

        return new int[] {Math.round(maxWidth * scale), Math.round(contentHeight * scale)};
    }

    private static List<Component> buildLines(boolean showDescriptions) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal("Raffle Tracker").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD));
        lines.add(Component.empty());

        addRaffleLines(lines);
        lines.add(Component.empty());
        addTaskLines(lines, showDescriptions);

        return lines;
    }

    private static void addRaffleLines(List<Component> lines) {
        if (!RaffleData.hasDraws()) {
            lines.add(Component.literal("No raffle data").withStyle(ChatFormatting.GRAY));
            lines.add(Component.literal("Run /centuryrafflebox").withStyle(ChatFormatting.YELLOW));
            return;
        }
        lines.add(Component.literal("Raffles:").withStyle(ChatFormatting.GOLD));
        List<RaffleDraw> sorted = new ArrayList<>(RaffleData.draws());
        sorted.sort(Comparator.comparingLong(RaffleData::remainingMillis));
        for (int i = 0; i < sorted.size(); i++) {
            RaffleDraw draw = sorted.get(i);
            String countdown = DurationUtil.format(RaffleData.remainingMillis(draw));
            boolean soonest = i == 0;
            MutableComponent line = Component.literal(" ")
                    .append(Component.literal(draw.name()).withStyle(soonest ? ChatFormatting.GOLD : ChatFormatting.GRAY))
                    .append(Component.literal(": ").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.literal(countdown).withStyle(soonest ? ChatFormatting.YELLOW : ChatFormatting.GRAY));
            lines.add(line);
        }
    }

    private static void addTaskLines(List<Component> lines, boolean showDescriptions) {
        if (!RaffleData.hasTasks()) {
            lines.add(Component.literal("No task data").withStyle(ChatFormatting.GRAY));
            lines.add(Component.literal("Run /centurytasks").withStyle(ChatFormatting.YELLOW));
            return;
        }
        if (RaffleData.tasksStale()) {
            lines.add(Component.literal("Tasks reset").withStyle(ChatFormatting.GRAY));
            lines.add(Component.literal("Run /centurytasks").withStyle(ChatFormatting.YELLOW));
            return;
        }
        List<RaffleTask> tasks = RaffleData.tasks();
        for (TaskTier tier : TaskTier.values()) {
            lines.add(Component.literal(tier.label + " (" + tier.resetLabel + "):")
                    .withStyle(tier.color, ChatFormatting.BOLD));
            boolean anyIncomplete = false;
            for (RaffleTask task : tasks) {
                if (task.tier() == tier && !task.completed()) {
                    anyIncomplete = true;
                    MutableComponent line = Component.literal("  ")
                            .append(Component.literal(task.name()).withStyle(ChatFormatting.WHITE));
                    if (showDescriptions && !task.description().isEmpty()) {
                        line.append(Component.literal(": ").withStyle(ChatFormatting.DARK_GRAY))
                                .append(Component.literal(task.description()).withStyle(ChatFormatting.GRAY));
                    }
                    lines.add(line);
                }
            }
            if (!anyIncomplete) {
                lines.add(Component.literal("  ✔ All complete").withStyle(ChatFormatting.GREEN));
            }
        }
    }
}
