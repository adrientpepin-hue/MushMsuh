package com.mushroomforest.registry;

import com.mushroomforest.MushroomForest;
import com.mushroomforest.worldgen.FairyRingFeature;
import com.mushroomforest.worldgen.GiantMushroomFeature;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Registers the two custom world-generation feature types. Which mushrooms
 * actually grow (colours, sizes, shapes, how many) is configured in JSON under
 * data/mushroomforest/worldgen/feature and .../placed_feature.
 */
public final class ModFeatures {
	public static void register() {
		Registry.register(BuiltInRegistries.FEATURE_TYPE, MushroomForest.id("giant_mushroom"), GiantMushroomFeature.CODEC);
		Registry.register(BuiltInRegistries.FEATURE_TYPE, MushroomForest.id("fairy_ring"), FairyRingFeature.CODEC);
	}

	private ModFeatures() {
	}
}
