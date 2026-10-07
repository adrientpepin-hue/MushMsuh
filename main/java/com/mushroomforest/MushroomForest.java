package com.mushroomforest;

import com.mushroomforest.registry.ModBlocks;
import com.mushroomforest.registry.ModEntities;
import com.mushroomforest.registry.ModFeatures;
import com.mushroomforest.registry.ModItems;
import com.mushroomforest.registry.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mushroom Forest: a cozy biome of giant rainbow mushrooms, and the little
 * Mushroom Fairies who live there.
 *
 * Almost everything about the biome itself (colours, music, spawns, which
 * mushrooms grow and how often) lives in data files under
 * src/main/resources/data/mushroomforest, so it can be tweaked without Java.
 */
public class MushroomForest implements ModInitializer {
	public static final String MOD_ID = "mushroomforest";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// Order matters a little: sounds and blocks first, then the fairy,
		// then items (the spawn egg needs the fairy's entity type).
		ModSounds.register();
		ModBlocks.register();
		ModEntities.register();
		ModItems.register();
		ModFeatures.register();

		LOGGER.info("The mushroom forest is sprouting.");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
