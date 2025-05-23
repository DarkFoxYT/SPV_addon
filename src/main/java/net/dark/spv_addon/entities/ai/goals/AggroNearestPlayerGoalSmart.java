package net.dark.spv_addon.entities.ai.goals;

import net.dark.spv_addon.entities.custom.IkeaWalkerEntity;
import net.dark.spv_addon.init.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class AggroNearestPlayerGoalSmart extends Goal {
    private final IkeaWalkerEntity mob;
    private final double maxRange;
    private final double aggroSpeed;
    private double originalSpeed;
    private int soundCooldown;

    private static final int MIN_SOUND_DELAY = 5;   // fastest (close)
    private static final int MAX_SOUND_DELAY = 40;  // slowest (far)

    public AggroNearestPlayerGoalSmart(IkeaWalkerEntity mob, double maxRange, double aggroSpeed, int i) {
        this.mob = mob;
        this.maxRange = maxRange;
        this.aggroSpeed = aggroSpeed;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    public AggroNearestPlayerGoalSmart(IkeaWalkerEntity mob, double maxRange) {
        this(mob, maxRange, 0.6, 60);
    }

    private boolean isNightOrAggro() {
        World world = mob.getWorld();
        // Allow aggro at night OR if already angry from being hit
        return world.isNight() || mob.getAttacker() != null;
    }

    @Override
    public boolean canStart() {
        // Only start at night or if already attacked
        if (!isNightOrAggro())
            return false;

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
    public void start() {
        originalSpeed = mob.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .setBaseValue(aggroSpeed);

        double distSq = mob.getTarget().squaredDistanceTo(mob);
        soundCooldown = calculateSoundDelay(distSq);
    }

    @Override
    public void tick() {
    }

    private int calculateSoundDelay(double squaredDistance) {
        double distance = Math.sqrt(squaredDistance);
        double ratio = Math.min(distance / maxRange, 1.0);
        return (int)(MIN_SOUND_DELAY + (MAX_SOUND_DELAY - MIN_SOUND_DELAY) * ratio);
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = mob.getTarget();
        // Don't continue if it's day and not angry (retaliation only)
        if (!isNightOrAggro()) return false;
        return target instanceof PlayerEntity
                && target.isAlive()
                && mob.getSoundPitch() >= 2;
    }
}
