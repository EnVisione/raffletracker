package com.enviouse.raffletracker.config;

import com.enviouse.RaffleTracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// loads and saves the config to config/raffletracker.json.
public final class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE =
            FabricLoader.getInstance().getConfigDir().resolve("raffletracker.json");

    private static RaffleConfig config = new RaffleConfig();

    private ConfigManager() {
    }

    public static RaffleConfig get() {
        return config;
    }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                RaffleConfig loaded = GSON.fromJson(Files.readString(FILE), RaffleConfig.class);
                if (loaded != null) {
                    config = loaded;
                    config.clampScale();
                    config.clampOpacity();
                }
            } else {
                save();
            }
        } catch (Exception e) {
            RaffleTracker.LOGGER.error("Failed to load RaffleTracker config, using defaults", e);
            config = new RaffleConfig();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(config));
        } catch (IOException e) {
            RaffleTracker.LOGGER.error("Failed to save RaffleTracker config", e);
        }
    }
}
