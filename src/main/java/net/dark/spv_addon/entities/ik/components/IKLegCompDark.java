// src/main/java/net/dark/spv_addon/entities/ik/components/IKLegCompDark.java
package net.dark.spv_addon.entities.ik.components;

import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.debug_renderers.LegDebugRenderer;
import com.sp.entity.ik.model.BoneAccessor;
import com.sp.entity.ik.model.ModelAccessor;
import com.sp.entity.ik.parts.ik_chains.EntityLeg;
import com.sp.entity.ik.parts.ik_chains.IKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
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

public class IKLegCompDark<C extends IKChain, E extends IKAnimatable<E>>
        extends com.sp.entity.ik.components.IKLegComponent<C, E> {

    private final List<ServerLimb> endPoints;
    private final List<Vec3d> bases;
    private final List<LegSetting> settings;
    private int stillStandCounter = 0;
    private Vec3d armTarget = null;
    private final List<Double> stepProgress = new ArrayList<>();

    @SafeVarargs
    public IKLegCompDark(List<LegSetting> settings, List<ServerLimb> endpoints, C... limbs) {
        super(settings, endpoints, limbs);
        this.endPoints = endpoints;
        this.bases = new ArrayList<>();
        this.settings = settings;
        Arrays.stream(limbs).forEach(limb -> this.bases.add(Vec3d.ZERO));
        for (int i = 0; i < endpoints.size(); i++) stepProgress.add(0.0);
    }

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
        return world.raycast(
                new RaycastContext(
                        rotatedLimbOffset.offset(Direction.UP, 3),
                        rotatedLimbOffset.offset(Direction.DOWN, 10),
                        RaycastContext.ShapeType.COLLIDER,
                        fluid,
                        entity
                )
        );
    }

    // Empêche la jambe d'aller dans un mur (collision latérale)
    private Vec3d adjustForWall(PathAwareEntity entity, Vec3d from, Vec3d to) {
        World world = entity.getWorld();
        Vec3d dir = to.subtract(from);
        double dist = dir.length();
        if (dist < 0.01) return to;
        dir = dir.normalize();
        Vec3d checkTo = from.add(dir.multiply(dist + 0.2));
        BlockHitResult hit = world.raycast(new RaycastContext(
                from, checkTo,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                entity
        ));
        if (hit.getType() == BlockHitResult.Type.BLOCK) {
            return hit.getPos().subtract(dir.multiply(0.1));
        }
        return to;
    }

    @Override
    public void tickClient(E animatable, ModelAccessor model) {
        Entity entity = (Entity) animatable;
        long age = entity.age;
        for (int i = 0; i < this.limbs.size(); i++) {
            var optBone = model.getBone("base_leg" + (i + 1));
            if (optBone.isEmpty()) return;
            Vec3d basePos = this.bases.get(i);
            C limbChain = this.setLimb(i, basePos, entity);

            // Mouvement de respiration même à l'arrêt
            double breathing = Math.sin((age + i * 10) * 0.04) * 0.07;

            // Bump vertical doux et long, type ease-in-out
            double progress = stepProgress.get(i);
            double bumpHeight = 0.0;
            if (progress < 1.0) {
                // Courbe ease-in-out pour un mouvement plus naturel
                double t = 0.5 - 0.5 * Math.cos(Math.PI * progress);
                bumpHeight = Math.sin(Math.PI * t) * 0.19 + breathing;
            } else {
                bumpHeight = breathing;
            }

            for (int k = 0; k < limbChain.getJoints().size() - 1; k++) {
                Vec3d start = limbChain.getJoints().get(k);
                Vec3d end = limbChain.getJoints().get(k + 1);
                if (k == limbChain.getJoints().size() - 2) {
                    end = end.add(0, bumpHeight, 0);
                }
                var segBone = model.getBone("seg" + (k + 1) + "_leg" + (i + 1));
                if (segBone.isEmpty()) return;
                BoneAccessor segAcc = segBone.get();
                segAcc.moveTo(start, end, entity);
            }
        }
        // Arms
        if (armTarget != null) {
            for (int i = 0; i < this.limbs.size(); i++) {
                var optArmBone = model.getBone("arm_base" + (i + 1));
                if (optArmBone.isEmpty()) continue;
                C armChain = this.setLimb(i, this.bases.get(i), entity);
                armChain.getJoints().set(armChain.getJoints().size() - 1, armTarget);
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

            // Collision murale
            target = adjustForWall(entity, worldBase, target);

            // Progression du pas plus lente et moins agressive
            double prog = stepProgress.get(i);
            if (limb.hasToBeSet) {
                prog = 0.0;
                limb.set(target);
                limb.hasToBeSet = false;
            } else if (!target.isInRange(limb.target, this.getMaxLegFormTargetDistance(entity))) {
                prog = 0.0;
                limb.setTarget(target);
            } else {
                prog = Math.min(prog + 0.045, 1.0); // Plus petit = plus lent, plus doux
            }
            stepProgress.set(i, prog);
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