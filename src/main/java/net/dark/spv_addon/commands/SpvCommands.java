package net.dark.spv_addon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.dark.spv_addon.Additions.battery.BatteryManager;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Simplified SPV commands - only allows setting values
 * Systems are controlled via gamerules for servers
 */
public class SpvCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("spv")
                .then(CommandManager.literal("battery")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 100))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    int value = IntegerArgumentType.getInteger(ctx, "value");
                                    BatteryManager.setBattery(player.getUuid(), value);
                                    ctx.getSource().sendFeedback(() ->
                                            Text.literal("Battery set to " + value + "%"), false);
                                    return 1;
                                })))
                .then(CommandManager.literal("sanity")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 100))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    int value = IntegerArgumentType.getInteger(ctx, "value");
                                    SanityComponent sanity = InitializeComponents.SANITY.get(player);
                                    sanity.setSanityLevel(value);
                                    ctx.getSource().sendFeedback(() ->
                                            Text.literal("Sanity set to " + value + "%"), false);
                                    return 1;
                                })))
                .then(CommandManager.literal("thirst")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 100))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    int value = IntegerArgumentType.getInteger(ctx, "value");
                                    ThirstManager.setThirst(player, value);
                                    ctx.getSource().sendFeedback(() ->
                                            Text.literal("Thirst set to " + value + "%"), false);
                                    return 1;
                                })))
        );
    }
}