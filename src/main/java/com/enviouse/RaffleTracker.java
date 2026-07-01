package com.enviouse;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaffleTracker implements ModInitializer {
	public static final String MOD_ID = "raffletracker";

	// logger for the mod. we name it after the mod id so its clear in the logs which mod is talking.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// this is a client only mod so the real setup lives in the client entrypoint.
		// this just marks the shared initializer as loaded.
		LOGGER.info("RaffleTracker loaded");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
