package net.dark.spv_addon.entities.ik.components;

import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.debug_renderers.LegDebugRenderer;
import com.sp.entity.ik.model.BoneAccessor;
import com.sp.entity.ik.model.ModelAccessor;
import com.sp.entity.ik.parts.ik_chains.EntityLeg;
import com.sp.entity.ik.parts.ik_chains.EntityLegWithFoot;
import com.sp.entity.ik.parts.ik_chains.IKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import com.sp.entity.ik.util.PrAnCommonClass;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Stands in for both legs and arms IK—
 * for legs, behaves as before; for arms, you can call setArmTarget() then tickClient will point chains toward it.
 */
public class IKLegCompDark<C extends IKChain, E extends IKAnimatable<E>>
        extends com.sp.entity.ik.components.IKLegComponent<C, E> {

    //---- leg-specific fields:
    private final List<ServerLimb> endPoints;
    private final List<Vec3d> bases;
    private final List<LegSetting> settings;
    private int stillStandCounter = 0;

    //---- arm-target for pointing chains (if your geo JSON has bones "arm_chain1_jointX" etc)
    private Vec3d armTarget = null;

    @SafeVarargs
    public IKLegCompDark(List<LegSetting> settings, List<ServerLimb> endpoints, C... limbs) {
        super(settings, endpoints, limbs);
        this.endPoints = endpoints;
        this.bases = new ArrayList<>();
        this.settings = settings;
        Arrays.stream(limbs).forEach(limb -> this.bases.add(Vec3d.ZERO));
    }

    /**
     * Call this to point the arm chains at world space (x,y,z)
     */
    public void setArmTarget(double x, double y, double z) {
        this.armTarget = new Vec3d(x, y, z);
    }

    private static boolean hasMovedOverLastTick(PathAwareEntity entity) {
        Vec3d vel = entity.getVelocity();
        float yawDelta = Math.abs(entity.getHeadYaw() - entity.prevHeadYaw);
        return vel.x != 0 || vel.z != 0 || yawDelta >= 0.01F;
    }

    public static BlockHitResult rayCastToGround(Vec3d rotatedLimbOffset, Entity entity, RaycastContext.FluidHandling fluid) {
        World world = entity.getWorld();
        BlockHitResult hit = world.raycast(
                new RaycastContext(
                        rotatedLimbOffset.offset(Direction.UP, 3),
                        rotatedLimbOffset.offset(Direction.DOWN, 10),
                        RaycastContext.ShapeType.COLLIDER,
                        fluid,
                        entity
                )
        );
        return hit;
    }

    @Override
    public void tickClient(E animatable, ModelAccessor model) {
        Entity entity = (Entity) animatable;
        // 1) legs as before
        for (int i = 0; i < this.limbs.size(); i++) {
            var optBone = model.getBone("base_leg" + (i + 1));
            if (optBone.isEmpty()) return;
            Vec3d basePos = this.bases.get(i);
            C limbChain = this.setLimb(i, basePos, entity);
            // iterate each segment
            for (int k = 0; k < limbChain.getJoints().size() - 1; k++) {
                Vec3d start = limbChain.getJoints().get(k);
                Vec3d end = limbChain.getJoints().get(k + 1);
                var segBone = model.getBone("seg" + (k + 1) + "_leg" + (i + 1));
                if (segBone.isEmpty()) return;
                BoneAccessor segAcc = segBone.get();
                segAcc.moveTo(start, end, entity);

            }
        }
        // 2) arms pointing if target set
        if (armTarget != null) {
            // assume your geo JSON has bones named "arm_base1"... etc and chain joints
            for (int i = 0; i < this.limbs.size(); i++) {
                var optArmBone = model.getBone("arm_base" + (i + 1));
                if (optArmBone.isEmpty()) continue;
                // here reuse same limbs array as arms chain lengths; adjust C[] in constructor if needed
                C armChain = this.setLimb(i, this.bases.get(i), entity);
                // assign each chain to point at armTarget
                armChain.getJoints().set(armChain.getJoints().size() - 1, armTarget);
                // now move each segment bone
                for (int k = 0; k < armChain.getJoints().size() - 1; k++) {
                    Vec3d start = armChain.getJoints().get(k);
                    Vec3d end = armChain.getJoints().get(k + 1);
                    var segBone = model.getBone("seg" + (k + 1) + "_arm" + (i + 1));
                    if (segBone.isEmpty()) continue;
                    BoneAccessor segAcc = segBone.get();
                    segAcc.moveTo(start, end, entity);
                }
            }
        }
    }

    @Override
    public void getModelPositions(E animatable, ModelAccessor model) {
        for (int i = 0; i < this.limbs.size(); i++) {
            var optBone = model.getBone("base_leg" + (i + 1));
            if (optBone.isEmpty()) return;
            BoneAccessor baseAcc = optBone.get();
            this.bases.set(i, baseAcc.getPosition());
        }
    }

    @Override
    public void tickServer(E animatable) {
        super.tickServer(animatable);
        PathAwareEntity entity = (PathAwareEntity) animatable;
        Vec3d pos = entity.getPos();
        for (int i = 0; i < endPoints.size(); i++) {
            ServerLimb limb = endPoints.get(i);
            limb.tick(this, i, this.settings.get(i).movementSpeed());
            Vec3d offset = limb.baseOffset.multiply(this.getScale());
            if (hasMovedOverLastTick(entity)) {
                offset = offset.add(0, 0, settings.get(i).stepInFront() * this.getScale());
            }
            offset = offset.rotateY((float) Math.toRadians(-entity.getBodyYaw()));
            Vec3d worldBase = offset.add(pos);
            var hit = rayCastToGround(worldBase, entity, settings.get(i).fluid());
            Vec3d target = hit.getPos();
            if (limb.hasToBeSet) {
                limb.set(target);
                limb.hasToBeSet = false;
            }
            if (!target.isInRange(limb.target, this.getMaxLegFormTargetDistance(entity))) {
                limb.setTarget(target);
            }
        }
    }

    @Override
    public C setLimb(int index, Vec3d base, Entity entity) {
        C limb = super.setLimb(index, base, entity);
        if (limb instanceof EntityLeg leg) leg.entity = entity;
        return limb;
    }

    public void renderDebug(MatrixStack stack, E animatable,
                            RenderLayer layer, VertexConsumerProvider pipes,
                            VertexConsumer vb, float pt, int light, int overlay) {
        new LegDebugRenderer<E, C>()
                .renderDebug(this, animatable, stack, layer, pipes, vb, pt, light, overlay);
    }

    public double getMaxLegFormTargetDistance(PathAwareEntity entity) {
        if (stillStandCounter >= settings.get(0).standStillCounter() && hasMovedOverLastTick(entity)) {
            stillStandCounter = 0;
        } else if (stillStandCounter < settings.get(0).standStillCounter()) {
            stillStandCounter++;
        }
        return (stillStandCounter == settings.get(0).standStillCounter()
                ? settings.get(0).maxStandingStillDistance()
                : settings.get(0).maxDistance())
                * this.getScale();
    }
}
