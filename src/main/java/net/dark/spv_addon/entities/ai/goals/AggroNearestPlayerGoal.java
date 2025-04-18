package net.dark.spv_addon.entities.ai.goals;

import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.voicechat.SpvAddonVoicechatPlugin;
import net.dark.spv_addon.init.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

/**
 * AggroNearestPlayerGoal: targets the nearest speaking player
 * and plays a stop sound whose frequency increases as it closes in.
 */
public class AggroNearestPlayerGoal extends Goal {
    private final BellWalkerEntity mob;
    private final double maxRange;
    private final double aggroSpeed;
    private double originalSpeed;
    private int soundCooldown;

    private static final int MIN_SOUND_DELAY = 5;   // fastest (close)
    private static final int MAX_SOUND_DELAY = 40;  // slowest (far)

    public AggroNearestPlayerGoal(BellWalkerEntity mob, double maxRange, double aggroSpeed) {
        this.mob = mob;
        this.maxRange = maxRange;
        this.aggroSpeed = aggroSpeed;
        this.setControls(EnumSet.of(Control.TARGET));
    }

    public AggroNearestPlayerGoal(BellWalkerEntity mob, double maxRange) {
        this(mob, maxRange, 0.6);
    }

    @Override
    public boolean canStart() {
        List<UUID> candidates = SpvAddonVoicechatPlugin.justSpoke.stream()
                .filter(uuid -> {
                    PlayerEntity player = mob.getWorld().getPlayerByUuid(uuid);
                    return player != null && player.squaredDistanceTo(mob) <= maxRange * maxRange;
                })
                .toList();
        if (candidates.isEmpty()) return false;

        UUID nearestUuid = candidates.stream()
                .min(Comparator.comparingDouble(uuid -> {
                    PlayerEntity p = mob.getWorld().getPlayerByUuid(uuid);
                    return p.squaredDistanceTo(mob);
                }))
                .get();

        mob.setTarget(mob.getWorld().getPlayerByUuid(nearestUuid));
        return true;
    }

    @Override
    public void start() {
        // boost movement speed
        originalSpeed = mob.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .setBaseValue(aggroSpeed);
        // init sound cooldown based on current distance
        double distSq = mob.getTarget().squaredDistanceTo(mob);
        soundCooldown = calculateSoundDelay(distSq);
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target instanceof PlayerEntity) {
            if (--soundCooldown <= 0) {
                mob.playSound(ModSounds.BELLWALKER_BELL, 1.0f, 1.0f);
                soundCooldown = calculateSoundDelay(mob.getTarget().squaredDistanceTo(mob));
            }
        }
    }

    private int calculateSoundDelay(double squaredDistance) {
        double distance = Math.sqrt(squaredDistance);
        double ratio = Math.min(distance / maxRange, 1.0);
        return (int)(MIN_SOUND_DELAY + (MAX_SOUND_DELAY - MIN_SOUND_DELAY) * ratio);
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity target = mob.getTarget();
        return target instanceof PlayerEntity && target.isAlive();
    }

    @Override
    public void stop() {
        // restore original speed
        mob.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)
                .setBaseValue(originalSpeed);
        mob.setTarget(null);
    }
}
