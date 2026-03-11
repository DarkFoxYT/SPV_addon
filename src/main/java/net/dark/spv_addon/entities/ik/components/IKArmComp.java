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
import net.minecraft.util.math.Vec3d;

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
        for (int i = 0; i < endpoints.size(); i++) {
            stepProgress.add(0.0);
        }
    }

    @Override
    public void tickClient(E animatable, ModelAccessor model) {
        Entity entity = (Entity) animatable;
        long age = entity.age;
        boolean walking = entity.isOnGround() && entity.getVelocity().horizontalLengthSquared() > 0.01;

        for (int i = 0; i < this.limbs.size(); i++) {
            var optBone = model.getBone("base_arm" + (i + 1));
            if (optBone.isEmpty()) {
                return;
            }

            C chain = this.setLimb(i, this.bases.get(i), entity);
            double breathing = Math.sin((age + i * 12) * 0.035) * 0.05;
            double swingPhase = i == 0 ? 0.0 : Math.PI;
            double swing = 0.0;
            double vertical = 0.0;
            if (walking) {
                double walkCycle = age * 0.18;
                swing = Math.sin(walkCycle + swingPhase) * 0.18;
                vertical = Math.abs(Math.cos(walkCycle + swingPhase)) * 0.10;
            }

            double progress = stepProgress.get(i);
            double handArc = breathing;
            if (progress < 1.0) {
                double t = 0.5 - 0.5 * Math.cos(Math.PI * progress);
                handArc = Math.sin(Math.PI * t) * 0.22 + breathing;
            }

            List<Vec3d> joints = chain.getJoints();
            for (int k = 0; k < joints.size() - 1; k++) {
                Vec3d start = joints.get(k);
                Vec3d end = joints.get(k + 1);

                if (k == joints.size() - 2) {
                    end = end.add(swing, handArc + vertical, -0.08 * (1.0 - progress));
                }

                var segBone = model.getBone("seg" + (k + 1) + "_arm" + (i + 1));
                if (segBone.isEmpty()) {
                    return;
                }
                BoneAccessor segAcc = segBone.get();
                segAcc.moveTo(start, end, entity);
            }
        }
    }

    @Override
    public void getModelPositions(E animatable, ModelAccessor model) {
        for (int i = 0; i < this.limbs.size(); i++) {
            var optBone = model.getBone("base_arm" + (i + 1));
            if (optBone.isEmpty()) {
                return;
            }
            this.bases.set(i, optBone.get().getPosition());
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

            if (IKMovementUtil.hasMovedOverLastTick(entity)) {
                offset = offset.add(0, 0, Math.min(settings.get(i).stepInFront() * this.getScale(), 0.18));
            }
            offset = offset.rotateY((float) Math.toRadians(-entity.getBodyYaw()));
            Vec3d worldBase = offset.add(pos);
            Vec3d target = IKMovementUtil.rayCastToGround(worldBase, entity, 2.5, 8.0, settings.get(i).fluid()).getPos();
            target = IKMovementUtil.adjustForWall(entity, worldBase, target, 0.15);

            double prog = stepProgress.get(i);
            boolean otherMoving = false;
            for (int j = 0; j < stepProgress.size(); j++) {
                if (j != i && stepProgress.get(j) < 1.0) {
                    otherMoving = true;
                    break;
                }
            }

            if (limb.hasToBeSet && !otherMoving) {
                prog = 0.0;
                limb.set(target);
                limb.hasToBeSet = false;
            } else if (!target.isInRange(limb.target, this.getMaxArmFormTargetDistance(entity)) && !otherMoving) {
                prog = 0.0;
                limb.setTarget(target);
            } else {
                prog = Math.min(prog + 0.035, 1.0);
            }
            stepProgress.set(i, prog);
        }
    }

    @Override
    public C setLimb(int index, Vec3d base, Entity entity) {
        C limb = super.setLimb(index, base, entity);
        if (limb instanceof net.dark.spv_addon.entities.ik.parts.ik_chains.EntityArm arm) {
            arm.entity = entity;
        }
        return limb;
    }

    public void renderDebug(MatrixStack poseStack, E animatable, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        new com.sp.entity.ik.components.debug_renderers.LegDebugRenderer<E, C>()
                .renderDebug(this, animatable, poseStack, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
    }

    public double getMaxArmFormTargetDistance(PathAwareEntity entity) {
        if (stillStandCounter >= settings.get(0).standStillCounter() && IKMovementUtil.hasMovedOverLastTick(entity)) {
            stillStandCounter = 0;
        } else if (stillStandCounter < settings.get(0).standStillCounter()) {
            stillStandCounter++;
        }
        return (stillStandCounter == settings.get(0).standStillCounter()
                ? settings.get(0).maxStandingStillDistance() * 0.5
                : settings.get(0).maxDistance() * 0.4) * this.getScale();
    }
}

