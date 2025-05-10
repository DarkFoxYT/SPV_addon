// File: net.dark.spv_addon.commands.SanityCommand.java

package net.dark.spv_addon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class SanityCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("setsanity")
                .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 100))
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                            int value = IntegerArgumentType.getInteger(ctx, "value");
                            SanityComponent sanity = InitializeComponents.SANITY.get(player);
                            sanity.setSanityLevel(value);
                            ctx.getSource().sendFeedback(() ->
                                    Text.literal("Sanity set to " + value), false);
                            return 1;
                        })));
    }
}
