package net.dark.spv_addon.entities.ai.procedural;

import net.dark.spv_addon.init.voicechat.VoiceActivityTracker;
import net.dark.spv_addon.util.BackroomsLevelContext;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.Optional;

/**
 * Shared procedural predator brain for entities that stalk and chase players.
 * It keeps perception refreshes throttled while building a richer awareness model.
 */
public class PredatorBrain {
    private static final int SENSE_INTERVAL_TICKS = 4;
    private static final int DECISION_INTERVAL_TICKS = 2;

    private final PathAwareEntity mob;
    private final PredatorBrainConfig config;

    private PredatorBrainState state = PredatorBrainState.PATROL;
    private PlayerEntity currentTarget;
    private Vec3d investigatePos;
    private Vec3d searchAnchor;
    private Vec3d lastSeenPos;
    private int searchTicks;
    private int repathCooldown;
    private int patrolIdleTicks;
    private int sensoryCooldown;
    private int decisionCooldown;
    private int lostSightTicks;
    private int searchSweepIndex;
    private double awareness;

    public PredatorBrain(PathAwareEntity mob, PredatorBrainConfig config) {
        this.mob = mob;
        this.config = config;
    }

    public PredatorBrainState getState() {
        return state;
    }

    public Optional<PlayerEntity> getCurrentTarget() {
        return Optional.ofNullable(currentTarget);
    }

    public void tick() {
        if (mob.getWorld().isClient || !mob.isAlive()) {
            return;
        }

        if (repathCooldown > 0) {
            repathCooldown--;
        }
        if (searchTicks > 0) {
            searchTicks--;
        }
        if (sensoryCooldown > 0) {
            sensoryCooldown--;
        }
        if (decisionCooldown > 0) {
            decisionCooldown--;
        }

        if (currentTarget != null && (!currentTarget.isAlive() || currentTarget.isSpectator())) {
            clearTarget();
        }

        if (sensoryCooldown <= 0) {
            sensoryCooldown = SENSE_INTERVAL_TICKS;
            refreshPerception();
        } else if (currentTarget != null) {
            updateVisualMemory(currentTarget);
        }

        if (decisionCooldown <= 0) {
            decisionCooldown = DECISION_INTERVAL_TICKS;
            decideState();
        }

        executeState();
        awareness = Math.max(0.0, awareness - 0.0035);
    }

    private void refreshPerception() {
        scanForTarget().ifPresentOrElse(snapshot -> {
            currentTarget = snapshot.player();
            awareness = Math.max(awareness, snapshot.score());
            updateVisualMemory(snapshot.player());
        }, () -> {
            if (currentTarget != null && lostSightTicks > config.searchDurationTicks()) {
                clearTarget();
            }
        });

        refreshHeardNoise();
    }

    private Optional<TargetSnapshot> scanForTarget() {
        double rangeSq = config.senseRange() * config.senseRange();
        return mob.getWorld().getPlayers().stream()
                .filter(player -> player.isAlive() && !player.isSpectator())
                .filter(player -> player.squaredDistanceTo(mob) <= rangeSq)
                .map(player -> new TargetSnapshot(player, targetScore(player)))
                .filter(snapshot -> snapshot.score() > 0.18)
                .max(Comparator.comparingDouble(TargetSnapshot::score));
    }

    private double targetScore(PlayerEntity player) {
        double distNorm = 1.0 - Math.min(1.0, mob.distanceTo(player) / config.senseRange());
        double visionBonus = mob.canSee(player) ? 0.48 : 0.0;
        double voiceBonus = VoiceActivityTracker.getActivity(player.getUuid()) * 0.95;
        double movementBonus = player.getVelocity().horizontalLengthSquared() > 0.02 ? 0.20 : 0.0;
        double sprintBonus = player.isSprinting() ? 0.16 : 0.0;
        double darknessBonus = BackroomsLevelContext.darknessScore(player, player.getBlockPos()) * 0.18;
        double contextBias = BackroomsLevelContext.aggressionMultiplier(mob.getWorld()) * 0.12;
        return distNorm + visionBonus + voiceBonus + movementBonus + sprintBonus + darknessBonus + contextBias;
    }

