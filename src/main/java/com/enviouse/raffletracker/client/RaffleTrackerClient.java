package com.enviouse.raffletracker.client;

import com.enviouse.RaffleTracker;
import com.enviouse.raffletracker.config.ConfigManager;
import com.enviouse.raffletracker.data.RaffleData;
import com.enviouse.raffletracker.data.RaffleDraw;
import com.enviouse.raffletracker.data.RaffleTask;
import com.enviouse.raffletracker.gui.RaffleConfigScreen;
import com.enviouse.raffletracker.parse.ContainerParser;
import com.enviouse.raffletracker.render.RaffleHudElement;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Client entrypoint. Registers the HUD overlay, the {@code /raffletracker} config command, and a
 * tick listener that reads the Century Celebration chests whenever the player opens them.
 */
public class RaffleTrackerClient implements ClientModInitializer {

    // Matches "RAFFLE TASK! You completed the <name> raffle task and earned ..." from chat.
    private static final Pattern TASK_COMPLETE = Pattern.compile("You completed the (.+?) raffle task");

    // The screen instances we last parsed, so each fresh open announces exactly once.
    private Screen lastTasksScreen;
    private Screen lastBoxScreen;

    @Override
    public void onInitializeClient() {
        ConfigManager.load();
        HudElementRegistry.addLast(RaffleTracker.id("raffle_tracker"), new RaffleHudElement());
        registerCommands();
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
        ClientReceiveMessageEvents.GAME.register(this::onGameMessage);
        RaffleTracker.LOGGER.info("RaffleTracker client initialized");
    }

    /** Watches chat and drops a task from the tracker the moment its completion message appears. */
    private void onGameMessage(Component message, boolean overlay) {
        Matcher matcher = TASK_COMPLETE.matcher(message.getString());
        if (matcher.find()) {
            RaffleData.markCompleted(matcher.group(1));
        }
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) -> {
            dispatcher.register(ClientCommands.literal("raffletracker").executes(this::openConfig));
            dispatcher.register(ClientCommands.literal("rt").executes(this::openConfig));
        });
    }

    private int openConfig(CommandContext<FabricClientCommandSource> context) {
        Minecraft mc = context.getSource().getClient();
        // Defer to the next tick so the chat screen has finished closing before we swap screens.
        mc.execute(() -> mc.setScreen(new RaffleConfigScreen()));
        return 1;
    }

    private void onEndTick(Minecraft client) {
        // This only detects an open chest so its contents can be read. Countdowns and the task
        // reset are worked out from wall clock time at render time, never from ticks.
        if (!(client.screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }
        String title = containerScreen.getTitle().getString();
        AbstractContainerMenu menu = containerScreen.getMenu();

        if (title.equals(ContainerParser.TASKS_TITLE)) {
            List<RaffleTask> tasks = ContainerParser.parseTasks(menu);
            if (!tasks.isEmpty()) {
                RaffleData.setTasks(tasks);
                if (client.screen != lastTasksScreen) {
                    lastTasksScreen = client.screen;
                    long incomplete = tasks.stream().filter(task -> !task.completed()).count();
                    announce(client, "Loaded " + tasks.size() + " tasks (" + incomplete + " incomplete)");
                }
            }
        } else if (title.contains(ContainerParser.BOX_TITLE_MARKER)) {
            List<RaffleDraw> draws = ContainerParser.parseDraws(menu);
            if (!draws.isEmpty()) {
                RaffleData.setDraws(draws);
                if (client.screen != lastBoxScreen) {
                    lastBoxScreen = client.screen;
                    announce(client, "Tracking " + draws.size() + " raffle draws");
                }
            }
        }
    }

    private void announce(Minecraft client, String message) {
        client.gui.getChat().addClientSystemMessage(
                Component.literal("[RaffleTracker] ").withStyle(ChatFormatting.AQUA)
                        .append(Component.literal(message).withStyle(ChatFormatting.GRAY)));
    }
}
