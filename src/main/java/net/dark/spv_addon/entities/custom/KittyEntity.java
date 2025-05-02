package net.dark.spv_addon.entities.custom;

import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.IKModelComponent;
import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.parts.ik_chains.TargetReachingIKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import com.sp.entity.ik.model.ModelAccessor;
import net.dark.spv_addon.entities.ik.components.IKLegCompKitty;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.List;
import java.util.stream.Collectors;

public class KittyEntity extends PathAwareEntity implements IKAnimatable<KittyEntity>, GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final IKLegCompKitty<TargetReachingIKChain, KittyEntity> legComponent;
    private final IKLegCompKitty<TargetReachingIKChain, KittyEntity> armComponent;

    public KittyEntity(EntityType<? extends KittyEntity> type, World world) {
        super(type, world);

        List<ServerLimb> legs = List.of(
                new ServerLimb(1.5, 0.0, 2),
                new ServerLimb(-1.5, 0.0, 2)
        );
        var legSettings = legs.stream().map(e -> new IKLegCompKitty.LegSetting.Builder()
                .maxDistance(1.5)
                .stepInFront(1)
                .movementSpeed(0.6)
                .maxStandingStillDistance(0.1)
                .standStillCounter(20)
                .build()).collect(Collectors.toList());
        TargetReachingIKChain legChain = new TargetReachingIKChain(
                new Segment.Builder().length(0.6).build(),
                new Segment.Builder().length(0.8).build(),
                new Segment.Builder().length(1.2).build(),
                new Segment.Builder().length(0.8).build()
        );
        this.legComponent = new IKLegCompKitty<>("base_leg", legSettings, legs, legChain, legChain);

        List<ServerLimb> arms = List.of(
                new ServerLimb(0.8, 1.5, 0),
                new ServerLimb(-0.8, 1.5, 0)
        );
        var armSettings = arms.stream().map(e -> new IKLegCompKitty.LegSetting.Builder()
                .maxDistance(2.0)
                .stepInFront(0)
                .movementSpeed(0.4)
                .maxStandingStillDistance(0.0)
                .standStillCounter(0)
                .build()).collect(Collectors.toList());
        TargetReachingIKChain armChain = new TargetReachingIKChain(
                new Segment.Builder().length(0.6).build(),
                new Segment.Builder().length(0.5).build(),
                new Segment.Builder().length(0.5).build()
        );
        this.armComponent = new IKLegCompKitty<>("arm_base", armSettings, arms, armChain, armChain);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(1, new LookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        legComponent.tickServer(this);
        armComponent.tickServer(this);

        if (getTarget() != null) {
            var pos = getTarget().getEyePos();
            armComponent.setArmTarget(pos.x, pos.y, pos.z);
        }
    }

    @Override
    public List<IKModelComponent<KittyEntity>> getComponents() {
        return List.of(legComponent, armComponent);
    }

    public void applyModelPose(ModelAccessor model) {
        legComponent.getModelPositions(this, model);
        legComponent.tickClient(this, model);

        armComponent.getModelPositions(this, model);
        armComponent.tickClient(this, model);
    }

    @Override public double getSize() { return 1.0; }
    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override public double getTick(Object o) { return age; }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}
}
