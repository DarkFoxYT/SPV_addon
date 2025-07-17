package net.dark.spv_addon.entities.custom;

import com.sp.entity.custom.SkinWalkerEntity;
import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.IKModelComponent;
import com.sp.entity.ik.model.ModelAccessor;
import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.parts.ik_chains.TargetReachingIKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import net.dark.spv_addon.entities.ai.SlightlyBetterMobNavigation;
import net.dark.spv_addon.entities.ai.goals.AggroNearestPlayerGoal;
import net.dark.spv_addon.entities.ai.goals.BellWalkerStalkGoal;
import net.dark.spv_addon.entities.ik.components.IKLegCompDark;
import net.dark.spv_addon.init.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class BellWalkerEntity extends PathAwareEntity
        implements IKAnimatable<BellWalkerEntity>, GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final IKLegCompDark<TargetReachingIKChain, BellWalkerEntity> legComponent;

    private PlayerEntity stalkTarget = null;
    private int stalkCooldown = 0;
    private Vec3d lastHeardPos = null;

    public BellWalkerEntity(EntityType<? extends BellWalkerEntity> type, World world) {
        super(type, world);
        this.navigation = new SlightlyBetterMobNavigation(this, world);


        List<ServerLimb> endpoints = List.of(
                new ServerLimb(new Vec3d(1.25, 0.0, 1.25)),
                new ServerLimb(new Vec3d(-1.25, 0.0, 1.25)),
                new ServerLimb(new Vec3d(1.25, 0.0, 0.1)),
                new ServerLimb(new Vec3d(-1.25, 0.0, 0.1)),
                new ServerLimb(new Vec3d(1.25, 0.0, -1.25)),
                new ServerLimb(new Vec3d(-1.25, 0.0, -1.25))
        );
        IKLegCompDark.LegSetting setting = new IKLegCompDark.LegSetting.Builder()
                .maxDistance(0.5)
                .stepInFront(1)
                .movementSpeed(0.7)
                .maxStandingStillDistance(0.2)
                .standStillCounter(20)
                .build();
        List<IKLegCompDark.LegSetting> settings = endpoints.stream()
                .map(e -> setting).collect(Collectors.toList());
        TargetReachingIKChain chain = new TargetReachingIKChain(
                new Segment.Builder().length(0.20).build(),
                new Segment.Builder().length(0.60).build(),
                new Segment.Builder().length(0.80).build(),
                new Segment.Builder().length(0.40).build()
        );
        this.legComponent = new IKLegCompDark<>(
                settings, endpoints,
                chain, chain, chain,
                chain, chain, chain
        );
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 3000.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.5)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1000.0);
    }

    @Override
    public boolean damage(net.minecraft.entity.damage.DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(net.minecraft.entity.damage.DamageSource damageSource) {
        return true;
    }

    public void clearStalkTarget() {
        this.stalkTarget = null;
        this.stalkCooldown = 0;
        this.lastHeardPos = null;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 0.75, true));
        this.goalSelector.add(1, new LookAroundGoal(this));
        this.goalSelector.add(2, new SmartWanderGoal(this, 0.5, 10, 60));
        this.goalSelector.add(1, new AggroNearestPlayerGoal(this, 20.0));
        this.goalSelector.add(1, new BellWalkerStalkGoal(this, 0.75, 0.30));
    }

    public void onPlayerSoundHeard(PlayerEntity player, Vec3d pos) {
        this.stalkTarget = player;
        this.lastHeardPos = pos;
        this.stalkCooldown = 100;
    }

    public boolean hasStalkTarget() {
        return stalkTarget != null && stalkCooldown > 0 && stalkTarget.isAlive() && stalkTarget.distanceTo(this) < 32;
    }

    public PlayerEntity getStalkTarget() {
        return hasStalkTarget() ? stalkTarget : null;
    }

    @Override
    public void tick() {
        super.tick();
        legComponent.tickServer(this);

        if (stalkCooldown > 0) {
            stalkCooldown--;
            if (stalkCooldown == 0) stalkTarget = null;
        }
        if (!hasStalkTarget() && age % 40 == 0 && this.getWorld().random.nextInt(8) == 0) {
            this.playSound(ModSounds.BELLWALKER_BELL, 1.0f, 1.0f);
        }
        if (!this.getWorld().isClient) {
            double detectRadius = 18.0;
            for (PlayerEntity player : this.getWorld().getPlayers()) {
                if (net.dark.spv_addon.voicechat.SpvAddonVoicechatPlugin.justMadeNoise.contains(player.getUuid())
                        && player.squaredDistanceTo(this) < detectRadius * detectRadius
                        && !player.isSpectator() && player.isAlive()) {
                    this.onPlayerSoundHeard(player, player.getPos());
                }
            }
        }
        if (hasStalkTarget()) {
            this.setTarget(this.stalkTarget);
        }
    }

    @Override
    public List<IKModelComponent<BellWalkerEntity>> getComponents() {
        return List.of(legComponent);
    }

    @Override
    public double getSize() {
        return 1.0;
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object o) {
        return 0;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    public void applyModelPose(ModelAccessor model) {
        legComponent.getModelPositions(this, model);
        legComponent.tickClient(this, model);
    }

    public static class SmartWanderGoal extends Goal {
        private final PathAwareEntity mob;
        private final double speed;
        private final int minIdle, maxIdle;
        private int idleTicks;
        private boolean idlePhase = true;

        public SmartWanderGoal(PathAwareEntity mob, double speed, int minIdle, int maxIdle) {
            this.mob = mob;
            this.speed = speed;
            this.minIdle = minIdle;
            this.maxIdle = maxIdle;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
            resetIdle();
        }

        private void resetIdle() {
            int range = maxIdle - minIdle;
            idleTicks = minIdle + mob.getRandom().nextInt(range + 1);
        }

        @Override
        public boolean canStart() {
            return mob.getNavigation().isIdle();
        }

        @Override
        public boolean shouldContinue() {
            return true;
        }

        @Override
        public void tick() {
            if (idlePhase) {
                if (--idleTicks <= 0) {
                    double dx = (mob.getRandom().nextDouble() * 2 - 1) * 10;
                    double dz = (mob.getRandom().nextDouble() * 2 - 1) * 10;
                    BlockPos dest = mob.getBlockPos().add((int) dx, 0, (int) dz);
                    mob.getNavigation().startMovingTo(
                            dest.getX(), dest.getY(), dest.getZ(), speed
                    );
                    idlePhase = false;
                }
            } else {
                if (mob.getNavigation().isIdle()) {
                    mob.playSound(ModSounds.BELLWALKER_BELL, 0.5f, 0.5f);
                    idlePhase = true;
                    resetIdle();
                }
            }
        }
    }
}