    private void updateVisualMemory(PlayerEntity player) {
        if (mob.canSee(player)) {
            lastSeenPos = player.getPos();
            searchAnchor = lastSeenPos;
            searchTicks = config.searchDurationTicks();
            lostSightTicks = 0;
            awareness = Math.min(1.25, awareness + 0.06 * BackroomsLevelContext.aggressionMultiplier(mob.getWorld()));
        } else {
            lostSightTicks++;
        }
    }

    private void refreshHeardNoise() {
        Optional<VoiceActivityTracker.HeardNoise> heard = VoiceActivityTracker.findLoudestNoise(
                mob,
                config.senseRange(),
                config.minimumHearingActivity()
        );
        heard.ifPresent(noise -> {
            investigatePos = noise.pos();
            searchAnchor = noise.pos();
            searchTicks = Math.max(searchTicks, config.searchDurationTicks() / 2);
            awareness = Math.max(awareness, 0.22 + noise.activity() * 0.42);
        });
    }

    private void decideState() {
        boolean hasTarget = currentTarget != null && currentTarget.isAlive();
        boolean seesTarget = hasTarget && mob.canSee(currentTarget);

        if (hasTarget) {
            double aggression = BackroomsLevelContext.aggressionMultiplier(mob.getWorld());
            double chaseThreshold = 0.56 / aggression;
            double stalkThreshold = 0.28 - BackroomsLevelContext.stalkingBias(mob.getWorld());

            if ((seesTarget && awareness >= chaseThreshold) || mob.distanceTo(currentTarget) < 4.0f) {
                state = PredatorBrainState.CHASE;
                return;
            }

            if (seesTarget || awareness >= stalkThreshold || VoiceActivityTracker.getActivity(currentTarget.getUuid()) > config.minimumHearingActivity()) {
                state = PredatorBrainState.STALK;
                return;
            }

            if (searchAnchor != null && searchTicks > 0) {
                state = PredatorBrainState.SEARCH;
                return;
            }
        }

        if (investigatePos != null) {
            state = PredatorBrainState.INVESTIGATE;
            return;
        }

        state = PredatorBrainState.PATROL;
    }

    private void executeState() {
        switch (state) {
            case CHASE -> runChase();
            case STALK -> runStalk();
            case INVESTIGATE -> runInvestigate();
            case SEARCH -> runSearch();
            case PATROL -> runPatrol();
        }
    }

    private void runChase() {
        if (currentTarget == null) {
            state = PredatorBrainState.PATROL;
            return;
        }

        if (!mob.canSee(currentTarget) && lostSightTicks > config.searchDurationTicks() / 2) {
            awareness = Math.max(0.18, awareness - 0.12);
            state = PredatorBrainState.SEARCH;
            mob.setTarget(null);
            return;
        }

        mob.setTarget(currentTarget);
        if (repathCooldown <= 0 || mob.getNavigation().isIdle()) {
            double chaseSpeed = config.chaseSpeed() * BackroomsLevelContext.aggressionMultiplier(mob.getWorld());
            mob.getNavigation().startMovingTo(currentTarget, chaseSpeed);
            repathCooldown = config.repathIntervalTicks();
        }
    }

    private void runStalk() {
        if (currentTarget == null) {
            state = PredatorBrainState.PATROL;
            return;
        }

        mob.setTarget(mob.canSee(currentTarget) && mob.distanceTo(currentTarget) < 6.0f ? currentTarget : null);
        Vec3d anchor = mob.canSee(currentTarget) ? currentTarget.getPos() : firstNonNull(lastSeenPos, investigatePos, searchAnchor);
        if (anchor == null) {
            state = PredatorBrainState.PATROL;
            return;
        }

        if (repathCooldown <= 0 || mob.getNavigation().isIdle()) {
            Vec3d destination = chooseStalkOffset(anchor);
            mob.getNavigation().startMovingTo(destination.x, destination.y, destination.z, config.investigateSpeed());
            repathCooldown = config.repathIntervalTicks() + 2;
        }

        if (mob.canSee(currentTarget)) {
            awareness = Math.min(1.25, awareness + 0.02);
        } else if (lostSightTicks > 18 && searchAnchor != null) {
            state = PredatorBrainState.SEARCH;
        }
    }

