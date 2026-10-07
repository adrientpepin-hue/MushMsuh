package com.mushroomforest.entity;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.phys.Vec3;

/** Lazy, bee-like flitting: hovers 1-3 blocks above the ground and drifts around. */
public class FairyWanderGoal extends Goal {
	private final PathfinderMob mob;

	public FairyWanderGoal(PathfinderMob mob) {
		this.mob = mob;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		return this.mob.getNavigation().isDone() && this.mob.getRandom().nextInt(10) == 0;
	}

	@Override
	public boolean canContinueToUse() {
		return this.mob.getNavigation().isInProgress();
	}

	@Override
	public void start() {
		Vec3 target = this.findPos();
		if (target != null) {
			this.mob.getNavigation().moveTo(this.mob.getNavigation().createPath(BlockPos.containing(target), 1), 1.0D);
		}
	}

	private Vec3 findPos() {
		Vec3 view = this.mob.getViewVector(0.0F);
		Vec3 hover = HoverRandomPos.getPos(this.mob, 8, 7, view.x, view.z, (float) (Math.PI / 2.0), 3, 1);
		return hover != null ? hover : AirAndWaterRandomPos.getPos(this.mob, 8, 4, -2, view.x, view.z, Math.PI / 2.0);
	}
}
