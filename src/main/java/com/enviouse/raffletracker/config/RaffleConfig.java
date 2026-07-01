package com.enviouse.raffletracker.config;

// saved settings for the mod. plain fields so gson can read and write them straight.
public class RaffleConfig {

    // hub category. the main toggle for the hud, on or off.
    public boolean enabled = true;

    // gui category. where the hud sits in gui pixels, plus the render size.
    public float hudX = 5.0f;
    public float hudY = 5.0f;
    public float scale = 1.0f;

    // gui category. draw a solid backdrop behind the text so its easier to read over busy scenes.
    public boolean backgroundEnabled = false;
    // backdrop opacity, from 0 fully see through up to 1 fully solid.
    public float backgroundOpacity = 0.5f;

    // gui category. when off, tasks show just their name and hide the how to text.
    public boolean showDescriptions = true;

    public static final float MIN_SCALE = 0.5f;
    public static final float MAX_SCALE = 3.0f;

    public void clampScale() {
        if (scale < MIN_SCALE) {
            scale = MIN_SCALE;
        } else if (scale > MAX_SCALE) {
            scale = MAX_SCALE;
        }
    }

    public void clampOpacity() {
        if (backgroundOpacity < 0f) {
            backgroundOpacity = 0f;
        } else if (backgroundOpacity > 1f) {
            backgroundOpacity = 1f;
        }
    }
}
