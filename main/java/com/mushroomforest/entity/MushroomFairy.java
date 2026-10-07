package com.mushroomforest.entity;

import com.mushroomforest.registry.ModSounds;
import com.mushroomforest.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;

/**
 * A little mushroom fairy: a tiny flying sprite with a toadstool for a hat.
 *
 * Completely peaceful. It drifts around the mushroom forest, follows anyone
 * holding a mushroom, and if you hand it one while you're hurt it takes the
 * mushroom and heals you by one heart. If you're already at full health it
 * just giggles and lets you keep your mushroom.
 */
public class MushroomFairy extends PathfinderMob {
	/** One heart = two health points. */
	private static final float HEAL_AMOUNT = 2.0F;

	/** Soft pink, gold, mint and lilac fairy dust. */
	private static final int[] DUST_COLORS = {0xFFB7E3, 0xFFE38A, 0xB8F5D2, 0xD7B8FF};

	public MushroomFairy(EntityType<? extends MushroomFairy> type, Level level) {
		super(type, level);
		this.moveControl = new FlyingMoveControl(this, 20, true);
		this.setPathfindingMalus(PathType.FIRE_IN_NEIGHBOR, -1.0F);
		this.setPathfindingMalus(PathType.WATER, -1.0F);
		this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
		this.setPathfindingMalus(PathType.FENCE, -1.0F);
	}

	public static AttributeSupplier.Builder createAttributes() {
		// Animal attributes include the tempt range used by TemptGoal.
		return Animal.createAnimalAttributes()
				.add(Attributes.MAX_HEALTH, 8.0D)
				.add(Attributes.FLYING_SPEED, 0.5D)
				.add(Attributes.MOVEMENT_SPEED, 0.25D);
	}

	/** Fairies appear above grass, moss and similar soft ground, day or night. */
	public static boolean checkFairySpawnRules(EntityType<? extends MushroomFairy> type, ServerLevelAccessor level, EntitySpawnReason reason, BlockPos pos, RandomSource random) {
		return level.getBlockState(pos.below()).is(ModTags.FAIRIES_SPAWNABLE_ON);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.5D));
		this.goalSelector.addGoal(2, new TemptGoal(this, 1.1D, stack -> stack.is(ModTags.FAIRY_TREATS), false));
		this.goalSelector.addGoal(3, new FairyWanderGoal(this));
		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
		navigation.setCanOpenDoors(false);
		navigation.setCanFloat(true);
		return navigation;
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!stack.is(ModTags.FAIRY_TREATS)) {
			return super.mobInteract(player, hand);
		}

		if (!this.level().isClientSide()) {
			ServerLevel serverLevel = (ServerLevel) this.level();

			if (player.getHealth() < player.getMaxHealth()) {
				// Thank you for the mushroom! Have a heart.
				stack.consume(1, player);
				player.heal(HEAL_AMOUNT);

				serverLevel.sendParticles(ParticleTypes.HEART,
						player.getX(), player.getY() + player.getBbHeight() * 0.7D, player.getZ(),
						3, 0.3D, 0.2D, 0.3D, 0.02D);
				serverLevel.sendParticles(new DustParticleOptions(DUST_COLORS[0], 1.0F),
						this.getX(), this.getY() + 0.3D, this.getZ(),
						14, 0.3D, 0.3D, 0.3D, 0.0D);
				this.level().playSound(null, this.blockPosition(), ModSounds.FAIRY_HEAL, SoundSource.NEUTRAL,
						1.0F, 0.95F + this.random.nextFloat() * 0.2F);
			} else {
				// Already healthy: a happy little twinkle, and you keep your mushroom.
				serverLevel.sendParticles(ParticleTypes.END_ROD,
						this.getX(), this.getY() + 0.4D, this.getZ(),
						6, 0.25D, 0.25D, 0.25D, 0.02D);
				this.level().playSound(null, this.blockPosition(), ModSounds.FAIRY_GIGGLE, SoundSource.NEUTRAL,
						1.0F, 1.0F + this.random.nextFloat() * 0.2F);
			}
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void aiStep() {
		super.aiStep();

		// A faint trail of sparkly fairy dust.
		if (this.level().isClientSide() && this.random.nextInt(5) == 0) {
			ParticleOptions particle = this.random.nextInt(4) == 0
					? ParticleTypes.END_ROD
					: new DustParticleOptions(DUST_COLORS[this.random.nextInt(DUST_COLORS.length)], 0.6F);
			this.level().addParticle(particle,
					this.getX() + (this.random.nextDouble() - 0.5D) * 0.4D,
					this.getY() + 0.15D + this.random.nextDouble() * 0.35D,
					this.getZ() + (this.random.nextDouble() - 0.5D) * 0.4D,
					0.0D, -0.015D, 0.0D);
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return ModSounds.FAIRY_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return ModSounds.FAIRY_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ModSounds.FAIRY_DEATH;
	}

	@Override
	public boolean causeFallDamage(double fallDistance, float multiplier, DamageSource source) {
		return false;
	}

	@Override
	protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
	}

	@Override
	public boolean isFlapping() {
		return true;
	}

	/** Fairies stay put like farm animals instead of despawning when you walk away. */
	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}
}
