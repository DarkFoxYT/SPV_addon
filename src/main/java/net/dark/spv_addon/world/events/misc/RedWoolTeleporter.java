package net.dark.spv_addon.world.events.misc;

import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.dark.spv_addon.util.ServerTickScheduler;
import net.dark.spv_addon.world.transitions.SpbTransitionDirector;

public class RedWoolTeleporter {

    public static final RegistryKey<World> TARGET_WORLD = com.sp.init.BackroomsLevels.POOLROOMS_WORLD_KEY;
    public static final RegistryKey<World> SOURCE_WORLD1 = BackroomsLevels.LEVELRUN_WORLD_KEY;
    private static final int CHECK_INTERVAL_TICKS = 6;
    private static final int HORIZONTAL_RADIUS = 3;
    private static final int VERTICAL_RADIUS = 2;

    public static void tickPlayer(ServerPlayerEntity player) {
        if (!player.getWorld().getRegistryKey().equals(SOURCE_WORLD1)) return;
        if (player.age % CHECK_INTERVAL_TICKS != 0) return;

        BlockPos playerPos = player.getBlockPos();

        if (player.getWorld().getBlockState(playerPos.down()).isOf(Blocks.RED_WOOL)) {
            teleportPlayer(player);
            return;
        }

        BlockPos.Mutable scanPos = new BlockPos.Mutable();
        for (int x = -HORIZONTAL_RADIUS; x <= HORIZONTAL_RADIUS; x++) {
            for (int y = -VERTICAL_RADIUS; y <= VERTICAL_RADIUS; y++) {
                for (int z = -HORIZONTAL_RADIUS; z <= HORIZONTAL_RADIUS; z++) {
                    scanPos.set(playerPos.getX() + x, playerPos.getY() + y, playerPos.getZ() + z);
                    if (player.getWorld().getBlockState(scanPos).isOf(Blocks.RED_WOOL)) {
                        teleportPlayer(player);
                        return;
                    }
                }
            }
        }
    }

    public static void teleportPlayer(ServerPlayerEntity player) {
        MinecraftServer server = player.getServer();
        if (server == null) return;

        ServerWorld targetWorld = server.getWorld(TARGET_WORLD);
        if (targetWorld == null) return;

        int teleportDelay = SpbTransitionDirector.beginDirectTransition(
                player,
                SpbTransitionDirector.TransitionProfile.quickCut()
        );
        ServerTickScheduler.schedule(teleportDelay, () -> {
            if (!player.isRemoved()) {
                player.teleport(targetWorld, 15, 90, 15, player.getYaw(), -90);
                SpbTransitionDirector.completeDirectTransition(player);
            }
        });
    }
}
