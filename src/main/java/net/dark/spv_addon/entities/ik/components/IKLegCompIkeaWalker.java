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
import java.util.List;

public class IKLegCompIkeaWalker<C extends IKChain, E extends IKAnimatable<E>>
        extends com.sp.entity.ik.components.IKLegComponent<C, E> {

    private final List<ServerLimb> endPoints;
    private final List<Vec3d> bases;
    private final List<LegSetting> settings;
    private final List<Double> stepProgress = new ArrayList<>();
    private int stillStandCounter = 0;

    @SafeVarargs
    public IKLegCompIkeaWalker(List<LegSetting> settings, List<ServerLimb> endpoints, C... limbs) {
        super(settings, endpoints, limbs);
        this.endPoints = endpoints;
        this.bases = new ArrayList<>();
        this.settings = settings;

        int limbCount = Math.max(limbs.length, endpoints.size());
        for (int i = 0; i < limbCount; i++) {
            this.bases.add(Vec3d.ZERO);
            this.stepProgress.add(0.0);
        }
    }

    @Override
    public void tickClient(E animatable, ModelAccessor model) {
        Entity entity = (Entity) animatable;
        for (int i = 0; i < this.limbs.size(); i++) {
            var optBone = model.getBone("base_leg" + (i + 1));
            if (optBone.isEmpty()) {
                return;
            }
            Vec3d basePos = this.bases.get(i);
            C limbChain = this.setLimb(i, basePos, entity);

            for (int k = 0; k < 2; k++) {
                Vec3d start = limbChain.getJoints().get(k);
                Vec3d end = limbChain.getJoints().get(k + 1);
                var segBone = model.getBone("seg" + (k + 1) + "_leg" + (i + 1));
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
        for (int i = 0; i < Math.min(this.limbs.size(), this.bases.size()); i++) {
            var optBone = model.getBone("base_leg" + (i + 1));
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
        boolean moved = IKMovementUtil.hasMovedOverLastTick(entity);

        for (int i = 0; i < Math.min(endPoints.size(), settings.size()); i++) {
            ServerLimb limb = endPoints.get(i);
            limb.tick(this, i);
            Vec3d offset = limb.baseOffset.multiply(this.getScale());
            if (moved) {
                offset = offset.add(0, 0, Math.min(settings.get(i).stepInFront() * this.getScale(), 0.18));
            }
            offset = offset.rotateY((float) Math.toRadians(-entity.getBodyYaw()));
            Vec3d worldBase = offset.add(pos);
            Vec3d target = IKMovementUtil.rayCastToGround(worldBase, entity, 2.5, 8.0, settings.get(i).fluid()).getPos();
            target = IKMovementUtil.adjustForWall(entity, worldBase, target, 0.15);

            double prog = stepProgress.get(i);
            Vec3d limbPos = limb.getPos();

            boolean canStep = true;
            for (int j = 0; j < Math.min(endPoints.size(), settings.size()); j++) {
                if (j == i) {
                    continue;
                }
                Vec3d otherFoot = endPoints.get(j).getPos();
                if (otherFoot.z < limbPos.z) {
                    canStep = false;
                    break;
                }
            }

            double maxIdleDistance = settings.get(i).maxStandingStillDistance() * this.getScale();
            double behindDistance = limbPos.z - worldBase.z;
            if (!moved && behindDistance < -maxIdleDistance) {
                prog = 0.0;
                limb.setTarget(worldBase);
                stepProgress.set(i, prog);
                continue;
            }

            if (limb.hasToBeSet && canStep) {
                prog = 0.0;
                limb.set(target);
                limb.hasToBeSet = false;
            } else if (!target.isInRange(limb.target, this.getMaxLegFormTargetDistance(entity)) && canStep) {
                prog = 0.0;
                limb.setTarget(target);
            } else {
                prog = Math.min(prog + 0.035, 1.0);
            }
            stepProgress.set(i, prog);
        }
    }

    public void renderDebug(MatrixStack stack, E animatable,
                            RenderLayer layer, VertexConsumerProvider pipes,
                            VertexConsumer vb, float pt, int light, int overlay) {
        new com.sp.entity.ik.components.debug_renderers.LegDebugRenderer<E, C>()
                .renderDebug(this, animatable, stack, layer, pipes, vb, pt, light, overlay);
    }

    public double getMaxLegFormTargetDistance(PathAwareEntity entity) {
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

