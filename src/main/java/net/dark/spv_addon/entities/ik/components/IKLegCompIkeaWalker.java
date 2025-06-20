// src/main/java/net/dark/spv_addon/entities/ik/components/IKLegCompIkeaWalker.java
package net.dark.spv_addon.entities.ik.components;

import com.sp.entity.ik.components.IKAnimatable;
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

public class IKLegCompIkeaWalker<C extends IKChain, E extends IKAnimatable<E>>
        extends com.sp.entity.ik.components.IKLegComponent<C, E> {

    private final List<ServerLimb> endPoints;
    private final List<Vec3d> bases;
    private final List<LegSetting> settings;
    private int stillStandCounter = 0;
    private final List<Double> stepProgress = new ArrayList<>();

    @SafeVarargs
    public IKLegCompIkeaWalker(List<LegSetting> settings, List<ServerLimb> endpoints, C... limbs) {
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

    // Empêche la jambe d'aller dans un mur (collision latérale)
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
        for (int i = 0; i < this.limbs.size(); i++) {
            var optBone = model.getBone("base_leg" + (i + 1));
            if (optBone.isEmpty()) return;
            Vec3d basePos = this.bases.get(i);
            C limbChain = this.setLimb(i, basePos, entity);

            // Mouvement de respiration léger
            double breathing = Math.sin((age + i * 12) * 0.035) * 0.05;

            // Progression du pas
            double progress = stepProgress.get(i);
            double footArc = 0.0;
            if (progress < 1.0) {
                // Courbe de marche humaine : lever le pied, avancer, reposer
                double t = 0.5 - 0.5 * Math.cos(Math.PI * progress);
                // Arc du pied : monte puis descend
                footArc = Math.sin(Math.PI * t) * 0.22 + breathing;
            } else {
                footArc = breathing;
            }

            // Appliquer l'arc au dernier segment (pied)
            for (int k = 0; k < limbChain.getJoints().size() - 1; k++) {
                Vec3d start = limbChain.getJoints().get(k);
                Vec3d end = limbChain.getJoints().get(k + 1);
                if (k == limbChain.getJoints().size() - 2) {
                    // Appliquer l'arc vertical et un léger recul pour garder le pied sous le bassin
                    end = end.add(0, footArc, -0.08 * (1.0 - progress));
                }
                var segBone = model.getBone("seg" + (k + 1) + "_leg" + (i + 1));
                if (segBone.isEmpty()) return;
                BoneAccessor segAcc = segBone.get();
                segAcc.moveTo(start, end, entity);
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

            // Limiter l'avancée du pied : garder le pied sous le bassin
            if (hasMovedOverLastTick(entity)) {
                offset = offset.add(0, 0, Math.min(settings.get(i).stepInFront() * this.getScale(), 0.18));
            }
            offset = offset.rotateY((float) Math.toRadians(-entity.getBodyYaw()));
            Vec3d worldBase = offset.add(pos);
            var hit = rayCastToGround(worldBase, entity, settings.get(i).fluid());
            Vec3d target = hit.getPos();

            // Collision murale
            target = adjustForWall(entity, worldBase, target);

            // Progression du pas plus douce et réaliste
            double prog = stepProgress.get(i);
            if (limb.hasToBeSet) {
                prog = 0.0;
                limb.set(target);
                limb.hasToBeSet = false;
            } else if (!target.isInRange(limb.target, this.getMaxLegFormTargetDistance(entity))) {
                prog = 0.0;
                limb.setTarget(target);
            } else {
                prog = Math.min(prog + 0.035, 1.0); // Plus lent, plus humain
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
        // Utiliser le même renderer debug
        new com.sp.entity.ik.components.debug_renderers.LegDebugRenderer<E, C>()
                .renderDebug(this, animatable, stack, layer, pipes, vb, pt, light, overlay);
    }

    public double getMaxLegFormTargetDistance(PathAwareEntity entity) {
        if (stillStandCounter >= settings.get(0).standStillCounter() && hasMovedOverLastTick(entity)) {
            stillStandCounter = 0;
        } else if (stillStandCounter < settings.get(0).standStillCounter()) {
            stillStandCounter++;
        }
        // Distance plus faible pour éviter que le pied parte trop loin
        return (stillStandCounter == settings.get(0).standStillCounter()
                ? settings.get(0).maxStandingStillDistance() * 0.7
                : settings.get(0).maxDistance() * 0.7)
                * this.getScale();
    }
}