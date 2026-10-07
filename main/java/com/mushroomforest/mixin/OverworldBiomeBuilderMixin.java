package com.mushroomforest.mixin;

import com.mojang.datafixers.util.Pair;
import com.mushroomforest.registry.ModBiomes;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Puts the Mushroom Forest into normal Overworld generation, exactly as
 * common as Plains.
 *
 * Vanilla builds the Overworld biome map by handing (climate region -> biome)
 * pairs to a consumer. We wrap that consumer: every climate region that would
 * have been Plains is cut in half along its humidity range. The drier half
 * stays Plains and the more humid half becomes Mushroom Forest - so the two
 * biomes end up covering the same amount of the world, and the mushrooms
 * appear wherever Plains meet the wetter forests.
 *
 * This also applies to Large Biomes and Amplified worlds, and plays nicely
 * with TerraBlender, which reuses the same vanilla builder.
 */
@Mixin(OverworldBiomeBuilder.class)
public abstract class OverworldBiomeBuilderMixin {
	@ModifyVariable(method = "addBiomes", at = @At("HEAD"), argsOnly = true)
	private Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mushroomforest$sharePlains(Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> output) {
		return entry -> {
			if (Biomes.PLAINS.equals(entry.getSecond())) {
				Climate.ParameterPoint point = entry.getFirst();
				Climate.Parameter humidity = point.humidity();
				if (humidity.max() > humidity.min()) {
					long middle = humidity.min() + (humidity.max() - humidity.min()) / 2L;
					output.accept(Pair.of(mushroomforest$withHumidity(point, new Climate.Parameter(humidity.min(), middle)), Biomes.PLAINS));
					output.accept(Pair.of(mushroomforest$withHumidity(point, new Climate.Parameter(middle, humidity.max())), ModBiomes.MUSHROOM_FOREST));
					return;
				}
			}
			output.accept(entry);
		};
	}

	@Unique
	private static Climate.ParameterPoint mushroomforest$withHumidity(Climate.ParameterPoint point, Climate.Parameter humidity) {
		return new Climate.ParameterPoint(
				point.temperature(),
				humidity,
				point.continentalness(),
				point.erosion(),
				point.depth(),
				point.weirdness(),
				point.offset());
	}
}
