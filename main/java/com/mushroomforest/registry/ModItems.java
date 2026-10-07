package com.mushroomforest.registry;

import com.mushroomforest.MushroomForest;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class ModItems {
	public static final Item ROSY_MUSHROOM_BLOCK = registerBlockItem("rosy_mushroom_block", ModBlocks.ROSY_MUSHROOM_BLOCK);
	public static final Item LAVENDER_MUSHROOM_BLOCK = registerBlockItem("lavender_mushroom_block", ModBlocks.LAVENDER_MUSHROOM_BLOCK);
	public static final Item AZURE_MUSHROOM_BLOCK = registerBlockItem("azure_mushroom_block", ModBlocks.AZURE_MUSHROOM_BLOCK);
	public static final Item BUTTERCUP_MUSHROOM_BLOCK = registerBlockItem("buttercup_mushroom_block", ModBlocks.BUTTERCUP_MUSHROOM_BLOCK);

	public static final Item MUSHROOM_FAIRY_SPAWN_EGG = registerSpawnEgg("mushroom_fairy_spawn_egg");

	private static ResourceKey<Item> key(String name) {
		return ResourceKey.create(Registries.ITEM, MushroomForest.id(name));
	}

	private static Item registerBlockItem(String name, Block block) {
		ResourceKey<Item> key = key(name);
		Item item = new BlockItem(block, new Item.Properties().setId(key).useBlockDescriptionPrefix());
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	private static Item registerSpawnEgg(String name) {
		ResourceKey<Item> key = key(name);
		Item item = new SpawnEggItem(new Item.Properties().setId(key).spawnEgg(ModEntities.MUSHROOM_FAIRY));
		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	public static void register() {
		// Put the caps right after vanilla's mushroom stem in the Natural Blocks tab...
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(output ->
				output.insertAfter(Blocks.MUSHROOM_STEM, ROSY_MUSHROOM_BLOCK, LAVENDER_MUSHROOM_BLOCK, AZURE_MUSHROOM_BLOCK, BUTTERCUP_MUSHROOM_BLOCK));

		// ...and the fairy egg in the Spawn Eggs tab.
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(output ->
				output.accept(MUSHROOM_FAIRY_SPAWN_EGG));
	}

	private ModItems() {
	}
}
