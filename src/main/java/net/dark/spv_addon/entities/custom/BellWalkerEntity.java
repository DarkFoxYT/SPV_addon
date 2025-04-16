package net.dark.spv_addon.entities.custom;

import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.IKLegComponent;
import com.sp.entity.ik.components.IKModelComponent;
import com.sp.entity.ik.parts.ik_chains.TargetReachingIKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.model.ModelAccessor;
import net.minecraft.entity.EntityType;
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

public class BellWalkerEntity extends PathAwareEntity implements IKAnimatable<BellWalkerEntity>, GeoAnimatable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache((GeoAnimatable) this);
    private final IKLegComponent<TargetReachingIKChain, BellWalkerEntity> legComponent;

    public BellWalkerEntity(EntityType<? extends BellWalkerEntity> type, World world) {
        super(type, world);


        List<ServerLimb> endpoints = List.of(
                new ServerLimb( 0.5, 0.0,  0.8),
                new ServerLimb(-0.5, 0.0,  0.8),
                new ServerLimb( 0.8, 0.0,  0.2),
                new ServerLimb(-0.8, 0.0,  0.2),
                new ServerLimb( 0.5, 0.0, -0.6),
                new ServerLimb(-0.5, 0.0, -0.6)
        );


        IKLegComponent.LegSetting setting = new IKLegComponent.LegSetting.Builder()
                .maxDistance(1.5)
                .stepInFront(1.0)
                .movementSpeed(0.7)
                .maxStandingStillDistance(0.1)
                .standStillCounter(20)
                .build();
        List<IKLegComponent.LegSetting> settings = endpoints.stream()
                .map(e -> setting)
                .collect(Collectors.toList());


        TargetReachingIKChain chainTemplate = new TargetReachingIKChain(
                new Segment.Builder().length(0.65).build(),
                new Segment.Builder().length(1.00).build(),
                new Segment.Builder().length(1.30).build(),
                new Segment.Builder().length(0.85).build()
        );

        this.legComponent = new IKLegComponent<>(
                settings,
                endpoints,
                chainTemplate, chainTemplate, chainTemplate,
                chainTemplate, chainTemplate, chainTemplate
        );
    }


    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0);
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


    @Override
    public void tick() {
        super.tick();
        legComponent.tickServer(this);
    }

    public void applyModelPose(ModelAccessor model) {
        legComponent.getModelPositions(this, model);
        legComponent.tickClient(this, model);
    }
}
