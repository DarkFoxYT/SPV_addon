package net.dark.spv_addon.entities.ai.goals;

import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.init.ModSounds;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.PositionSourceType;
import net.minecraft.world.event.listener.GameEventListener;

import java.util.EnumSet;
import java.util.Optional;

public class BellWalkerStalkGoal extends Goal implements GameEventListener {
    private final BellWalkerEntity mob;
    private final double stalkSpeed;
    private final double wanderSpeed;
    private int lostTargetTicks = 0;
    private BlockPos soundHeardPos = null;
    private int soundHeardTicks = 0;

    public BellWalkerStalkGoal(BellWalkerEntity mob, double stalkSpeed, double wanderSpeed) {
        this.mob = mob;
        this.stalkSpeed = stalkSpeed;
        this.wanderSpeed = wanderSpeed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return mob.hasStalkTarget() || mob.getNavigation().isIdle() || soundHeardPos != null;
    }

    @Override
    public void tick() {
        PlayerEntity target = mob.getStalkTarget();

        // Si un son a été entendu récemment, s'y rendre
        if (soundHeardPos != null) {
            if (mob.getNavigation().isIdle() || mob.age % 10 == 0) {
                mob.getNavigation().startMovingTo(soundHeardPos.getX(), soundHeardPos.getY(), soundHeardPos.getZ(), stalkSpeed);
            }
            soundHeardTicks++;
            if (soundHeardTicks > 40) {
                soundHeardPos = null;
                soundHeardTicks = 0;
            }
            mob.setNoGravity(false);
            mob.setAiDisabled(false);
            mob.setTarget(null);
            mob.setGlowing(false);
            return;
        }

        if (target != null) {
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

            if (mob.getNavigation().isIdle() || mob.age % 10 == 0) {
                mob.getNavigation().startMovingTo(target, stalkSpeed);
            }

            mob.setNoGravity(false);
            mob.setAiDisabled(false);
            mob.setTarget(null);

            if (mob.age % 20 == 0) {
                mob.playSound(ModSounds.BELLWALKER_BELL, 0.7f, 1.2f);
                mob.getWorld().addParticle(ParticleTypes.SMOKE,
                        mob.getX(), mob.getY() + 1.2, mob.getZ(),
                        0, 0.05, 0);
            }
        } else {
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
        mob.setGlowing(false);
    }

    @Override
    public boolean shouldContinue() {
        return true;
    }

    @Override
    public PositionSource getPositionSource() {
        return new PositionSource() {
            @Override
            public Optional<Vec3d> getPos(World world) {
                return Optional.empty();
            }

            @Override
            public PositionSourceType<?> getType() {
                return null;
            }

            public Vec3d getPos(ServerWorld world) {
                return mob.getPos();
            }
        };
    }

    @Override
    public int getRange() {
        return 64;
    }

    @Override
    public boolean listen(ServerWorld world, GameEvent event, GameEvent.Emitter emitter, Vec3d emitterPos) {
        if (event == GameEvent.STEP || event == GameEvent.ENTITY_INTERACT || event == GameEvent.ENTITY_DAMAGE) {
            if (mob.squaredDistanceTo(emitterPos) < 64 * 64) {
                soundHeardPos = new BlockPos((int) emitterPos.x, (int) emitterPos.y, (int) emitterPos.z);
                soundHeardTicks = 0;
            }
        }
        return true;
    }
}