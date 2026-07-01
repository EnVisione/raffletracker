package com.enviouse.raffletracker.config;

/** Persisted settings for the mod. Plain fields so Gson can (de)serialize it directly. */
public class RaffleConfig {

    /** Hub category: master on/off toggle for the HUD element. */
    public boolean enabled = true;

    /** GUI category: HUD position, in scaled-GUI pixels, and render scale. */
    public float hudX = 5.0f;
    public float hudY = 5.0f;
    public float scale = 1.0f;

    /** GUI category: draw a solid backdrop behind the text (helps readability over busy scenes). */
    public boolean backgroundEnabled = false;
    /** Backdrop opacity, 0 (fully transparent) to 1 (fully opaque). */
    public float backgroundOpacity = 0.5f;

    /** GUI category: when false, tasks show only their name, hiding the "how to complete" text. */
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
