package com.mushroomforest.registry;

import com.mushroomforest.MushroomForest;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

/**
 * The biome itself is pure data: data/mushroomforest/worldgen/biome/mushroom_forest.json.
 * Because it is a registered biome, it also shows up in the "Single Biome"
 * world type automatically.
 */
public final class ModBiomes {
	public static final ResourceKey<Biome> MUSHROOM_FOREST = ResourceKey.create(Registries.BIOME, MushroomForest.id("mushroom_forest"));

	private ModBiomes() {
	}
}
