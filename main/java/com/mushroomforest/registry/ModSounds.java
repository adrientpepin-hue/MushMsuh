package com.mushroomforest.registry;

import com.mushroomforest.MushroomForest;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

/**
 * The fairy's voice. The actual audio is vanilla's (allay chirps and amethyst
 * chimes, pitched up) - see assets/mushroomforest/sounds.json.
 */
public final class ModSounds {
	public static final SoundEvent FAIRY_AMBIENT = register("entity.mushroom_fairy.ambient");
	public static final SoundEvent FAIRY_HURT = register("entity.mushroom_fairy.hurt");
	public static final SoundEvent FAIRY_DEATH = register("entity.mushroom_fairy.death");
	public static final SoundEvent FAIRY_HEAL = register("entity.mushroom_fairy.heal");
	public static final SoundEvent FAIRY_GIGGLE = register("entity.mushroom_fairy.giggle");

	private static SoundEvent register(String name) {
		Identifier id = MushroomForest.id(name);
		return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
	}

	public static void register() {
		// Loading this class registers everything above.
	}

	private ModSounds() {
	}
}
