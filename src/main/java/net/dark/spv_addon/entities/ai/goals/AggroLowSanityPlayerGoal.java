package net.dark.spv_addon.entities.ai.goals;

import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.entities.custom.SanityStalkerEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

import java.util.EnumSet;
import java.util.List;

public class AggroLowSanityPlayerGoal extends Goal {
    private final SanityStalkerEntity mob;
    private final double range;

    public AggroLowSanityPlayerGoal(SanityStalkerEntity mob, double range) {
        this.mob = mob;
        this.range = range;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart() {
        List<PlayerEntity> players = (List<PlayerEntity>) mob.getWorld().getPlayers();
        for (PlayerEntity player : players) {
            if (mob.squaredDistanceTo(player) <= range * range
                    && InitializeComponents.SANITY.get(player).getSanity() < 10) {
                mob.setTarget(player);
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        // already handled in canStart
    }

    @Override
    public boolean shouldContinue() {
        return mob.getTarget() != null && mob.getTarget().isAlive();
    }
}
