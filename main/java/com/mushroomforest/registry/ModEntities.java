package com.mushroomforest.registry;

import com.mushroomforest.MushroomForest;
import com.mushroomforest.entity.MushroomFairy;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;

public final class ModEntities {
	public static final ResourceKey<EntityType<?>> MUSHROOM_FAIRY_KEY = ResourceKey.create(Registries.ENTITY_TYPE, MushroomForest.id("mushroom_fairy"));

	public static final EntityType<MushroomFairy> MUSHROOM_FAIRY = Registry.register(
			BuiltInRegistries.ENTITY_TYPE,
			MUSHROOM_FAIRY_KEY,
			FabricEntityType.Builder.<MushroomFairy>createMob(MushroomFairy::new, MobCategory.CREATURE, mob -> mob
							.defaultAttributes(MushroomFairy::createAttributes)
							.spawnPlacement(SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MushroomFairy::checkFairySpawnRules))
					.sized(0.4F, 0.6F)
					.clientTrackingRange(8)
					.build(MUSHROOM_FAIRY_KEY));

	public static void register() {
		// Loading this class registers everything above.
	}

	private ModEntities() {
	}
}
