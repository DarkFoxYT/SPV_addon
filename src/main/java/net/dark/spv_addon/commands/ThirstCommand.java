package net.dark.spv_addon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * Registers:
 *   /thirst set <0–100>
 *   /thirst enabled <true|false>
 */
public class ThirstCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
            dispatcher.register(literal("thirst")
                    .then(literal("set")
                            .then(argument("amount", IntegerArgumentType.integer(0, 100))
                                    .executes(ctx -> {
                                        ServerCommandSource src = ctx.getSource();
                                        ServerPlayerEntity player = src.getPlayer();
                                        int amt = IntegerArgumentType.getInteger(ctx, "amount");
                                        ThirstManager.setThirst(player, amt);
                                        player.sendMessage(Text.of("§cthirst set to " + amt), false);
                                        return 1;
                                    })
                            )
                    )
                    .then(literal("enabled")
                            .then(argument("value", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean on = BoolArgumentType.getBool(ctx, "value");
                                        ThirstManager.enabled = on;
                                        ServerCommandSource src = ctx.getSource();
                                        ServerPlayerEntity player = src.getPlayer();
                                                player.sendMessage(Text.of("§aThirst system " + (on ? "enabled" : "disabled")), false);
                                        return 1;
                                    }
                            )
                    )
            ));
        }
    }
