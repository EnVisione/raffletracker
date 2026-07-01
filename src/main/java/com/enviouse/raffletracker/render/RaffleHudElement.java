package com.enviouse.raffletracker.render;

import com.enviouse.raffletracker.config.ConfigManager;
import com.enviouse.raffletracker.config.RaffleConfig;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

// the always visible hud overlay with a see through background. its a fabric hud element so it
// follows the normal hud rules. it hides with f1 and hides while a full screen is open, and the
// edit screen draws its own preview instead.
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
        // while any screen is open the edit screen or the menu itself owns the overlay, so we skip
        // drawing here so we dont end up with two trackers on top of each other.
        if (mc.screen != null) {
            return;
        }
        RaffleRenderer.render(ctx, mc.font, Math.round(config.hudX), Math.round(config.hudY), config.scale, false);
    }
}
