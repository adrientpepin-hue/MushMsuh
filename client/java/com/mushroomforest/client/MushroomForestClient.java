package com.mushroomforest.client;

import com.mushroomforest.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;

public class MushroomForestClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModelLayerRegistry.registerModelLayer(MushroomFairyModel.LAYER, MushroomFairyModel::createBodyLayer);
		EntityRendererRegistry.register(ModEntities.MUSHROOM_FAIRY, MushroomFairyRenderer::new);
	}
}
