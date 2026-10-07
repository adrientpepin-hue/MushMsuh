package com.mushroomforest.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mushroomforest.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * A fairy ring: a near-perfect circle of little red and brown mushrooms
 * sprouting in the grass, with a few gaps so it looks natural.
 */
public record FairyRingFeature(int minRadius, int maxRadius) implements Feature {
	public static final MapCodec<FairyRingFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.intRange(2, 8).fieldOf("min_radius").forGetter(FairyRingFeature::minRadius),
			Codec.intRange(2, 8).fieldOf("max_radius").forGetter(FairyRingFeature::maxRadius)
	).apply(instance, FairyRingFeature::new));

	@Override
	public MapCodec<FairyRingFeature> codec() {
		return CODEC;
	}

	@Override
	public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
		if (!level.getBlockState(origin.below()).is(ModTags.FAIRY_RING_SOIL)) {
			return false;
		}

		int radius = this.minRadius + random.nextInt(Math.max(1, this.maxRadius - this.minRadius + 1));
		int steps = (int) Math.ceil(2.0 * Math.PI * radius * 1.4);
		boolean redRing = random.nextBoolean();
		int placed = 0;

		for (int i = 0; i < steps; i++) {
			if (random.nextFloat() < 0.15F) {
				continue; // a few gaps
			}
			double angle = 2.0 * Math.PI * i / steps;
			int x = origin.getX() + (int) Math.round(Math.cos(angle) * radius);
			int z = origin.getZ() + (int) Math.round(Math.sin(angle) * radius);
			BlockPos spot = this.findSpot(level, x, origin.getY(), z);
			if (spot != null) {
				// Mostly one colour, with the odd mushroom of the other kind.
				boolean red = random.nextInt(5) == 0 ? !redRing : redRing;
				BlockState mushroom = red ? Blocks.RED_MUSHROOM.defaultBlockState() : Blocks.BROWN_MUSHROOM.defaultBlockState();
				level.setBlock(spot, mushroom, 3);
				placed++;
			}
		}

		return placed > 0;
	}

	/** Finds the grass surface near the ring's height, so rings follow gentle slopes. */
	private BlockPos findSpot(WorldGenLevel level, int x, int y, int z) {
		for (int dy = 3; dy >= -3; dy--) {
			BlockPos pos = new BlockPos(x, y + dy, z);
			BlockState state = level.getBlockState(pos);
			boolean free = state.isAir() || state.is(Blocks.SHORT_GRASS);
			if (free && level.getBlockState(pos.below()).is(ModTags.FAIRY_RING_SOIL)) {
				return pos;
			}
		}
		return null;
	}
}
