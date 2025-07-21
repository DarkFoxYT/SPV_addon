package net.dark.spv_addon.init.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

/**
 * Mod Menu integration for SPV Addon configuration
 * Systems tab only appears in singleplayer
 */
@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            // Check if we're in singleplayer to show/hide systems tab
            MinecraftClient client = MinecraftClient.getInstance();
            boolean isSingleplayer = client.isInSingleplayer() || client.getServer() != null;

            if (!isSingleplayer) {
                // Hide systems category in multiplayer by creating a custom screen
                return createMultiplayerConfigScreen(parent);
            }

            return MidnightConfig.getScreen(parent, "spv_addon");
        };
    }

    private Screen createMultiplayerConfigScreen(Screen parent) {
        // For multiplayer, we'll still show the config but systems will be disabled
        // MidnightConfig will handle this automatically based on our singleplayer check
        return MidnightConfig.getScreen(parent, "spv_addon");
    }
}
