package com.enviouse.raffletracker.parse;

import com.enviouse.raffletracker.data.DrawType;
import com.enviouse.raffletracker.data.RaffleDraw;
import com.enviouse.raffletracker.data.RaffleTask;
import com.enviouse.raffletracker.data.TaskTier;
import com.enviouse.raffletracker.util.DurationUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the two Century Celebration chests into {@link RaffleTask}s and {@link RaffleDraw}s.
 *
 * <p>Only the top container is read (the trailing 36 slots are always the player inventory), so
 * paper/maps the player happens to be carrying are never mistaken for tasks.
 */
public final class ContainerParser {

    public static final String TASKS_TITLE = "Raffle Tasks";
    public static final String BOX_TITLE_MARKER = "Raffle Box";

    private static final String UNTIL_DRAW_PREFIX = "Until draw:";

    private ContainerParser() {
    }

    /** Reads the "Raffle Tasks" chest. Returns an empty list if the contents haven't loaded yet. */
    public static List<RaffleTask> parseTasks(AbstractContainerMenu menu) {
        List<RaffleTask> tasks = new ArrayList<>();
        for (ItemStack stack : topContainerItems(menu)) {
            TaskTier tier = tierFor(stack.getItem());
            if (tier == null) {
                continue;
            }
            List<String> lore = loreLines(stack);
            boolean completed = lore.contains("COMPLETE");
            if (!completed && !lore.contains("INCOMPLETE")) {
                continue; // not a task item
            }
            String name = stack.getHoverName().getString().trim();
            String description = extractDescription(lore);
            tasks.add(new RaffleTask(name, description, tier, completed));
        }
        return tasks;
    }

    /** Reads the "Year 500 Incredible Raffle Box". Returns an empty list if not loaded yet. */
    public static List<RaffleDraw> parseDraws(AbstractContainerMenu menu) {
        List<RaffleDraw> draws = new ArrayList<>();
        for (ItemStack stack : topContainerItems(menu)) {
            DrawType type = drawTypeFor(stack.getItem());
            if (type == null) {
                continue;
            }
            long untilDraw = -1L;
            for (String line : loreLines(stack)) {
                String trimmed = line.trim();
                if (trimmed.startsWith(UNTIL_DRAW_PREFIX)) {
                    untilDraw = DurationUtil.parse(trimmed.substring(UNTIL_DRAW_PREFIX.length()));
                    break;
                }
            }
            if (untilDraw < 0L) {
                continue; // not a raffle draw item
            }
            String name = stack.getHoverName().getString().trim();
            draws.add(new RaffleDraw(name, untilDraw, type));
        }
        return draws;
    }

    private static DrawType drawTypeFor(Item item) {
        if (item == Items.GOLD_BLOCK) {
            return DrawType.SPEED;
        }
        if (item == Items.DIAMOND_BLOCK) {
            return DrawType.DAILY;
        }
        if (item == Items.FIREWORK_ROCKET) {
            return DrawType.BIG_ONE;
        }
        return null;
    }

    private static TaskTier tierFor(Item item) {
        if (item == Items.PAPER) {
            return TaskTier.EASY;
        }
        if (item == Items.MAP) {
            return TaskTier.MEDIUM;
        }
        if (item == Items.FILLED_MAP) {
            return TaskTier.HARD;
        }
        return null;
    }

    /**
     * Joins the task description lines. Lore is laid out as: tier header, blank, one or more
     * description lines, blank, then the COMPLETE/INCOMPLETE status.
     */
    private static String extractDescription(List<String> lore) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < lore.size(); i++) {
            String line = lore.get(i).trim();
            if (line.isEmpty()) {
                if (sb.length() > 0) {
                    break; // reached the blank line after the description
                }
                continue; // still in the blank line before the description
            }
            if (line.equals("COMPLETE") || line.equals("INCOMPLETE")) {
                break;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(line);
        }
        return sb.toString();
    }

    private static List<String> loreLines(ItemStack stack) {
        ItemLore lore = stack.get(DataComponents.LORE);
        if (lore == null) {
            return List.of();
        }
        List<String> lines = new ArrayList<>(lore.lines().size());
        lore.lines().forEach(component -> lines.add(component.getString()));
        return lines;
    }

    /** The item stacks in the top container only (excludes the 36 player-inventory slots). */
    private static List<ItemStack> topContainerItems(AbstractContainerMenu menu) {
        int containerSize = menu.slots.size() - 36;
        if (containerSize <= 0) {
            return List.of();
        }
        List<ItemStack> items = new ArrayList<>(containerSize);
        for (int i = 0; i < containerSize; i++) {
            Slot slot = menu.slots.get(i);
            items.add(slot.getItem());
        }
        return items;
    }
}
