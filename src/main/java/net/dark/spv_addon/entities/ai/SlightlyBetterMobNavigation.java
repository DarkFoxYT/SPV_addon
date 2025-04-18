package net.dark.spv_addon.entities.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class SlightlyBetterMobNavigation extends MobNavigation {
    public SlightlyBetterMobNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getTarget();
        if (target != null && canSee(this.entity, target)) {
            this.entity.getMoveControl().moveTo(
                    target.getX(),
                    this.adjustTargetY(target.getPos()),
                    target.getZ(),
                    this.speed
            );
            return;
        }
        super.tick();
    }

    private boolean canSee(Entity source, Entity target) {
        if (source.getWorld() != target.getWorld()) return false;
        Vec3d eyePos = new Vec3d(source.getX(), source.getY() + source.getEyeHeight(source.getPose()), source.getZ());
        Vec3d targetPos = new Vec3d(target.getX(), target.getEyeY(), target.getZ());
        HitResult hit = source.getWorld().raycast(new RaycastContext(
                eyePos, targetPos,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                source
        ));
        return hit.getType() == HitResult.Type.MISS;
    }
}
