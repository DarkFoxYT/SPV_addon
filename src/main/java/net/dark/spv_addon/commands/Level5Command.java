package net.dark.spv_addon.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class Level5Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        dispatcher.register(CommandManager.literal("level5")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    ServerWorld level5 = player.getServer().getWorld(BackroomsLevels.LEVEL5_WORLD_KEY);
                    BlockPos spawn = getSpawnSafe(level5, BackroomsLevels.LEVEL5_WORLD_KEY);

                    if (level5 != null && spawn != null) {
                        player.moveToWorld(level5);
                        player.teleport(level5, spawn.getX(), spawn.getY(), spawn.getZ(), player.getYaw(), player.getPitch());
                        player.sendMessage(Text.literal("§eTeleported to Level 5 lobby."), false);
                        return 1;
                    } else {
                        player.sendMessage(Text.literal("§cLevel 5 dimension or spawn point missing."), false);
                        return 0;
                    }
                }));

        dispatcher.register(CommandManager.literal("levelikea")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    ServerWorld level5 = player.getServer().getWorld(BackroomsLevels.LEVEL_IKEA_WORLD_KEY);
                    BlockPos spawn = getSpawnSafe(level5, BackroomsLevels.LEVEL_IKEA_WORLD_KEY);

                    if (level5 != null && spawn != null) {
                        player.moveToWorld(level5);
                        player.teleport(level5, spawn.getX(), spawn.getY(), spawn.getZ(), player.getYaw(), player.getPitch());
                        player.sendMessage(Text.literal("§eTeleported to Level ikea."), false);
                        return 1;
                    } else {
                        player.sendMessage(Text.literal("§cLevel ikea dimension or spawn point missing."), false);
                        return 0;
                    }
                }));

        dispatcher.register(CommandManager.literal("run")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    ServerWorld run = player.getServer().getWorld(BackroomsLevels.LEVELRUN_WORLD_KEY);
                    BlockPos spawn = getSpawnSafe(run, BackroomsLevels.LEVELRUN_WORLD_KEY);

                    if (run != null && spawn != null) {
                        player.moveToWorld(run);
                        player.teleport(run, spawn.getX(), spawn.getY(), spawn.getZ(), player.getYaw(), player.getPitch());
                        player.sendMessage(Text.literal("§eTeleported to Level RUN!"), false);
                        return 1;
                    } else {
                        player.sendMessage(Text.literal("§cLevel RUN! dimension or spawn point missing."), false);
                        return 0;
                    }
                }));
    }

    private static BlockPos getSpawnSafe(ServerWorld world, net.minecraft.registry.RegistryKey<net.minecraft.world.World> key) {
        if (world == null) return null;
        // Essaie d'obtenir la vraie pos (support pour mod)
        try {
            var levelObj = com.sp.init.BackroomsLevels.getLevel(world);
            if (levelObj != null && levelObj.getSpawnPos() != null) {
                var spawn = levelObj.getSpawnPos();
                return new BlockPos((int) spawn.x, (int) spawn.y, (int) spawn.z);
            }
        } catch (Exception ignored) {}
        // Sinon fallback vanilla
        return world.getSpawnPos();
    }
}
