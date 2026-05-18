package net.dark.spv_addon.world.transitions;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.world.levels.BackroomsLevel;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Shared transition builder for cinematic SPB transitions.
 */
public final class SpbTransitionDirector {
    private SpbTransitionDirector() {
    }

    public static BackroomsLevel.LevelTransition createTransition(
            PlayerComponent playerComponent,
            Vec3d destination,
            BackroomsLevel from,
            BackroomsLevel to,
            TransitionProfile profile
    ) {
        return new BackroomsLevel.LevelTransition(
                profile.durationTicks(),
                (teleport, tick) -> handleTick(teleport, tick, profile),
                new BackroomsLevel.CrossDimensionTeleport(playerComponent, destination, from, to),
                (teleport, tick) -> cleanup(teleport.playerComponent())
        );
    }

    private static void handleTick(BackroomsLevel.CrossDimensionTeleport teleport, int tick, TransitionProfile profile) {
        World world = teleport.playerComponent().player.getWorld();
        if (world.isClient()) {
            return;
        }
        if (!(teleport.playerComponent().player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        if (tick == profile.lightsOutTick()) {
            SPBRevamped.sendLevelTransitionLightsOutPacket(serverPlayer, profile.lightsOutDurationTicks());
        }

        if (tick == profile.staticStartTick()) {
            teleport.playerComponent().setShouldDoStatic(profile.enableStatic());
            teleport.playerComponent().sync();
        }

        if (tick == profile.noClipStartTick()) {
            teleport.playerComponent().setShouldNoClip(true);
            teleport.playerComponent().sync();
        }

        if (tick == profile.shakeTick() && profile.shakeIntensity() > 0.0D) {
            SPBRevamped.sendCameraShakePacket(serverPlayer, profile.shakeIntensity(), profile.shakeRoughness());
        }

        if (tick == profile.blackoutTick()) {
            SPBRevamped.sendBlackScreenPacket(serverPlayer, profile.blackoutDurationTicks(), true, profile.enableStatic());
        }

        if (tick == 1) {
            cleanup(teleport.playerComponent());
        }
    }

    private static void cleanup(PlayerComponent playerComponent) {
        playerComponent.setShouldNoClip(false);
        playerComponent.setShouldDoStatic(false);
        playerComponent.setShouldRender(true);
        playerComponent.setTeleporting(false);
        playerComponent.sync();
    }

    public static int beginDirectTransition(ServerPlayerEntity player, TransitionProfile profile) {
        PlayerComponent playerComponent = InitializeComponents.PLAYER.get(player);

        if (profile.lightsOutDurationTicks() > 0) {
            SPBRevamped.sendLevelTransitionLightsOutPacket(player, profile.lightsOutDurationTicks());
        }
        if (profile.shakeIntensity() > 0.0D) {
            SPBRevamped.sendCameraShakePacket(player, profile.shakeIntensity(), profile.shakeRoughness());
        }
        if (profile.blackoutDurationTicks() > 0) {
            SPBRevamped.sendBlackScreenPacket(player, profile.blackoutDurationTicks(), true, profile.enableStatic());
        }

        playerComponent.setShouldNoClip(true);
        playerComponent.setShouldDoStatic(profile.enableStatic());
        playerComponent.setShouldRender(true);
        playerComponent.sync();
        return profile.directTeleportDelayTicks();
    }

    public static void completeDirectTransition(ServerPlayerEntity player) {
        cleanup(InitializeComponents.PLAYER.get(player));
    }

    public record TransitionProfile(
            int durationTicks,
            int lightsOutTick,
            int lightsOutDurationTicks,
            int staticStartTick,
            int noClipStartTick,
            int blackoutTick,
            int blackoutDurationTicks,
            int directTeleportDelayTicks,
            int shakeTick,
            double shakeIntensity,
            double shakeRoughness,
            boolean enableStatic
    ) {
        public static TransitionProfile cinematicDefault() {
            return new TransitionProfile(120, 58, 82, 42, 24, 26, 42, 28, 52, 0.18D, 0.72D, true);
        }

        public static TransitionProfile unstableGlitch() {
            return new TransitionProfile(145, 86, 105, 74, 34, 32, 54, 34, 82, 0.34D, 0.92D, true);
        }

        public static TransitionProfile quickCut() {
            return new TransitionProfile(70, 42, 54, 34, 20, 18, 30, 20, 40, 0.12D, 0.55D, true);
        }

        public static TransitionProfile runEscape() {
            return new TransitionProfile(95, 64, 72, 56, 36, 34, 56, 40, 62, 0.28D, 0.85D, true);
        }
    }
}
