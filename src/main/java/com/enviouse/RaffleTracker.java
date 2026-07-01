package com.enviouse;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaffleTracker implements ModInitializer {
	public static final String MOD_ID = "raffletracker";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// RaffleTracker is a client-only mod; the real setup happens in the client entrypoint
		// (see RaffleTrackerClient). This just marks the common initializer as loaded.
		LOGGER.info("RaffleTracker loaded");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
