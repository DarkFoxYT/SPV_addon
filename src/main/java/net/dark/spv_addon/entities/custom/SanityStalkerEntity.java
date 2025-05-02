package net.dark.spv_addon.entities.custom;

import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.IKModelComponent;
import com.sp.entity.ik.model.ModelAccessor;
import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.parts.ik_chains.TargetReachingIKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.entities.ai.goals.AggroLowSanityPlayerGoal;
import net.dark.spv_addon.entities.ik.components.IKLegCompSanity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.stream.Collectors;

public class SanityStalkerEntity extends PathAwareEntity implements IKAnimatable<SanityStalkerEntity>, GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final IKLegCompSanity<TargetReachingIKChain, SanityStalkerEntity> legComponent;

    public SanityStalkerEntity(EntityType<? extends SanityStalkerEntity> type, World world) {
        super(type, world);

        List<ServerLimb> endpoints = List.of(
                new ServerLimb(1.3, 0.0, 1.5),
                new ServerLimb(-1.3, 0.0, 1.5)
        );

        var settings = endpoints.stream().map(e ->
                new IKLegCompSanity.LegSetting.Builder()
                        .maxDistance(1.2)
                        .stepInFront(1)
                        .movementSpeed(0.5)
                        .maxStandingStillDistance(0.1)
                        .standStillCounter(20)
                        .build()
        ).collect(Collectors.toList());

        TargetReachingIKChain chain = new TargetReachingIKChain(
                new Segment.Builder().length(0.4).build(),
                new Segment.Builder().length(0.6).build(),
                new Segment.Builder().length(0.7).build()
        );

        legComponent = new IKLegCompSanity(settings, endpoints, chain, chain);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new AggroLowSanityPlayerGoal(this, 32.0));
    }

    @Override
    public void tick() {
        super.tick();
        legComponent.tickServer(this);
    }

    @Override
    public List<IKModelComponent<SanityStalkerEntity>> getComponents() {
        return List.of(legComponent);
    }

    public void applyModelPose(ModelAccessor model) {
        legComponent.getModelPositions(this, model);
        legComponent.tickClient(this, model);
    }

    @Override
    public double getSize() {
        return 0.8;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object o) {
        return age;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.fixed(0.6F, 1.4F); // smaller
    }
}
