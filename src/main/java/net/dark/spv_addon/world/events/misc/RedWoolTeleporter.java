package net.dark.spv_addon.world.events.misc;

import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedWoolTeleporter {

    public static final RegistryKey<World> TARGET_WORLD = com.sp.init.BackroomsLevels.POOLROOMS_WORLD_KEY;
    public static final RegistryKey<World> SOURCE_WORLD1 = BackroomsLevels.LEVELRUN_WORLD_KEY;

    public static void tickPlayer(ServerPlayerEntity player) {
        if (!player.getWorld().getRegistryKey().equals(SOURCE_WORLD1)) return;

        BlockPos playerPos = player.getBlockPos();

        BlockPos.Mutable scanPos = new BlockPos.Mutable();
        for (int x = -5; x <= 5; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -5; z <= 5; z++) {
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

        player.teleport(targetWorld, 15, 90, 15, player.getYaw(), -90);
    }
}
