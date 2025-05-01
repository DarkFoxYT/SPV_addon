package net.dark.spv_addon.entities.ik.components;

import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.components.IKLegComponent;
import com.sp.entity.ik.model.ModelAccessor;
import com.sp.entity.ik.parts.ik_chains.IKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class IKLegCompKitty<C extends IKChain, E extends PathAwareEntity & IKAnimatable<E>>
        extends IKLegComponent<C, E> {

    private final String prefix;

    public IKLegCompKitty(String prefix, List<LegSetting> settings, List<?> endpoints, C... limbs) {
        super(settings, (List<ServerLimb>) endpoints, limbs);
        this.prefix = prefix;
    }

    @Override
    public void tickClient(E animatable, ModelAccessor model) {
        Entity entity = animatable;

        for (int i = 0; i < limbs.size(); i++) {
            var baseBone = model.getBone(prefix + (i + 1));
            if (baseBone.isEmpty()) continue;

            var basePos = baseBone.get().getPosition();
            var chain = setLimb(i, basePos, entity);

            for (int j = 0; j < chain.getJoints().size() - 1; j++) {
                var segBone = model.getBone("seg" + (j + 1) + "_" + prefix + (i + 1));
                if (segBone.isEmpty()) continue;

                Vec3d start = chain.getJoints().get(j);
                Vec3d end = chain.getJoints().get(j + 1);
                segBone.get().moveTo(start, end, entity);
            }
        }
    }

    @Override
    public void getModelPositions(E animatable, ModelAccessor model) {
        for (int i = 0; i < limbs.size(); i++) {
            var bone = model.getBone(prefix + (i + 1));
            if (bone.isEmpty()) continue;

            setLimb(i, bone.get().getPosition(), animatable);
        }
    }

    @Override
    public void tickServer(E animatable) {
        super.tickServer(animatable); // let parent handle limb stepping logic
    }

    public void setArmTarget(double x, double y, double z) {
        Vec3d target = new Vec3d(x, y, z);
        for (C chain : limbs) {
            List<Vec3d> joints = chain.getJoints();
            if (!joints.isEmpty()) {
                joints.set(joints.size() - 1, target);
            }
        }
    }
}
