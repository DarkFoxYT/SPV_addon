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

    @SafeVarargs
    public IKLegCompDark(List<LegSetting> settings, List<ServerLimb> endpoints, C... limbs) {
        super(settings, endpoints, limbs);
        this.endPoints = endpoints;
        this.bases = new ArrayList<>();
        this.settings = settings;
        Arrays.stream(limbs).forEach(limb -> this.bases.add(Vec3d.ZERO));
    }

    @SafeVarargs
    public IKLegCompDark(LegSetting singleSetting, List<ServerLimb> endpoints, C... limbs) {
        this(Arrays.asList(singleSetting), endpoints, limbs);
    }

    private static boolean hasMovedOverLastTick(PathAwareEntity entity) {
        Vec3d vel = entity.getVelocity();
        float yawDelta = Math.abs(entity.getHeadYaw() - entity.prevHeadYaw);
        return vel.x != 0 || vel.z != 0 || yawDelta >= 0.01F;
    }

    public static BlockHitResult rayCastToGround(Vec3d pos, Entity entity, RaycastContext.FluidHandling fluid) {
        World world = entity.getWorld();

        // 1) Ray‑cast downwards
        Vec3d startDown = pos.add(0, 3, 0);
        Vec3d endDown   = pos.add(0, -10, 0);
        BlockHitResult hit = world.raycast(
                new RaycastContext(startDown, endDown, RaycastContext.ShapeType.COLLIDER, fluid, entity));

        // 2) If that block is fire, ray‑cast upwards instead
        if (world.getBlockState(hit.getBlockPos()).isOf(Blocks.FIRE)
                || world.getBlockState(hit.getBlockPos()).isOf(Blocks.SOUL_FIRE)) {
            Vec3d startUp = pos.add(0, -3, 0);
            Vec3d endUp   = pos.add(0, 10, 0);
            return world.raycast(
                    new RaycastContext(startUp, endUp, RaycastContext.ShapeType.COLLIDER, fluid, entity));
        }

        // 3) Otherwise return the original ground hit
        return hit;
    }


    @Override
    public void tickClient(E animatable, ModelAccessor model) {
        Entity entity = (Entity) animatable;
        for (int i = 0; i < this.limbs.size(); i++) {
            var optBone = model.getBone("base_leg" + (i + 1));
            if (optBone.isEmpty()) return;
            Vec3d basePos = this.bases.get(i);
            C limbChain = this.setLimb(i, basePos, entity);

            for (int k = 0; k < limbChain.getJoints().size() - 1; k++) {
                Vec3d start = limbChain.getJoints().get(k);
                Vec3d end = limbChain.getJoints().get(k + 1);
                var segBone = model.getBone("seg" + (k + 1) + "_leg" + (i + 1));
                if (segBone.isEmpty()) return;
                BoneAccessor segAcc = segBone.get();
                segAcc.moveTo(start, end, entity);

                if (limbChain instanceof EntityLegWithFoot footChain) {
                    var footBone = model.getBone("foot_leg" + (i + 1));
                    if (footBone.isEmpty()) return;
                    BoneAccessor footAcc = footBone.get();
                    Vec3d shortened = end.add(end.subtract(limbChain.endJoint)
                            .normalize()
                            .multiply(limbChain.getLast().length * 0.8));
                    double yOffset = shortened.subtract(limbChain.endJoint).y;
                    footAcc.moveTo(
                            PrAnCommonClass.shouldRenderDebugLegs ? shortened.subtract(0,200,0) : shortened,
                            footChain.getFootPosition().add(0, yOffset, 0),
                            entity
                    );
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
