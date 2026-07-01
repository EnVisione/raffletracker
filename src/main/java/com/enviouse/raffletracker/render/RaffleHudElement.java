package com.enviouse.raffletracker.render;

import com.enviouse.raffletracker.config.ConfigManager;
import com.enviouse.raffletracker.config.RaffleConfig;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * The always-visible, transparent-background HUD overlay. Registered as a Fabric HUD element so it
 * follows vanilla HUD visibility rules (hidden with F1, hidden while a full screen is open — the
 * edit screen draws its own preview instead).
 */
public class RaffleHudElement implements HudElement {

    @Override
    public void extractRenderState(GuiGraphicsExtractor ctx, DeltaTracker delta) {
        RaffleConfig config = ConfigManager.get();
        if (!config.enabled) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }
        // While any screen is open the edit screen (or the menu itself) owns the overlay, so skip
        // drawing here to avoid a double-rendered tracker.
        if (mc.screen != null) {
            return;
        }
        RaffleRenderer.render(ctx, mc.font, Math.round(config.hudX), Math.round(config.hudY), config.scale, false);
    }
}
