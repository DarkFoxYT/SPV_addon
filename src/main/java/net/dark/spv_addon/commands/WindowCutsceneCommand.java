package net.dark.spv_addon.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.dark.spv_addon.init.cutscene.WindowCutsceneManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public class WindowCutsceneCommand {
    private static WindowCutsceneManager activeCutscene;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommandManager.literal("playcutscene")
                .executes(context -> {
                    if (activeCutscene != null) {
                        context.getSource().sendFeedback(Text.literal("Une cinématique est déjà en cours !"));
                        return 0;
                    }

                    activeCutscene = new WindowCutsceneManager();
                    activeCutscene.start();

                    return Command.SINGLE_SUCCESS;
                }));
    }

    public static void tick() {
        if (activeCutscene != null) {
            activeCutscene.tick();

            if (activeCutscene.isFinished()) {
                activeCutscene = null;
            }
        }
    }
}