package com.mushroomforest.registry;

import com.mushroomforest.MushroomForest;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/** Tags are defined in JSON under data/.../tags, so they can be edited without touching Java. */
public final class ModTags {
	/** Items a Mushroom Fairy will happily accept (and heal you for). */
	public static final TagKey<Item> FAIRY_TREATS = TagKey.create(Registries.ITEM, MushroomForest.id("fairy_treats"));

	/** Ground a giant mushroom is allowed to grow on. */
	public static final TagKey<Block> GIANT_MUSHROOM_SOIL = TagKey.create(Registries.BLOCK, MushroomForest.id("giant_mushroom_can_place_on"));

	/** Ground fairy rings can sprout on. */
	public static final TagKey<Block> FAIRY_RING_SOIL = TagKey.create(Registries.BLOCK, MushroomForest.id("fairy_ring_can_place_on"));

	/** Ground Mushroom Fairies can naturally spawn above. */
	public static final TagKey<Block> FAIRIES_SPAWNABLE_ON = TagKey.create(Registries.BLOCK, MushroomForest.id("fairies_spawnable_on"));

	/** Vanilla tag of plants and such that a growing mushroom may push aside. */
	public static final TagKey<Block> REPLACEABLE_BY_MUSHROOMS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("minecraft", "replaceable_by_mushrooms"));

	private ModTags() {
	}
}
