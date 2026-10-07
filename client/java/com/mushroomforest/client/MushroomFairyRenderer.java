package com.mushroomforest.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mushroomforest.MushroomForest;
import com.mushroomforest.entity.MushroomFairy;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class MushroomFairyRenderer extends MobRenderer<MushroomFairy, MushroomFairyRenderState, MushroomFairyModel> {
	private static final Identifier TEXTURE = MushroomForest.id("textures/entity/mushroom_fairy.png");

	public MushroomFairyRenderer(EntityRendererProvider.Context context) {
		super(context, new MushroomFairyModel(context.bakeLayer(MushroomFairyModel.LAYER)), 0.2F);
	}

	@Override
	public MushroomFairyRenderState createRenderState() {
		return new MushroomFairyRenderState();
	}

	@Override
	public Identifier getTextureLocation(MushroomFairyRenderState state) {
		return TEXTURE;
	}

	@Override
	protected void scale(MushroomFairyRenderState state, PoseStack poseStack) {
		// The model is built a bit larger than the fairy really is, for readable pixels.
		poseStack.scale(0.8F, 0.8F, 0.8F);
	}
}
