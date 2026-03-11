package net.dark.spv_addon.init.voicechat;

import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Smoothed voice/noise tracking used by procedural AI and ambient systems.
 */
public final class VoiceActivityTracker {
    private static final ConcurrentHashMap<UUID, VoiceState> STATES = new ConcurrentHashMap<>();

    private static final float VOICE_WEIGHT = 1.0f;
    private static final float MOVEMENT_WEIGHT = 0.28f;
    private static final float DECAY = 0.94f;
    private static final float MIN_KEEP = 0.01f;

    private VoiceActivityTracker() {
    }

    public static void recordVoice(PlayerEntity player, float strength) {
        if (player == null || player.getWorld().isClient) {
            return;
        }
        record(player, clamp01(strength), VOICE_WEIGHT);
    }

    public static void recordMovementNoise(PlayerEntity player, float strength) {
        if (player == null || player.getWorld().isClient) {
            return;
        }
        record(player, clamp01(strength), MOVEMENT_WEIGHT);
    }

    private static void record(PlayerEntity player, float rawStrength, float weight) {
        UUID id = player.getUuid();
        VoiceState state = STATES.computeIfAbsent(id, ignored -> new VoiceState());
        state.activity = Math.min(1.0f, state.activity * 0.65f + rawStrength * weight);
        state.lastPos = player.getPos();
        state.lastWorld = player.getWorld().getRegistryKey().getValue().toString();
        state.lastUpdatedTick = player.getWorld().getTime();

        // Compatibility bridge for existing systems still using this legacy set.
        SpvAddonVoicechatPlugin.justMadeNoise.add(id);
    }

    public static float getActivity(UUID playerId) {
        VoiceState state = STATES.get(playerId);
        return state == null ? 0.0f : state.activity;
    }

    public static Optional<HeardNoise> findLoudestNoise(PathAwareEntity seeker, double radius, float minActivity) {
        if (seeker == null || seeker.getWorld().isClient) {
            return Optional.empty();
        }

        double radiusSq = radius * radius;
        String seekerWorld = seeker.getWorld().getRegistryKey().getValue().toString();
        return seeker.getWorld().getPlayers().stream()
                .filter(player -> !player.isSpectator() && player.isAlive())
                .map(player -> {
                    VoiceState state = STATES.get(player.getUuid());
                    Vec3d noisePos = state != null && seekerWorld.equals(state.lastWorld) ? state.lastPos : player.getPos();
                    int ageTicks = state == null ? Integer.MAX_VALUE : (int) Math.max(0L, seeker.getWorld().getTime() - state.lastUpdatedTick);
                    return new HeardNoise(player, noisePos, getActivity(player.getUuid()), ageTicks);
                })
                .filter(noise -> noise.pos().squaredDistanceTo(seeker.getPos()) <= radiusSq)
                .filter(noise -> noise.activity() >= minActivity)
                .max(Comparator.comparingDouble(noise ->
                        noise.activity() - distancePenalty(seeker, noise.pos(), radius) - stalenessPenalty(noise.ageTicks())));
    }

    private static double distancePenalty(PathAwareEntity seeker, Vec3d pos, double radius) {
        double normalizedDistance = Math.sqrt(seeker.squaredDistanceTo(pos)) / Math.max(1.0, radius);
        return normalizedDistance * 0.45;
    }

    private static double stalenessPenalty(int ageTicks) {
        if (ageTicks <= 0) {
            return 0.0;
        }
        return Math.min(0.35, ageTicks * 0.01);
    }

    public static void tick(MinecraftServer server) {
        if (server == null) {
            return;
        }
        STATES.entrySet().removeIf(entry -> {
            VoiceState state = entry.getValue();
            state.activity *= DECAY;
            return state.activity < MIN_KEEP;
        });
        SpvAddonVoicechatPlugin.justMadeNoise.clear();
    }

    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    public record HeardNoise(PlayerEntity source, Vec3d pos, float activity, int ageTicks) {
    }

    private static final class VoiceState {
        float activity;
        Vec3d lastPos = Vec3d.ZERO;
        String lastWorld = "";
        long lastUpdatedTick = 0L;
    }
}
