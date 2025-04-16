package net.dark.spv_addon.entities.ai.goals;

import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class AggroNearestPlayerGoal extends Goal {
    private final BellWalkerEntity mob;
    private final double maxRange;

    public AggroNearestPlayerGoal(BellWalkerEntity mob, double maxRange) {
        this.mob = mob;
        this.maxRange = maxRange;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    @Override
    public boolean canStart() {
        List<PlayerEntity> candidates = mob.getWorld().getPlayers().stream()
                .filter(p -> mob.getSoundPitch() >= 2)
                .filter(p -> p.squaredDistanceTo(mob) <= maxRange * maxRange)
                .collect(Collectors.toList());
        if (candidates.isEmpty()) return false;

        PlayerEntity nearest = candidates.stream()
                .min(Comparator.comparingDouble(p -> p.squaredDistanceTo(mob)))
                .get();
        mob.setTarget(nearest);
        return true;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = mob.getTarget();
        return target instanceof PlayerEntity
                && target.isAlive()
                && mob.getSoundPitch() >= 2;
    }
}
