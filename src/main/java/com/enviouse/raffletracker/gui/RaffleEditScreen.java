package com.enviouse.raffletracker.gui;

import com.enviouse.raffletracker.config.ConfigManager;
import com.enviouse.raffletracker.config.RaffleConfig;
import com.enviouse.raffletracker.render.RaffleRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * SkyHanni-style "move the overlay" screen: drag the tracker to reposition it, scroll to scale.
 * Changes are saved on release / scroll and when the screen closes.
 */
public class RaffleEditScreen extends Screen {

    private static final int DIM_BACKGROUND = 0x88000000;

    private final Screen parent;

    private boolean dragging;
    private double dragOffsetX;
    private double dragOffsetY;
    private int boxWidth;
    private int boxHeight;

    public RaffleEditScreen(Screen parent) {
        super(Component.literal("RaffleTracker Edit"));
        this.parent = parent;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, DIM_BACKGROUND);

        RaffleConfig config = ConfigManager.get();
        int[] size = RaffleRenderer.render(ctx, this.font,
                Math.round(config.hudX), Math.round(config.hudY), config.scale, true);
        this.boxWidth = size[0];
        this.boxHeight = size[1];

        ctx.centeredText(this.font,
                Component.literal("Drag to move  •  Scroll to scale (" + String.format("%.1f", config.scale)
                        + "x)  •  Esc to save"),
                this.width / 2, this.height - 20, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            RaffleConfig config = ConfigManager.get();
            double mx = event.x();
            double my = event.y();
            if (mx >= config.hudX && mx <= config.hudX + this.boxWidth
                    && my >= config.hudY && my <= config.hudY + this.boxHeight) {
                this.dragging = true;
                this.dragOffsetX = mx - config.hudX;
                this.dragOffsetY = my - config.hudY;
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (this.dragging) {
            RaffleConfig config = ConfigManager.get();
            config.hudX = clamp((float) (event.x() - this.dragOffsetX), this.width - this.boxWidth);
            config.hudY = clamp((float) (event.y() - this.dragOffsetY), this.height - this.boxHeight);
            return true;
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.dragging) {
            this.dragging = false;
            ConfigManager.save();
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        RaffleConfig config = ConfigManager.get();
        config.scale += (float) verticalAmount * 0.1f;
        config.clampScale();
        config.scale = Math.round(config.scale * 10f) / 10f;
        ConfigManager.save();
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        ConfigManager.save();
        this.minecraft.setScreen(this.parent);
    }

    private static float clamp(float value, float max) {
        if (value < 0f) {
            return 0f;
        }
        return Math.min(value, Math.max(0f, max));
    }
}
