package com.mushroomforest.client;

import com.mushroomforest.MushroomForest;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;

/**
 * A tiny sprite in a leaf-green tunic, wearing a spotted toadstool as a hat,
 * with two pairs of shimmering wings.
 *
 * Texture layout (64x32, textures/entity/mushroom_fairy.png):
 *   head (0,0)  cap rim (0,8)  cap top (32,0)  body (0,18)
 *   arms (12,18) (16,18)  legs (20,18) (24,18)
 *   upper wings (0,23) (12,23)  lower wings (24,23) (32,23)
 */
public class MushroomFairyModel extends EntityModel<MushroomFairyRenderState> {
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(MushroomForest.id("mushroom_fairy"), "main");

	private final ModelPart all;
	private final ModelPart head;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;
	private final ModelPart rightWing;
	private final ModelPart leftWing;
	private final ModelPart rightLowerWing;
	private final ModelPart leftLowerWing;

	public MushroomFairyModel(ModelPart root) {
		// Translucent, so the wings can be see-through.
		super(root, RenderTypes::entityTranslucent);
		this.all = root.getChild("all");
		ModelPart body = this.all.getChild("body");
		this.head = body.getChild("head");
		this.rightArm = body.getChild("right_arm");
		this.leftArm = body.getChild("left_arm");
		this.rightLeg = body.getChild("right_leg");
		this.leftLeg = body.getChild("left_leg");
		this.rightWing = body.getChild("right_wing");
		this.leftWing = body.getChild("left_wing");
		this.rightLowerWing = body.getChild("right_lower_wing");
		this.leftLowerWing = body.getChild("left_lower_wing");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		CubeDeformation none = new CubeDeformation(0.0F);

		PartDefinition all = root.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		// Body: a little tunic, 4 x 3 x 2. Its pivot is the neck.
		PartDefinition body = all.addOrReplaceChild("body", CubeListBuilder.create()
				.texOffs(0, 18).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 3.0F, 2.0F, none),
				PartPose.offset(0.0F, -5.0F, 0.0F));

		// Head, with the toadstool hat attached so it turns with it.
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F, none),
				PartPose.offset(0.0F, 0.0F, 0.0F));
		head.addOrReplaceChild("cap", CubeListBuilder.create()
				.texOffs(0, 8).addBox(-4.0F, -5.5F, -4.0F, 8.0F, 2.0F, 8.0F, none)
				.texOffs(32, 0).addBox(-3.0F, -6.5F, -3.0F, 6.0F, 1.0F, 6.0F, none),
				PartPose.offset(0.0F, 0.0F, 0.0F));

		// Arms and legs.
		body.addOrReplaceChild("right_arm", CubeListBuilder.create()
				.texOffs(12, 18).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 3.0F, 1.0F, none),
				PartPose.offset(-2.5F, 0.4F, 0.0F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create()
				.texOffs(16, 18).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 3.0F, 1.0F, none),
				PartPose.offset(2.5F, 0.4F, 0.0F));
		body.addOrReplaceChild("right_leg", CubeListBuilder.create()
				.texOffs(20, 18).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, none),
				PartPose.offset(-1.0F, 3.0F, 0.0F));
		body.addOrReplaceChild("left_leg", CubeListBuilder.create()
				.texOffs(24, 18).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, none),
				PartPose.offset(1.0F, 3.0F, 0.0F));

		// Two pairs of paper-thin wings on the back.
		body.addOrReplaceChild("right_wing", CubeListBuilder.create()
				.texOffs(0, 23).addBox(-6.0F, -6.0F, 0.0F, 6.0F, 8.0F, 0.0F, none),
				PartPose.offset(-0.5F, 0.5F, 1.0F));
		body.addOrReplaceChild("left_wing", CubeListBuilder.create()
				.texOffs(12, 23).addBox(0.0F, -6.0F, 0.0F, 6.0F, 8.0F, 0.0F, none),
				PartPose.offset(0.5F, 0.5F, 1.0F));
		body.addOrReplaceChild("right_lower_wing", CubeListBuilder.create()
				.texOffs(24, 23).addBox(-4.0F, 0.0F, 0.0F, 4.0F, 4.0F, 0.0F, none),
				PartPose.offset(-0.5F, 1.5F, 1.0F));
		body.addOrReplaceChild("left_lower_wing", CubeListBuilder.create()
				.texOffs(32, 23).addBox(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 0.0F, none),
				PartPose.offset(0.5F, 1.5F, 1.0F));

		return LayerDefinition.create(mesh, 64, 32);
	}

	@Override
	public void setupAnim(MushroomFairyRenderState state) {
		super.setupAnim(state);
		float t = state.ageInTicks;

		// Bob gently up and down while hovering.
		this.all.y += Mth.sin(t * 0.15F) * 1.0F - 1.5F;

		// Lean forward a little when flying along.
		this.all.xRot = Math.min(state.walkAnimationSpeed, 1.0F) * 0.35F;

		// Look at things.
		this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
		this.head.xRot = state.xRot * Mth.DEG_TO_RAD;

		// Fast, fluttery wings (swept back, beating forward and back).
		float flap = Mth.sin(t * 1.7F) * 0.55F;
		this.rightWing.yRot = 0.5F + flap;
		this.leftWing.yRot = -0.5F - flap;
		this.rightLowerWing.yRot = 0.35F + flap * 0.8F;
		this.leftLowerWing.yRot = -0.35F - flap * 0.8F;

		// Arms sway, legs dangle.
		float sway = Mth.cos(t * 0.12F) * 0.08F;
		this.rightArm.zRot = 0.18F + sway;
		this.leftArm.zRot = -0.18F - sway;
		this.rightLeg.xRot = Mth.sin(t * 0.1F) * 0.15F;
		this.leftLeg.xRot = -Mth.sin(t * 0.1F) * 0.15F;
	}
}
