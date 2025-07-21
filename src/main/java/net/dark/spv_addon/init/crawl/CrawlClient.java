package net.dark.spv_addon.init.crawl;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Client-side crawling system
 */
public class CrawlClient implements ClientModInitializer {
    public static KeyBinding crawlKey;
    
    @Override
    public void onInitializeClient() {
        crawlKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.spv_addon.crawl",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_C, // Default to C key
            "category.spv_addon.movement"
        ));
    }
}
