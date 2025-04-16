package net.dark.spv_addon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.dark.spv_addon.battery.BatteryManager;
import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class FlashlightBatteryCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("flashbattery")
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
                                }))));

    }
}
