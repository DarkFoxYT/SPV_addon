package net.dark.spv_addon.entities.ai.goals;

import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.voicechat.SpvAddonVoicechatPlugin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

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
        List<UUID> candidates = SpvAddonVoicechatPlugin.justSpoke.stream()
                .filter(p -> mob.getWorld().getPlayerByUuid(p).squaredDistanceTo(mob) <= maxRange * maxRange)
                .toList();
        if (candidates.isEmpty()) return false;

        UUID nearest = candidates.stream()
                .min(Comparator.comparingDouble(p -> mob.getWorld().getPlayerByUuid(p).squaredDistanceTo(mob)))
                .get();

        mob.setTarget(mob.getWorld().getPlayerByUuid(nearest));
        return true;
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = mob.getTarget();
        return target instanceof PlayerEntity
                && target.isAlive();
    }
}
