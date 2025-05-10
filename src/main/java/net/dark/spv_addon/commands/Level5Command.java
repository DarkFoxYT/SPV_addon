package net.dark.spv_addon.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Level5Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        dispatcher.register(CommandManager.literal("level5")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    ServerWorld level5 = player.getServer().getWorld(BackroomsLevels.LEVEL5_WORLD_KEY);
                    BlockPos spawn = BlockPos.ofFloored(BackroomsLevels.getCurrentLevelsOrigin(BackroomsLevels.LEVEL5_WORLD_KEY));

                    if (level5 != null && spawn != null) {
                        player.teleport(level5, spawn.getX(), spawn.getY(), spawn.getZ(), 0, 0);
                        player.sendMessage(Text.of("§eTeleported to Level 5 lobby."), false);
                        return 1;
                    } else {
                        player.sendMessage(Text.of("§cLevel 5 dimension or spawn point missing."), false);
                        return 0;
                    }
                }));

        dispatcher.register(CommandManager.literal("RUN")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    ServerWorld run = player.getServer().getWorld(BackroomsLevels.LEVELRUN_WORLD_KEY);
                    BlockPos spawn = BlockPos.ofFloored(BackroomsLevels.getCurrentLevelsOrigin(BackroomsLevels.LEVELRUN_WORLD_KEY));

                    if (run != null && spawn != null) {
                        player.teleport(run, spawn.getX(), spawn.getY(), spawn.getZ(), 0, 0);
                        player.sendMessage(Text.of("§eTeleported to Level RUN! "), false);
                        return 1;
                    } else {
                        player.sendMessage(Text.of("§cLevel RUN! dimension or spawn point missing."), false);
                        return 0;
                    }
                }));
    }
}
