package net.dark.spv_addon.entities.ik.components;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public final class IKMovementUtil {
    private IKMovementUtil() {
    }

    public static boolean hasMovedOverLastTick(PathAwareEntity entity) {
        Vec3d vel = entity.getVelocity();
        float yawDelta = Math.abs(entity.getHeadYaw() - entity.prevHeadYaw);
        return vel.x != 0 || vel.z != 0 || yawDelta >= 0.01F;
    }

    public static BlockHitResult rayCastToGround(Vec3d limbBase, Entity entity, double up, double down, RaycastContext.FluidHandling fluid) {
        World world = entity.getWorld();
        return world.raycast(
                new RaycastContext(
                        limbBase.offset(Direction.UP, up),
                        limbBase.offset(Direction.DOWN, down),
                        RaycastContext.ShapeType.COLLIDER,
                        fluid,
                        entity
                )
        );
    }

    public static Vec3d adjustForWall(PathAwareEntity entity, Vec3d from, Vec3d to, double padding) {
        World world = entity.getWorld();
        Vec3d dir = to.subtract(from);
        double dist = dir.length();
        if (dist < 0.01) {
            return to;
        }

        dir = dir.normalize();
        Vec3d checkTo = from.add(dir.multiply(dist + padding));
        BlockHitResult hit = world.raycast(new RaycastContext(
                from,
                checkTo,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                entity
        ));
        if (hit.getType() == BlockHitResult.Type.BLOCK) {
            return hit.getPos().subtract(dir.multiply(padding * 0.5));
        }
        return to;
    }
}

