package net.dark.spv_addon.world.transitions;

import com.sp.SPBRevamped;
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

        if (tick == profile.effectStartTick()) {
            teleport.playerComponent().setShouldNoClip(true);
            teleport.playerComponent().setShouldDoStatic(profile.enableStatic());
            teleport.playerComponent().sync();
        }

        if (tick == profile.blackoutTick() && teleport.playerComponent().player instanceof ServerPlayerEntity serverPlayer) {
            SPBRevamped.sendBlackScreenPacket(serverPlayer, profile.blackoutDurationTicks(), true, profile.enableStatic());
        }

        if (tick == 1) {
            cleanup(teleport.playerComponent());
        }
    }

    private static void cleanup(PlayerComponent playerComponent) {
        playerComponent.setShouldNoClip(false);
        playerComponent.setShouldDoStatic(false);
        playerComponent.sync();
    }

    public record TransitionProfile(
            int durationTicks,
            int effectStartTick,
            int blackoutTick,
            int blackoutDurationTicks,
            boolean enableStatic
    ) {
        public static TransitionProfile cinematicDefault() {
            return new TransitionProfile(110, 20, 14, 20, true);
        }

        public static TransitionProfile unstableGlitch() {
            return new TransitionProfile(130, 36, 30, 30, true);
        }
    }
}

