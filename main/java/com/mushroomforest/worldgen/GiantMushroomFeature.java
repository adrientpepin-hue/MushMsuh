package com.mushroomforest.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mushroomforest.registry.ModTags;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

/**
 * A big storybook mushroom: a pale stem topped with a coloured cap.
 *
 * Three cap shapes:
 *  - toadstool: a round dome with a drooping rim (the classic fairy-tale one)
 *  - parasol:   a wide, flat umbrella on a tall slender stem
 *  - bell:      a pointy witch-hat cone with a curled tip and a flared skirt
 *
 * Caps are hollow shells (only the outside is placed). The underside of the
 * cap shows the gills texture, and some caps have glowing shroomlight
 * "lanterns" tucked into their underside.
 *
 * Every number here (sizes, chances) comes from the JSON feature files, so
 * the forest can be re-tuned without touching Java.
 */
public record GiantMushroomFeature(
		BlockStateProvider capProvider,
		BlockStateProvider stemProvider,
		Shape shape,
		int minHeight,
		int maxHeight,
		int minRadius,
		int maxRadius,
		float lanternChance,
		float leanChance,
		int thickStemRadius
) implements Feature {
	public static final MapCodec<GiantMushroomFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockStateProvider.DIRECT_CODEC.fieldOf("cap_provider").forGetter(GiantMushroomFeature::capProvider),
			BlockStateProvider.DIRECT_CODEC.fieldOf("stem_provider").forGetter(GiantMushroomFeature::stemProvider),
			Shape.CODEC.fieldOf("shape").forGetter(GiantMushroomFeature::shape),
			Codec.intRange(3, 40).fieldOf("min_height").forGetter(GiantMushroomFeature::minHeight),
			Codec.intRange(3, 40).fieldOf("max_height").forGetter(GiantMushroomFeature::maxHeight),
			Codec.intRange(2, 9).fieldOf("min_radius").forGetter(GiantMushroomFeature::minRadius),
			Codec.intRange(2, 9).fieldOf("max_radius").forGetter(GiantMushroomFeature::maxRadius),
			Codec.floatRange(0.0F, 1.0F).optionalFieldOf("lantern_chance", 0.0F).forGetter(GiantMushroomFeature::lanternChance),
			Codec.floatRange(0.0F, 1.0F).optionalFieldOf("lean_chance", 0.0F).forGetter(GiantMushroomFeature::leanChance),
			Codec.intRange(2, 12).optionalFieldOf("thick_stem_radius", 6).forGetter(GiantMushroomFeature::thickStemRadius)
	).apply(instance, GiantMushroomFeature::new));

	@Override
	public MapCodec<GiantMushroomFeature> codec() {
		return CODEC;
	}

	@Override
	public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
		BlockPos base = origin;
		if (base.getY() <= level.getMinY() + 1) {
			return false;
		}

		if (!level.getBlockState(base.below()).is(ModTags.GIANT_MUSHROOM_SOIL)) {
			return false;
		}

		int height = this.minHeight + random.nextInt(Math.max(1, this.maxHeight - this.minHeight + 1));
		int radius = this.minRadius + random.nextInt(Math.max(1, this.maxRadius - this.minRadius + 1));
		CapShape cap = CapShape.create(this.shape, radius, random);

		// An optional gentle lean, for a more storybook silhouette.
		int leanX = 0;
		int leanZ = 0;
		if (random.nextFloat() < this.leanChance) {
			switch (random.nextInt(4)) {
				case 0 -> leanX = 1;
				case 1 -> leanX = -1;
				case 2 -> leanZ = 1;
				default -> leanZ = -1;
			}
		}
		int leanStart = Math.max(2, Math.round(height * 0.55F));

		BlockPos capCenter = base.offset(leanX, height, leanZ);
		if (capCenter.getY() + cap.height() + 2 >= level.getMaxY()) {
			return false;
		}

		// The central column must be completely free, otherwise we don't grow here at all.
		List<BlockPos> core = new ArrayList<>();
		for (int y = 0; y < height; y++) {
			boolean leaning = y >= leanStart;
			core.add(base.offset(leaning ? leanX : 0, y, leaning ? leanZ : 0));
			if (y == leanStart && (leanX != 0 || leanZ != 0)) {
				core.add(base.above(y)); // the "elbow", so the bend stays connected
			}
		}
		for (BlockPos pos : core) {
			if (!canReplace(level, pos)) {
				return false;
			}
		}
		if (!canReplace(level, capCenter)) {
			return false;
		}

		// Stem girth grows with the cap: single column, then a plus-shaped trunk,
		// then a chunky 3x3 trunk. Every stem gets a little flared foot.
		int girth = radius >= this.thickStemRadius + 2 ? 2 : (radius >= this.thickStemRadius ? 1 : 0);
		List<BlockPos> extras = new ArrayList<>();
		for (int y = 0; y < height; y++) {
			BlockPos pos = base.offset(y >= leanStart ? leanX : 0, y, y >= leanStart ? leanZ : 0);
			boolean foot = y == 0 || (y == 1 && girth >= 1);
			for (int dx = -2; dx <= 2; dx++) {
				for (int dz = -2; dz <= 2; dz++) {
					int ring = Math.abs(dx) + Math.abs(dz);
					boolean chebyshevOne = Math.abs(dx) <= 1 && Math.abs(dz) <= 1;
					boolean add = switch (girth) {
						case 0 -> ring == 1 && foot && radius >= 4;
						case 1 -> ring == 1 || (foot && ring == 2 && (dx == 0 || dz == 0));
						default -> (chebyshevOne && ring > 0) || (foot && ring == 2 && (dx == 0 || dz == 0));
					};
					if (add) {
						extras.add(pos.offset(dx, 0, dz));
					}
				}
			}
		}

		for (BlockPos pos : core) {
			level.setBlock(pos, this.stemProvider.getState(level, random, pos), 3);
		}
		for (BlockPos pos : extras) {
			if (canReplace(level, pos)) {
				level.setBlock(pos, this.stemProvider.getState(level, random, pos), 3);
			}
		}

		// The cap: one colour per mushroom.
		BlockState capState = this.capProvider.getState(level, random, capCenter);
		int reach = radius + 2;
		for (int dy = -1; dy <= cap.height(); dy++) {
			for (int dx = -reach; dx <= reach; dx++) {
				for (int dz = -reach; dz <= reach; dz++) {
					BlockPos pos = capCenter.offset(dx, dy, dz);
					if (dy == -1) {
						if (cap.inBrim(dx, dz)) {
							this.placeCap(level, pos, capState, true);
						}
						continue;
					}
					if (!cap.inside(dx, dy, dz)) {
						continue;
					}
					boolean floor = dy == 0;
					boolean surface = floor
							|| !cap.inside(dx, dy + 1, dz)
							|| !cap.inside(dx + 1, dy, dz)
							|| !cap.inside(dx - 1, dy, dz)
							|| !cap.inside(dx, dy, dz + 1)
							|| !cap.inside(dx, dy, dz - 1);
					if (surface) {
						this.placeCap(level, pos, capState, floor);
					}
				}
			}
		}

		// Glowing shroomlight "lanterns" set into the underside of the cap.
		if (radius >= 3 && random.nextFloat() < this.lanternChance) {
			int wanted = 1 + random.nextInt(radius >= 5 ? 4 : 2);
			for (int attempt = 0; attempt < 24 && wanted > 0; attempt++) {
				int dx = random.nextInt(radius * 2 + 1) - radius;
				int dz = random.nextInt(radius * 2 + 1) - radius;
				int distSq = dx * dx + dz * dz;
				if (distSq < 4 || distSq > (radius - 1) * (radius - 1)) {
					continue;
				}
				BlockPos pos = capCenter.offset(dx, 0, dz);
				if (level.getBlockState(pos).is(capState.getBlock())) {
					level.setBlock(pos, Blocks.SHROOMLIGHT.defaultBlockState(), 3);
					wanted--;
				}
			}
		}

		return true;
	}

	private void placeCap(WorldGenLevel level, BlockPos pos, BlockState capState, boolean underside) {
		if (!canReplace(level, pos)) {
			return;
		}
		BlockState state = capState;
		if (underside && state.hasProperty(HugeMushroomBlock.DOWN)) {
			// Faces set to false show the "inside" texture: here, the gills under the cap.
			state = state.setValue(HugeMushroomBlock.DOWN, false);
		}
		level.setBlock(pos, state, 3);
	}

	/** Air, grass, flowers, small mushrooms, leaves, moss carpet... anything soft a mushroom can push aside. */
	static boolean canReplace(WorldGenLevel level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.isAir()
				|| state.is(ModTags.REPLACEABLE_BY_MUSHROOMS)
				|| state.getBlock() instanceof VegetationBlock
				|| state.is(Blocks.MOSS_CARPET);
	}

	/** The geometry of one cap, decided once per mushroom. */
	private record CapShape(Shape shape, int radius, int height, int tipX, int tipZ) {
		static CapShape create(Shape shape, int radius, RandomSource random) {
			return switch (shape) {
				case TOADSTOOL -> new CapShape(shape, radius, Math.max(2, Math.round(radius * 0.8F)), 0, 0);
				case PARASOL -> new CapShape(shape, radius, 2, 0, 0);
				case BELL -> {
					int tipX = 0;
					int tipZ = 0;
					switch (random.nextInt(4)) {
						case 0 -> tipX = 1;
						case 1 -> tipX = -1;
						case 2 -> tipZ = 1;
						default -> tipZ = -1;
					}
					yield new CapShape(shape, radius, Math.round(radius * 1.7F) + 1, tipX, tipZ);
				}
			};
		}

		/** Is this offset (relative to the middle of the cap's bottom layer) part of the cap? */
		boolean inside(int dx, int dy, int dz) {
			if (dy < 0) {
				return false;
			}
			switch (this.shape) {
				case TOADSTOOL -> {
					float rh = this.radius + 0.5F;
					float rv = this.height + 0.5F;
					return (dx * dx + dz * dz) / (rh * rh) + (dy * dy) / (rv * rv) <= 1.0F;
				}
				case PARASOL -> {
					float dist = (float) Math.sqrt(dx * dx + dz * dz);
					return switch (dy) {
						case 0 -> dist <= this.radius + 0.5F;
						case 1 -> dist <= this.radius - 1.5F;
						case 2 -> this.radius >= 5 && dist <= this.radius - 3.5F;
						default -> false;
					};
				}
				case BELL -> {
					if (dy >= this.height) {
						return false;
					}
					// The tip curls over to one side, like a wizard's hat.
					int curl = Math.max(0, dy - (this.height - 3));
					float cx = dx - this.tipX * curl;
					float cz = dz - this.tipZ * curl;
					float t = dy / (float) this.height;
					float r = this.radius * (float) Math.pow(1.0F - t, 0.8F);
					return Math.sqrt(cx * cx + cz * cz) <= r + 0.5F;
				}
				default -> {
					return false;
				}
			}
		}

		/** The drooping rim, one block below the cap. */
		boolean inBrim(int dx, int dz) {
			float dist = (float) Math.sqrt(dx * dx + dz * dz);
			return switch (this.shape) {
				case TOADSTOOL -> dist > this.radius - 1.0F && dist <= this.radius + 0.5F;
				case PARASOL -> dist > this.radius - 0.5F && dist <= this.radius + 0.5F;
				case BELL -> dist > this.radius - 0.5F && dist <= this.radius + 1.5F;
			};
		}
	}

	public enum Shape {
		TOADSTOOL("toadstool"),
		PARASOL("parasol"),
		BELL("bell");

		public static final Codec<Shape> CODEC = Codec.STRING.comapFlatMap(Shape::byName, Shape::getSerializedName);

		private final String serializedName;

		Shape(String serializedName) {
			this.serializedName = serializedName;
		}

		public String getSerializedName() {
			return this.serializedName;
		}

		private static DataResult<Shape> byName(String name) {
			for (Shape shape : values()) {
				if (shape.serializedName.equals(name)) {
					return DataResult.success(shape);
				}
			}
			return DataResult.error(() -> "Unknown giant mushroom shape: " + name);
		}
	}
}
