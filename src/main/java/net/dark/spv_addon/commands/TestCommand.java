package net.dark.spv_addon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.dark.spv_addon.init.cutscene.WindowCutsceneManager;
import net.dark.spv_addon.world.events.tests.DistortShaderHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class TestCommand {


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("spvtest")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player != null && player.getWorld().isClient()) {
                        player.sendMessage(Text.literal("test active !"), false);
                    }
                    return Command.SINGLE_SUCCESS;
                })
        );
    }
}