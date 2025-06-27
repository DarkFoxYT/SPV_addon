package net.dark.spv_addon.entities.ik.parts.ik_chains;


import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.parts.ik_chains.AngleConstraintIKChain;
import com.sp.entity.ik.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class EntityArm extends AngleConstraintIKChain {
    public Entity entity;

    public EntityArm(double... lengths) {
        super(lengths);
    }

    public EntityArm(Segment... segments) {
        super(segments);
    }

    @Override
    public Vec3d getDownNormalOnLegPlane() {
        return getDownNormalOnArmPlane();
    }

    public Vec3d getReferencePoint() {
        Vec3d referencePoint = MathUtil.getFlatRotationVector(this.entity.getBodyYaw());
        return this.getFirst().getPosition().add(referencePoint.multiply(100.0));
    }

    public Vec3d getStretchingPos(Vec3d target, Vec3d base) {
        return base.add(MathUtil.getFlatRotationVector(this.entity).multiply(this.getMaxLength() * 2.0));
    }

    public Vec3d getDownNormalOnArmPlane() {
        Vec3d baseRotated = this.getFirst().getPosition().rotateY(-this.entity.getBodyYaw());
        Vec3d targetRotated = this.endJoint.rotateY(-this.entity.getBodyYaw());
        Vec3d flatRotatedBase = new Vec3d(baseRotated.x, baseRotated.y, 0.0);
        Vec3d flatRotatedTarget = new Vec3d(targetRotated.x, targetRotated.y, 0.0);
        Vec3d flatBase = flatRotatedBase.rotateY(this.entity.getBodyYaw());
        Vec3d flatTarget = flatRotatedTarget.rotateY(this.entity.getBodyYaw());
        return flatTarget.subtract(flatBase).normalize();
    }

    public Vec3d getConstrainedPosForRootSegment() {
        return this.getConstrainedPosForRootSegment(this.getDownNormalOnArmPlane());
    }
}