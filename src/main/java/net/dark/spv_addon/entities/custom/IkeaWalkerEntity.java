package net.dark.spv_addon.entities.custom;

import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.parts.ik_chains.TargetReachingIKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import net.dark.spv_addon.entities.ik.components.IKArmComp;
import net.dark.spv_addon.entities.ik.components.IKLegCompIkeaWalker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
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

public class IkeaWalkerEntity extends PathAwareEntity
        implements IKAnimatable<IkeaWalkerEntity>, GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final IKLegCompIkeaWalker<TargetReachingIKChain, IkeaWalkerEntity> legComponent;
    private final IKArmComp<TargetReachingIKChain, IkeaWalkerEntity> armComponent;

    public IkeaWalkerEntity(EntityType<? extends IkeaWalkerEntity> type, World world) {
        super(type, world);

        // Jambes
        List<ServerLimb> legEndpoints = List.of(
                new ServerLimb(0.12, 0.0, 0.1),
                new ServerLimb(-0.12, 0.0, 0.1)
        );
        IKLegCompIkeaWalker.LegSetting legSetting = new IKLegCompIkeaWalker.LegSetting.Builder()
                .maxDistance(0.5)
                .stepInFront(0.3)
                .movementSpeed(0.15)
                .maxStandingStillDistance(0.05)
                .standStillCounter(12)
                .build();
        List<IKLegCompIkeaWalker.LegSetting> legSettings = legEndpoints.stream()
                .map(e -> legSetting).collect(Collectors.toList());

        TargetReachingIKChain leftLeg = new TargetReachingIKChain(
                new Segment.Builder().length(0.4).build(),
                new Segment.Builder().length(0.4).build()
        );
        TargetReachingIKChain rightLeg = new TargetReachingIKChain(
                new Segment.Builder().length(0.4).build(),
                new Segment.Builder().length(0.4).build()
        );

        this.legComponent = new IKLegCompIkeaWalker<>(
                legSettings, legEndpoints,
                leftLeg, rightLeg
        );

        // Bras
        List<ServerLimb> armEndpoints = List.of(
                new ServerLimb(0.2, 1.5, -0.1),
                new ServerLimb(-0.2, 1.5, -0.1)
        );
        IKArmComp.LegSetting armSetting = new IKArmComp.LegSetting.Builder()
                .maxDistance(0.5)
                .stepInFront(0.2)
                .movementSpeed(0.25)
                .maxStandingStillDistance(0.04)
                .standStillCounter(12)
                .build();
        List<IKArmComp.LegSetting> armSettings = armEndpoints.stream()
                .map(e -> armSetting).collect(Collectors.toList());

        TargetReachingIKChain leftArm = new TargetReachingIKChain(
                new Segment.Builder().length(0.05).build(),
                new Segment.Builder().length(0.45).build(),
                new Segment.Builder().length(0.35).build()

        );
        TargetReachingIKChain rightArm = new TargetReachingIKChain(
                new Segment.Builder().length(0.05).build(),
                new Segment.Builder().length(0.45).build(),
                new Segment.Builder().length(0.35).build()
        );

        this.armComponent = new IKArmComp<>(
                armSettings, armEndpoints,
                leftArm, rightArm
        );
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 400.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 50.0);
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

    @Override
    public void tick() {
        super.tick();
        legComponent.tickServer(this);
        armComponent.tickServer(this);

        // Attirer vers le joueur tenant un os
        this.getWorld().getPlayers().stream()
                .filter(player -> player.getMainHandStack().getItem().toString().toLowerCase().contains("bone"))
                .findFirst()
                .ifPresent(player -> {
                    double dx = player.getX() - this.getX();
                    double dz = player.getZ() - this.getZ();
                    double distance = Math.sqrt(dx * dx + dz * dz);
                    if (distance > 2.0) { // Garde une petite distance
                        double speed = 0.5;
                        this.getNavigation().startMovingTo(player, speed);
                    }
                });
    }

    @Override
    public List<com.sp.entity.ik.components.IKModelComponent<IkeaWalkerEntity>> getComponents() {
        return List.of(legComponent, armComponent);
    }

    @Override
    public double getSize() {
        return 2;
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