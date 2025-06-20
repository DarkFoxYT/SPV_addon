package net.dark.spv_addon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.dark.spv_addon.world.events.tests.DistortShaderHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class TestCommand {
    private static int distortionTicks = 0;

    public static void clientTick() {
        if (distortionTicks > 0) {
            distortionTicks--;
            if (distortionTicks == 0) {
                DistortShaderHandler.removeDistortionPostProcess();
            }
        }
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("spvtest")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player != null && player.getWorld().isClient()) {
                        DistortShaderHandler.applyDistortionPostProcess();
                        distortionTicks = 20 * 20; // 20 secondes à 20 ticks/seconde
                        player.sendMessage(Text.literal("Distorsion activée pour 20 secondes !"), false);
                    }
                    return Command.SINGLE_SUCCESS;
                })
        );
    }
}