// src/main/java/net/dark/spv_addon/entities/ai/goals/BellWalkerStalkGoal.java
package net.dark.spv_addon.entities.ai.goals;

import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.init.ModSounds;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class BellWalkerStalkGoal extends Goal {
    private final BellWalkerEntity mob;
    private final double stalkSpeed;
    private final double wanderSpeed;
    private int lostTargetTicks = 0;

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
            // Si la cible est trop loin ou morte, on la "perd"
            if (!target.isAlive() || target.distanceTo(mob) > 40) {
                lostTargetTicks++;
                if (lostTargetTicks > 40) {
                    mob.clearStalkTarget();
                    lostTargetTicks = 0;
                }
                return;
            } else {
                lostTargetTicks = 0;
            }

            // Navigation améliorée
            if (mob.getNavigation().isIdle() || mob.age % 10 == 0) {
                mob.getNavigation().startMovingTo(target, stalkSpeed);
            }

            mob.setNoGravity(false);
            mob.setAiDisabled(false);
            mob.setTarget(null); // Aveugle comme le Warden

            // Effet sonore et particules lors de la traque
            if (mob.age % 20 == 0) {
                mob.playSound(ModSounds.BELLWALKER_BELL, 0.7f, 1.2f);
                mob.getWorld().addParticle(ParticleTypes.SMOKE,
                        mob.getX(), mob.getY() + 1.2, mob.getZ(),
                        0, 0.05, 0);
            }
        } else {
            // Errance intelligente si pas de cible
            if (mob.getNavigation().isIdle() || mob.age % 60 == 0) {
                double angle = mob.getWorld().random.nextDouble() * 2 * Math.PI;
                double dx = Math.cos(angle) * 6;
                double dz = Math.sin(angle) * 6;
                BlockPos pos = mob.getBlockPos().add((int) dx, 0, (int) dz);
                mob.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), wanderSpeed);
            }
            mob.setNoGravity(false);
            mob.setAiDisabled(false);
            mob.setTarget(null);
        }
        mob.setGlowing(false); // Pour simuler la cécité
    }

    @Override
    public boolean shouldContinue() {
        return true;
    }
}