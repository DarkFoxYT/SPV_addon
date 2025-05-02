package net.dark.spv_addon.entities.ik.components;

import com.sp.entity.ik.components.IKAnimatable;
import com.sp.entity.ik.model.BoneAccessor;
import com.sp.entity.ik.model.ModelAccessor;
import com.sp.entity.ik.parts.ik_chains.EntityLeg;
import com.sp.entity.ik.parts.ik_chains.IKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class IKLegCompSanity<C extends IKChain, E extends IKAnimatable<E>>
        extends com.sp.entity.ik.components.IKLegComponent<C, E> {

    private final List<Vec3d> bases = new ArrayList<>();

    public IKLegCompSanity(List<LegSetting> settings, List<ServerLimb> endpoints, C... limbs) {
        super(settings, endpoints, limbs);
        for (C ignored : limbs) bases.add(Vec3d.ZERO);
    }

    @Override
    public void tickClient(E animatable, ModelAccessor model) {
        Entity entity = (Entity) animatable;
        for (int i = 0; i < limbs.size(); i++) {
            var opt = model.getBone("base_leg" + (i + 1));
            if (opt.isEmpty()) continue;

            BoneAccessor baseAcc = opt.get();
            Vec3d base = baseAcc.getPosition();
            this.bases.set(i, base);

            C limb = this.setLimb(i, base, entity);

            for (int j = 0; j < limb.getJoints().size() - 1; j++) {
                Vec3d from = limb.getJoints().get(j);
                Vec3d to = limb.getJoints().get(j + 1);
                var segment = model.getBone("seg" + (j + 1) + "_leg" + (i + 1));
                if (segment.isPresent()) {
                    BoneAccessor seg = segment.get();
                    seg.moveTo(from, to, entity);
                }
            }
        }
    }

    @Override
    public void getModelPositions(E animatable, ModelAccessor model) {
        for (int i = 0; i < limbs.size(); i++) {
            var opt = model.getBone("base_leg" + (i + 1));
            if (opt.isPresent()) {
                BoneAccessor baseAcc = opt.get();
                this.bases.set(i, baseAcc.getPosition());
            }
        }
    }

    @Override
    public C setLimb(int index, Vec3d base, Entity entity) {
        C limb = super.setLimb(index, base, entity);
        if (limb instanceof EntityLeg leg) leg.entity = entity;
        return limb;
    }
}
