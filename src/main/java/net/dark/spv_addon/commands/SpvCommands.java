package net.dark.spv_addon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.dark.spv_addon.battery.BatteryManager;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class SpvCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("spv")
                .then(CommandManager.literal("battery")
                        .then(CommandManager.literal("toggle")
                                .executes(ctx -> {
                                    boolean current = BatteryManager.isBatteryEnabled();
                                    BatteryManager.setBatteryEnabled(!current);
                                    ctx.getSource().sendFeedback(() ->
                                                    Text.literal("Battery drain is now " + (!current ? "enabled" : "disabled")),
                                            false
                                    );
                                    return 1;
                                }))
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> {
                                            int value = IntegerArgumentType.getInteger(ctx, "value");
                                            BatteryManager.setBattery(ctx.getSource().getPlayer().getUuid(), value);
                                            ctx.getSource().sendFeedback(() ->
                                                            Text.literal("Battery set to " + value),
                                                    false
                                            );
                                            return 1;
                                        }))))
                .then(CommandManager.literal("sanity")
                        .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 100))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    int value = IntegerArgumentType.getInteger(ctx, "value");
                                    SanityComponent sanity = InitializeComponents.SANITY.get(player);
                                    sanity.setSanityLevel(value);
                                    ctx.getSource().sendFeedback(() ->
                                            Text.literal("Sanity set to " + value), false);
                                    return 1;
                                })))
                .then(CommandManager.literal("thirst")
                        .then(CommandManager.literal("set")
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> {
                                            ServerCommandSource src = ctx.getSource();
                                            ServerPlayerEntity player = src.getPlayer();
                                            int amt = IntegerArgumentType.getInteger(ctx, "amount");
                                            ThirstManager.setThirst(player, amt);
                                            player.sendMessage(Text.of("§cthirst set to " + amt), false);
                                            return 1;
                                        })))
                        .then(CommandManager.literal("enabled")
                                .then(CommandManager.argument("value", BoolArgumentType.bool())
                                        .executes(ctx -> {
                                            boolean on = BoolArgumentType.getBool(ctx, "value");
                                            ThirstManager.enabled = on;
                                            ServerCommandSource src = ctx.getSource();
                                            ServerPlayerEntity player = src.getPlayer();
                                            player.sendMessage(Text.of("§aThirst system " + (on ? "enabled" : "disabled")), false);
                                            return 1;
                                        })))
                )
        );
    }
}