package net.dark.spv_addon.entities.custom;

import com.sp.entity.ik.components.IKAnimatable;
import net.dark.spv_addon.entities.ai.goals.AggroNearestPlayerGoal;
import net.dark.spv_addon.entities.ai.goals.AggroNearestPlayerGoalSmart;
import net.dark.spv_addon.entities.ik.components.IKLegCompDark;
import com.sp.entity.ik.parts.ik_chains.TargetReachingIKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.model.ModelAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.List;
import java.util.stream.Collectors;

public class IkeaWalkerEntity extends PathAwareEntity
        implements IKAnimatable<IkeaWalkerEntity>, GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache((GeoAnimatable) this);
    private final IKLegCompDark<TargetReachingIKChain, IkeaWalkerEntity> legComponent;

    public IkeaWalkerEntity(EntityType<? extends IkeaWalkerEntity> type, World world) {
        super(type, world);

        // 1. Setup des endpoints
        List<ServerLimb> endpoints = List.of(
                new ServerLimb(0.1, 0.0,  0.1), // jambe gauche
                new ServerLimb(-0.1, 0.0, 0.1)  // jambe droite
        );
        // 2. Settings
        IKLegCompDark.LegSetting setting = new IKLegCompDark.LegSetting.Builder()
                .maxDistance(0.5)
                .stepInFront(0.7)
                .movementSpeed(0.7)
                .maxStandingStillDistance(0.05)
                .standStillCounter(18)
                .build();
        List<IKLegCompDark.LegSetting> settings = endpoints.stream()
                .map(e -> setting).collect(Collectors.toList());

        // 3. Chaînes IK
        TargetReachingIKChain leftLeg = new TargetReachingIKChain(
                new Segment.Builder().length(0.2).build(),
                new Segment.Builder().length(0.23).build(),
                new Segment.Builder().length(0.25).build(),
                new Segment.Builder().length(0.29).build()
        );
        TargetReachingIKChain rightLeg = new TargetReachingIKChain(
                new Segment.Builder().length(0.2).build(),
                new Segment.Builder().length(0.23).build(),
                new Segment.Builder().length(0.25).build(),
                new Segment.Builder().length(0.29).build()
        );

        this.legComponent = new IKLegCompDark<>(
                settings, endpoints,
                leftLeg, rightLeg
        );
    }

    @Override
    protected void initGoals() {
        // 0: melee if in range
        this.goalSelector.add(0, new MeleeAttackGoal(this, 1.0, true));
        // 1: look around when idle
        this.goalSelector.add(1, new LookAroundGoal(this));
        // 2: smart wander (move, pause, repeat)
        this.goalSelector.add(2, new AggroNearestPlayerGoalSmart(this, 0.5, 20, 60));

    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 400.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0);
    }


    @Override
    public void tick() {
        super.tick();
        legComponent.tickServer(this);
    }

    @Override
    public List<com.sp.entity.ik.components.IKModelComponent<IkeaWalkerEntity>> getComponents() {
        return List.of(legComponent);
    }

    @Override
    public double getSize() {
        return 1.5;
    }

    @Override
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
}
