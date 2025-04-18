package net.dark.spv_addon.entities.custom;

import com.sp.entity.ik.components.IKAnimatable;
import net.dark.spv_addon.entities.ai.goals.AggroNearestPlayerGoal;
import net.dark.spv_addon.entities.ai.SlightlyBetterMobNavigation;
import com.sp.entity.ik.components.IKModelComponent;
import com.sp.entity.ik.parts.ik_chains.TargetReachingIKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.model.ModelAccessor;
import net.dark.spv_addon.init.ModSounds;
import net.dark.spv_addon.entities.ik.components.IKLegCompDark;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class BellWalkerEntity extends PathAwareEntity
        implements IKAnimatable<BellWalkerEntity>, GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache((GeoAnimatable) this);
    private final IKLegCompDark<TargetReachingIKChain, BellWalkerEntity> legComponent;

    public BellWalkerEntity(EntityType<? extends BellWalkerEntity> type, World world) {
        super(type, world);
        this.navigation = new SlightlyBetterMobNavigation(this, world);

        List<ServerLimb> endpoints = List.of(
                new ServerLimb( 1.5, 0.0,  2),
                new ServerLimb(-1.5, 0.0,  2),
                new ServerLimb( 1.8, 0.0,  0),
                new ServerLimb(-1.8, 0.0,  0),
                new ServerLimb( 1.5, 0.0, -2),
                new ServerLimb(-1.5, 0.0, -2)
        );
        IKLegCompDark.LegSetting setting = new IKLegCompDark.LegSetting.Builder()
                .maxDistance(1.5)
                .stepInFront(1)
                .movementSpeed(0.7)
                .maxStandingStillDistance(0.1)
                .standStillCounter(20)
                .build();
        List<IKLegCompDark.LegSetting> settings = endpoints.stream()
                .map(e -> setting).collect(Collectors.toList());
        TargetReachingIKChain chain = new TargetReachingIKChain(
                new Segment.Builder().length(0.65).build(),
                new Segment.Builder().length(1.00).build(),
                new Segment.Builder().length(1.30).build(),
                new Segment.Builder().length(0.85).build()
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
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0);
    }

    @Override
    protected void initGoals() {
        // 0: melee if in range
        this.goalSelector.add(0, new MeleeAttackGoal(this, 1.0, true));
        // 1: look around when idle
        this.goalSelector.add(1, new LookAroundGoal(this));
        // 2: smart wander (move, pause, repeat)
        this.goalSelector.add(2, new SmartWanderGoal(this, 0.5, 20, 60));
        // 3: agro nearest player who spoke
        this.goalSelector.add(3, new AggroNearestPlayerGoal(this, 20.0));

    }

    @Override
    public void tick() {
        super.tick();
        legComponent.tickServer(this);
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
        // no custom GeckoLib controllers here
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
                    // pick a reachable random target within 10 blocks
                    double dx = (mob.getRandom().nextDouble() * 2 - 1) * 10;
                    double dz = (mob.getRandom().nextDouble() * 2 - 1) * 10;
                    BlockPos dest = mob.getBlockPos().add((int)dx, 0, (int)dz);
                    mob.getNavigation().startMovingTo(
                            dest.getX(), dest.getY(), dest.getZ(), speed
                    );
                    idlePhase = false;
                }
            } else {
                if (mob.getNavigation().isIdle()) {
                    // movement finished: play your custom stop sound
                    mob.playSound(ModSounds.BELLWALKER_BELL, 1.0f, 1.0f);

                    idlePhase = true;
                    resetIdle();
                }
            }
        }
    }
}
