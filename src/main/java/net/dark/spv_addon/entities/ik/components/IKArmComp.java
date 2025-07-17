package net.dark.spv_addon.entities.ik.components;

import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.model.BoneAccessor;
import com.sp.entity.ik.model.ModelAccessor;
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

public class IKArmComp<C extends IKChain, E extends IKAnimatable<E>>
        extends com.sp.entity.ik.components.IKLegComponent<C, E> {

    private final List<ServerLimb> endPoints;
    private final List<Vec3d> bases;
    private final List<LegSetting> settings;
    private final List<Double> stepProgress = new ArrayList<>();
    private int stillStandCounter = 0;

    @SafeVarargs
    public IKArmComp(List<LegSetting> settings, List<ServerLimb> endpoints, C... limbs) {
        super(settings, endpoints, limbs);
        this.endPoints = endpoints;
        this.bases = new ArrayList<>();
        this.settings = settings;
        Arrays.stream(limbs).forEach(limb -> this.bases.add(Vec3d.ZERO));
        for (int i = 0; i < endpoints.size(); i++) stepProgress.add(0.0);
    }

    private static boolean hasMovedOverLastTick(PathAwareEntity entity) {
        Vec3d vel = entity.getVelocity();
        float yawDelta = Math.abs(entity.getHeadYaw() - entity.prevHeadYaw);
        return vel.x != 0 || vel.z != 0 || yawDelta >= 0.01F;
    }

    public static BlockHitResult rayCastToGround(Vec3d limbBase, Entity entity, RaycastContext.FluidHandling fluid) {
        World world = entity.getWorld();
        return world.raycast(
                new RaycastContext(
                        limbBase.offset(Direction.UP, 2.5),
                        limbBase.offset(Direction.DOWN, 8),
                        RaycastContext.ShapeType.COLLIDER,
                        fluid,
                        entity
                )
        );
    }

    private Vec3d adjustForWall(PathAwareEntity entity, Vec3d from, Vec3d to) {
        World world = entity.getWorld();
        Vec3d dir = to.subtract(from);
        double dist = dir.length();
        if (dist < 0.01) return to;
        dir = dir.normalize();
        Vec3d checkTo = from.add(dir.multiply(dist + 0.15));
        BlockHitResult hit = world.raycast(new RaycastContext(
                from, checkTo,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                entity
        ));
        if (hit.getType() == BlockHitResult.Type.BLOCK) {
            return hit.getPos().subtract(dir.multiply(0.08));
        }
        return to;
    }

    @Override
    public void tickClient(E animatable, ModelAccessor model) {
        Entity entity = (Entity) animatable;
        long age = entity.age;
        boolean isWalking = entity.isOnGround() && entity.getVelocity().horizontalLengthSquared() > 0.01;

        for (int i = 0; i < this.limbs.size(); i++) {
            var optBone = model.getBone("base_arm" + (i + 1));
            if (optBone.isEmpty()) return;
            Vec3d basePos = this.bases.get(i);
            C limbChain = this.setLimb(i, basePos, entity);

            double breathing = Math.sin((age + i * 12) * 0.035) * 0.05;

            // Swing et vertical offset pour la marche
            double swingPhase = (i == 0 ? 0 : Math.PI);
            double swing = 0.0;
            double vertical = 0.0;
            if (isWalking) {
                double walkCycle = age * 0.18;
                swing = Math.sin(walkCycle + swingPhase) * 0.18;
                vertical = Math.abs(Math.cos(walkCycle + swingPhase)) * 0.10;
            }

            double progress = stepProgress.get(i);
            double handArc = 0.0;
            if (progress < 1.0) {
                double t = 0.5 - 0.5 * Math.cos(Math.PI * progress);
                handArc = Math.sin(Math.PI * t) * 0.22 + breathing;
            } else {
                handArc = breathing;
            }

            List<Vec3d> joints = limbChain.getJoints();
            List<Vec3d> interpolatedJoints = new ArrayList<>();
            double interpFactor = 0.25;

            for (int k = 0; k < joints.size(); k++) {
                Vec3d current = joints.get(k);
                Vec3d target = current;
                if (k > 0) {
                    Vec3d prev = interpolatedJoints.get(k - 1);
                    Vec3d dir = current.subtract(prev).normalize();
                    double maxAngle = Math.toRadians(80);
                    Vec3d baseDir = new Vec3d(0, -1, 0);
                    double angle = Math.acos(dir.dotProduct(baseDir));
                    if (angle > maxAngle) {
                        dir = baseDir.add(dir.subtract(baseDir).normalize().multiply(Math.tan(maxAngle)));
                        target = prev.add(dir.multiply(current.subtract(prev).length()));
                    }
                }
                if (k < joints.size() - 1) {
                    Vec3d next = joints.get(k + 1);
                    target = target.lerp(next, interpFactor);
                }
                interpolatedJoints.add(target);
            }

            for (int k = 0; k < interpolatedJoints.size() - 1; k++) {
                Vec3d start = interpolatedJoints.get(k);
                Vec3d end = interpolatedJoints.get(k + 1);

                // Appliquer swing et vertical offset à la main (dernier segment)
                if (k == interpolatedJoints.size() - 2) {
                    end = end.add(swing, handArc + vertical, -0.08 * (1.0 - progress));
                }

                var segBone = model.getBone("seg" + (k + 1) + "_arm" + (i + 1));
                if (segBone.isEmpty()) return;
                BoneAccessor segAcc = segBone.get();
                segAcc.moveTo(start, end, entity);

                BoneAccessor armSegmentAccessor = model.getBone("seg" + (k + 1) + "_arm" + (i + 1)).get();
                Vec3d modelPosWorldSpace = limbChain.getJoints().get(k);
                Vec3d targetVecWorldSpace = limbChain.getJoints().get(k + 1);

                // Décalage debug
                if (com.sp.entity.ik.util.PrAnCommonClass.shouldRenderDebugLegs) {
                    modelPosWorldSpace = modelPosWorldSpace.subtract(0.0, 200.0, 0.0);
                    targetVecWorldSpace = targetVecWorldSpace.subtract(0.0, 200.0, 0.0);
                }

                armSegmentAccessor.moveTo(modelPosWorldSpace, targetVecWorldSpace, entity);
            }
        }
    }

    @Override
    public void getModelPositions(E animatable, ModelAccessor model) {
        for (int i = 0; i < this.limbs.size(); i++) {
            var optBone = model.getBone("base_arm" + (i + 1));
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
            limb.tick(this, i);
            Vec3d offset = limb.baseOffset.multiply(this.getScale());

            if (hasMovedOverLastTick(entity)) {
                offset = offset.add(0, 0, Math.min(settings.get(i).stepInFront() * this.getScale(), 0.18));
            }
            offset = offset.rotateY((float) Math.toRadians(-entity.getBodyYaw()));
            Vec3d worldBase = offset.add(pos);
            var hit = rayCastToGround(worldBase, entity, settings.get(i).fluid());
            Vec3d target = hit.getPos();

            target = adjustForWall(entity, worldBase, target);

            double prog = stepProgress.get(i);
            if (limb.hasToBeSet) {
                boolean otherMoving = false;
                for (int j = 0; j < stepProgress.size(); j++) {
                    if (j != i && stepProgress.get(j) < 1.0) {
                        otherMoving = true;
                        break;
                    }
                }
                if (!otherMoving) {
                    prog = 0.0;
                    limb.set(target);
                    limb.hasToBeSet = false;
                }
            } else if (!target.isInRange(limb.target, this.getMaxArmFormTargetDistance(entity))) {
                boolean otherMoving = false;
                for (int j = 0; j < stepProgress.size(); j++) {
                    if (j != i && stepProgress.get(j) < 1.0) {
                        otherMoving = true;
                        break;
                    }
                }
                if (!otherMoving) {
                    prog = 0.0;
                    limb.setTarget(target);
                }
            } else {
                prog = Math.min(prog + 0.035, 1.0);
            }
            stepProgress.set(i, prog);
        }
    }

    @Override
    public C setLimb(int index, Vec3d base, Entity entity) {
        C limb = super.setLimb(index, base, entity);
        if (limb instanceof net.dark.spv_addon.entities.ik.parts.ik_chains.EntityArm arm) arm.entity = entity;
        return limb;
    }

    public void renderDebug(MatrixStack poseStack, E animatable, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (com.sp.entity.ik.util.PrAnCommonClass.shouldRenderDebugLegs) {
            for (int i = 0; i < this.limbs.size(); ++i) {
                C limb = this.limbs.get(i);
                List<Vec3d> joints = limb.getJoints();
                for (int k = 0; k < joints.size() - 1; ++k) {
                    Vec3d start = joints.get(k).subtract(0, 200, 0);
                    Vec3d end = joints.get(k + 1).subtract(0, 200, 0);
                }
            }
        }
        new com.sp.entity.ik.components.debug_renderers.LegDebugRenderer<E, C>()
                .renderDebug(this, animatable, poseStack, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }

    public double getMaxArmFormTargetDistance(PathAwareEntity entity) {
        if (stillStandCounter >= settings.get(0).standStillCounter() && hasMovedOverLastTick(entity)) {
            stillStandCounter = 0;
        } else if (stillStandCounter < settings.get(0).standStillCounter()) {
            stillStandCounter++;
        }
        return (stillStandCounter == settings.get(0).standStillCounter()
                ? settings.get(0).maxStandingStillDistance() * 0.5
                : settings.get(0).maxDistance() * 0.4)
                * this.getScale();
    }
}