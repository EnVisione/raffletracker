package com.enviouse.raffletracker.gui;

import com.enviouse.raffletracker.config.ConfigManager;
import com.enviouse.raffletracker.config.RaffleConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

// a small config screen with a skyhanni kind of vibe. categories run down the left, hub and gui,
// and the options for whichever one you pick show on the right. its fully standalone. skyhanni has
// no public api so we just copy the look and feel.
public class RaffleConfigScreen extends Screen {

    private static final int CATEGORY_HUB = 0;
    private static final int CATEGORY_GUI = 1;
    private static final String[] CATEGORY_NAMES = {"Hub", "GUI"};

    private static final int HEADER_COLOR = 0xFF55FFFF;
    private static final int DIVIDER_COLOR = 0x40FFFFFF;
    private static final int HEADER_BAR = 0x90000000;

    // layout for the right side option column. init and the label drawing share it so they line up.
    private static final int OPT_X = 115;
    private static final int OPT_Y = 58;
    private static final int OPT_GAP = 24;
    private static final int LABEL_X = OPT_X + 112;

    private final int category;

    public RaffleConfigScreen() {
        this(CATEGORY_HUB);
    }

    public RaffleConfigScreen(int category) {
        super(Component.literal("RaffleTracker"));
        this.category = category;
    }

    @Override
    protected void init() {
        // left column, the category picker.
        addCategoryButton("Hub", CATEGORY_HUB, 40);
        addCategoryButton("GUI", CATEGORY_GUI, 64);

        // right column, the options for whichever category is picked.
        if (category == CATEGORY_HUB) {
            initHubOptions();
        } else {
            initGuiOptions();
        }

        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(this.width / 2 - 50, this.height - 28, 100, 20).build());
    }

    private void initHubOptions() {
        RaffleConfig config = ConfigManager.get();
        addRenderableWidget(Button.builder(toggleLabel("Raffle Tracker", config.enabled), b -> {
            config.enabled = !config.enabled;
            saveAndReopen(CATEGORY_HUB);
        }).bounds(OPT_X, OPT_Y, 160, 20).build());
    }

    private void initGuiOptions() {
        RaffleConfig config = ConfigManager.get();

        addRenderableWidget(Button.builder(Component.literal("Edit Position"),
                b -> this.minecraft.setScreen(new RaffleEditScreen(this)))
                .bounds(OPT_X, OPT_Y, 160, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Scale -"),
                b -> adjustScale(-0.1f)).bounds(OPT_X, OPT_Y + OPT_GAP, 50, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Scale +"),
                b -> adjustScale(0.1f)).bounds(OPT_X + 55, OPT_Y + OPT_GAP, 50, 20).build());

        addRenderableWidget(toggleButton("Background", config.backgroundEnabled, OPT_Y + 2 * OPT_GAP, b -> {
            config.backgroundEnabled = !config.backgroundEnabled;
            saveAndReopen(CATEGORY_GUI);
        }));

        addRenderableWidget(Button.builder(Component.literal("Opacity -"),
                b -> adjustOpacity(-0.1f)).bounds(OPT_X, OPT_Y + 3 * OPT_GAP, 50, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Opacity +"),
                b -> adjustOpacity(0.1f)).bounds(OPT_X + 55, OPT_Y + 3 * OPT_GAP, 50, 20).build());

        addRenderableWidget(toggleButton("Descriptions", config.showDescriptions, OPT_Y + 4 * OPT_GAP, b -> {
            config.showDescriptions = !config.showDescriptions;
            saveAndReopen(CATEGORY_GUI);
        }));

        addRenderableWidget(Button.builder(Component.literal("Reset Position"), b -> {
            config.hudX = 5f;
            config.hudY = 5f;
            saveAndReopen(CATEGORY_GUI);
        }).bounds(OPT_X, OPT_Y + 5 * OPT_GAP, 160, 20).build());
    }

    private Button toggleButton(String name, boolean on, int y, Button.OnPress onPress) {
        return Button.builder(toggleLabel(name, on), onPress).bounds(OPT_X, y, 160, 20).build();
    }

    private static Component toggleLabel(String name, boolean on) {
        return Component.literal(name + ": ").append(on
                ? Component.literal("ON").withStyle(ChatFormatting.GREEN)
                : Component.literal("OFF").withStyle(ChatFormatting.RED));
    }

    private void addCategoryButton(String name, int index, int y) {
        String label = (index == category ? "▶ " : "   ") + name;
        addRenderableWidget(Button.builder(Component.literal(label),
                b -> this.minecraft.setScreen(new RaffleConfigScreen(index)))
                .bounds(10, y, 90, 20).build());
    }

    private void adjustScale(float delta) {
        RaffleConfig config = ConfigManager.get();
        config.scale = round1(config.scale + delta);
        config.clampScale();
        saveAndReopen(CATEGORY_GUI);
    }

    private void adjustOpacity(float delta) {
        RaffleConfig config = ConfigManager.get();
        config.backgroundOpacity = round1(config.backgroundOpacity + delta);
        config.clampOpacity();
        saveAndReopen(CATEGORY_GUI);
    }

    private static float round1(float value) {
        return Math.round(value * 10f) / 10f;
    }

    private void saveAndReopen(int targetCategory) {
        ConfigManager.save();
        this.minecraft.setScreen(new RaffleConfigScreen(targetCategory));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        // we draw the decoration first, then super paints the buttons on top.
        super.extractRenderState(ctx, mouseX, mouseY, delta);

        ctx.fill(0, 0, this.width, 28, HEADER_BAR);
        ctx.centeredText(this.font,
                Component.literal("RaffleTracker").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                this.width / 2, 10, 0xFFFFFFFF);

        ctx.fill(105, 34, 106, this.height - 34, DIVIDER_COLOR);

        ctx.text(this.font,
                Component.literal(CATEGORY_NAMES[category] + " Settings").withStyle(ChatFormatting.GOLD),
                OPT_X, 44, HEADER_COLOR, false);

        if (category == CATEGORY_GUI) {
            RaffleConfig config = ConfigManager.get();
            drawValue(ctx, String.format("%.1fx", config.scale), OPT_Y + OPT_GAP);
            drawValue(ctx, Math.round(config.backgroundOpacity * 100f) + "%", OPT_Y + 3 * OPT_GAP);
        }
    }

    private void drawValue(GuiGraphicsExtractor ctx, String text, int rowY) {
        ctx.text(this.font, Component.literal(text).withStyle(ChatFormatting.GRAY),
                LABEL_X, rowY + 6, 0xFFFFFFFF, false);
    }

    @Override
    public void onClose() {
        ConfigManager.save();
        this.minecraft.setScreen(null);
    }
}
