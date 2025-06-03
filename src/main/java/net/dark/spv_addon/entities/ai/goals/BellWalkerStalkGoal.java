package net.dark.spv_addon.entities.ai.goals;

import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class BellWalkerStalkGoal extends Goal {
    private final BellWalkerEntity mob;
    private final double stalkSpeed;
    private final double wanderSpeed;

    public BellWalkerStalkGoal(BellWalkerEntity mob, double stalkSpeed, double wanderSpeed) {
        this.mob = mob;
        this.stalkSpeed = stalkSpeed;
        this.wanderSpeed = wanderSpeed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return mob.hasStalkTarget() || mob.getNavigation().isIdle();
    }

    @Override
    public void tick() {
        PlayerEntity target = mob.getStalkTarget();
        if (target != null) {
            mob.getNavigation().startMovingTo(target, stalkSpeed); // Move sneakily
        } else {
            // Wander straight in the current direction (or implement random walk)
            if (mob.getNavigation().isIdle() || mob.age % 60 == 0) {
                double angle = mob.getWorld().random.nextDouble() * 2 * Math.PI;
                double dx = Math.cos(angle) * 6;
                double dz = Math.sin(angle) * 6;
                BlockPos pos = mob.getBlockPos().add((int) dx, 0, (int) dz);
                mob.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), wanderSpeed);
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        return true;
    }
}