    private void runInvestigate() {
        mob.setTarget(null);
        if (investigatePos == null) {
            state = PredatorBrainState.PATROL;
            return;
        }
        if (repathCooldown <= 0 || mob.getNavigation().isIdle()) {
            mob.getNavigation().startMovingTo(investigatePos.x, investigatePos.y, investigatePos.z, config.investigateSpeed());
            repathCooldown = config.repathIntervalTicks();
        }
        if (mob.squaredDistanceTo(investigatePos) < 4.0) {
            searchAnchor = investigatePos;
            investigatePos = null;
            searchTicks = Math.max(searchTicks, config.searchDurationTicks() / 2);
            state = PredatorBrainState.SEARCH;
        }
    }

    private void runSearch() {
        mob.setTarget(null);
        if (searchAnchor == null || searchTicks <= 0) {
            searchAnchor = null;
            investigatePos = null;
            state = PredatorBrainState.PATROL;
            return;
        }

        if (repathCooldown <= 0 || mob.getNavigation().isIdle()) {
            BlockPos around = sweepingSearchPos(searchAnchor);
            mob.getNavigation().startMovingTo(around.getX(), around.getY(), around.getZ(), config.investigateSpeed());
            repathCooldown = config.repathIntervalTicks() + 6;
        }

        if (lostSightTicks > 12) {
            awareness = Math.max(0.0, awareness - 0.015);
        }
    }

    private void runPatrol() {
        mob.setTarget(null);
        if (!mob.getNavigation().isIdle()) {
            patrolIdleTicks = 0;
            return;
        }

        patrolIdleTicks++;
        if (patrolIdleTicks < 20) {
            return;
        }
        patrolIdleTicks = 0;

        BlockPos destination = choosePatrolDestination();
        mob.getNavigation().startMovingTo(destination.getX(), destination.getY(), destination.getZ(), config.patrolSpeed());
    }

    private Vec3d chooseStalkOffset(Vec3d anchor) {
        Vec3d away = mob.getPos().subtract(anchor);
        if (away.lengthSquared() < 0.001) {
            away = new Vec3d(1.0, 0.0, 0.0);
        }
        Vec3d lateral = new Vec3d(-away.z, 0.0, away.x).normalize();
        Vec3d backwards = away.normalize().multiply(4.0 + mob.getRandom().nextDouble() * 4.0);
        Vec3d sidestep = lateral.multiply((mob.getRandom().nextBoolean() ? 1 : -1) * (2.0 + mob.getRandom().nextDouble() * 2.5));
        return anchor.add(backwards).add(sidestep);
    }

    private BlockPos choosePatrolDestination() {
        BlockPos best = mob.getBlockPos();
        double bestScore = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < 4; i++) {
            BlockPos candidate = mob.getBlockPos().add(
                    mob.getRandom().nextBetween(-config.patrolRadius(), config.patrolRadius()),
                    0,
                    mob.getRandom().nextBetween(-config.patrolRadius(), config.patrolRadius())
            );
            double distanceScore = Math.sqrt(candidate.getSquaredDistance(mob.getBlockPos())) * 0.04;
            double darknessScore = BackroomsLevelContext.darknessScore(mob, candidate) * 0.35;
            double score = distanceScore + darknessScore + mob.getRandom().nextDouble() * 0.12;
            if (score > bestScore) {
                best = candidate;
                bestScore = score;
            }
        }

        return best;
    }

    private BlockPos sweepingSearchPos(Vec3d anchor) {
        int radius = config.patrolRadius() + BackroomsLevelContext.searchRadiusBonus(mob.getWorld());
        double angle = (searchSweepIndex++ % 8) * (Math.PI / 4.0) + mob.getRandom().nextDouble() * 0.25;
        int x = MathHelper.floor(anchor.x + Math.cos(angle) * radius);
        int z = MathHelper.floor(anchor.z + Math.sin(angle) * radius);
        return new BlockPos(x, MathHelper.floor(anchor.y), z);
    }

    private void clearTarget() {
        currentTarget = null;
        lastSeenPos = null;
        lostSightTicks = 0;
    }

    @SafeVarargs
    private static <T> T firstNonNull(T... values) {
        for (T value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private record TargetSnapshot(PlayerEntity player, double score) {
    }
}
