package net.dark.spv_addon.entities.ai.goals;

import net.dark.spv_addon.entities.custom.SanityStalkerEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Always levitate the entity upwards by applying
 * constant positive Y motion and disabling gravity.
 */
public class LevitationGoal extends Goal {
    private final SanityStalkerEntity mob;

    public LevitationGoal(SanityStalkerEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        // Always active
        return true;
    }

    @Override
    public void start() {
        mob.setNoGravity(true);
    }

    @Override
    public void tick() {
        // gently push up
        mob.setVelocity(mob.getVelocity().x, 0.15, mob.getVelocity().z);
    }

    @Override
    public void stop() {
        mob.setNoGravity(false);
    }
}
