package net.dark.spv_addon.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class AddonLevelsTPCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("spvl")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("ikea")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                            ServerWorld level = player.getServer().getWorld(BackroomsLevels.LEVEL_IKEA_WORLD_KEY);
                            BlockPos spawn = getSpawnSafe(level, BackroomsLevels.LEVEL_IKEA_WORLD_KEY);

                            if (level != null && spawn != null) {
                                player.moveToWorld(level);
                                player.teleport(level, spawn.getX(), spawn.getY(), spawn.getZ(), player.getYaw(), player.getPitch());
                        return 1;
                    } else {
                                player.sendMessage(Text.literal("§cMissing tp point."), false);
                        return 0;
                    }
                        }))
                .then(CommandManager.literal("level207")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                            ServerWorld level = player.getServer().getWorld(BackroomsLevels.LEVEL207_WORLD_KEY);
                            BlockPos spawn = getSpawnSafe(level, BackroomsLevels.LEVEL207_WORLD_KEY);

                            if (level != null && spawn != null) {
                                player.moveToWorld(level);
                                player.teleport(level, spawn.getX(), spawn.getY(), spawn.getZ(), player.getYaw(), player.getPitch());
                        return 1;
                    } else {
                                player.sendMessage(Text.literal("§cMissing tp point."), false);
                        return 0;
                    }
                        }))
                .then(CommandManager.literal("run")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                            ServerWorld level = player.getServer().getWorld(BackroomsLevels.LEVELRUN_WORLD_KEY);
                            BlockPos spawn = getSpawnSafe(level, BackroomsLevels.LEVELRUN_WORLD_KEY);

                            if (level != null && spawn != null) {
                                player.moveToWorld(level);
                                player.teleport(level, spawn.getX(), spawn.getY(), spawn.getZ(), player.getYaw(), player.getPitch());
                        return 1;
                    } else {
                                player.sendMessage(Text.literal("§cMissing tp point."), false);
                        return 0;
                    }
                        }))
                .then(CommandManager.literal("kitty")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                            ServerWorld level = player.getServer().getWorld(BackroomsLevels.LEVEL_KITTY_WORLD_KEY);
                            BlockPos spawn = getSpawnSafe(level, BackroomsLevels.LEVEL_KITTY_WORLD_KEY);

                            if (level != null && spawn != null) {
                                player.moveToWorld(level);
                                player.teleport(level, spawn.getX(), spawn.getY(), spawn.getZ(), player.getYaw(), player.getPitch());
                        return 1;
                    } else {
                                player.sendMessage(Text.literal("§cMissing tp point."), false);
                        return 0;
                    }
                        }))
        );
    }

    private static BlockPos getSpawnSafe(ServerWorld world, net.minecraft.registry.RegistryKey<net.minecraft.world.World> key) {
        if (world == null) return null;
        try {
            var levelObj = com.sp.init.BackroomsLevels.getLevel(world);
            if (levelObj != null && levelObj.getSpawnPos() != null) {
                var spawn = levelObj.getSpawnPos();
                return new BlockPos((int) spawn.x, (int) spawn.y, (int) spawn.z);
            }
        } catch (Exception ignored) {}
        return world.getSpawnPos();
    }
}
