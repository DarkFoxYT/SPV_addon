package net.dark.spv_addon.entities.custom;

import com.sp.entity.ik.components.IKAnimatable;
import net.dark.spv_addon.entities.ik.components.IKLegCompDark;
import com.sp.entity.ik.parts.ik_chains.TargetReachingIKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.model.ModelAccessor;
import net.dark.spv_addon.entities.ik.components.IKLegCompIkeaWalker;
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

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final IKLegCompIkeaWalker<TargetReachingIKChain, IkeaWalkerEntity> legComponent;

    public IkeaWalkerEntity(EntityType<? extends IkeaWalkerEntity> type, World world) {
        super(type, world);

        // 1. Setup des endpoints
        List<ServerLimb> endpoints = List.of(
                new ServerLimb(0.12, 0.0,  0.1),
                new ServerLimb(-0.12, 0.0, 0.1)
        );
        // 2. Settings
        IKLegCompIkeaWalker.LegSetting setting = new IKLegCompIkeaWalker.LegSetting.Builder()
                .maxDistance(0.4)
                .stepInFront(0.2)
                .movementSpeed(0.5)
                .maxStandingStillDistance(0.05)
                .standStillCounter(12)
                .build();
        List<IKLegCompIkeaWalker.LegSetting> settings = endpoints.stream()
                .map(e -> setting).collect(Collectors.toList());

        TargetReachingIKChain leftLeg = new TargetReachingIKChain(
                new Segment.Builder().length(0.3).build(),
                new Segment.Builder().length(0.25).build(),
                new Segment.Builder().length(0.35).build(),
                new Segment.Builder().length(0.28).build()
        );
        TargetReachingIKChain rightLeg = new TargetReachingIKChain(
                new Segment.Builder().length(0.3).build(),
                new Segment.Builder().length(0.25).build(),
                new Segment.Builder().length(0.35).build(),
                new Segment.Builder().length(0.28).build()
        );

        this.legComponent = new IKLegCompIkeaWalker<>(
                settings, endpoints,
                leftLeg, rightLeg
        );
    }

    @Override
    protected void initGoals() {
        if (this.getWorld().isDay()) {
            this.goalSelector.add(0, new LookAroundGoal(this));
        } else {
        this.goalSelector.add(0, new MeleeAttackGoal(this, 1.0, true));
            this.targetSelector.add(1, new net.minecraft.entity.ai.goal.ActiveTargetGoal<>(this, net.minecraft.entity.LivingEntity.class, true));
        }
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 400.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 50.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0);
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

    }
}
