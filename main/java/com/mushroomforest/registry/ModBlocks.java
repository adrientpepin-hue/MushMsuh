package com.mushroomforest.registry;

import com.mushroomforest.MushroomForest;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

/**
 * The coloured giant-mushroom caps. They behave exactly like vanilla red and
 * brown mushroom blocks (same hardness, sounds, connecting faces, drops).
 */
public final class ModBlocks {
	public static final Block ROSY_MUSHROOM_BLOCK = registerCap("rosy_mushroom_block", MapColor.COLOR_PINK);
	public static final Block LAVENDER_MUSHROOM_BLOCK = registerCap("lavender_mushroom_block", MapColor.COLOR_PURPLE);
	public static final Block AZURE_MUSHROOM_BLOCK = registerCap("azure_mushroom_block", MapColor.COLOR_LIGHT_BLUE);
	public static final Block BUTTERCUP_MUSHROOM_BLOCK = registerCap("buttercup_mushroom_block", MapColor.COLOR_YELLOW);

	public static final List<Block> CAPS = List.of(ROSY_MUSHROOM_BLOCK, LAVENDER_MUSHROOM_BLOCK, AZURE_MUSHROOM_BLOCK, BUTTERCUP_MUSHROOM_BLOCK);

	private static Block registerCap(String name, MapColor color) {
		ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, MushroomForest.id(name));
		Block block = new HugeMushroomBlock(BlockBehaviour.Properties.of()
				.mapColor(color)
				.instrument(NoteBlockInstrument.BASS)
				.strength(0.2F)
				.sound(SoundType.WOOD)
				.ignitedByLava()
				.setId(key));
		return Registry.register(BuiltInRegistries.BLOCK, key, block);
	}

	public static void register() {
		// Loading this class registers everything above.
	}

	private ModBlocks() {
	}
}
