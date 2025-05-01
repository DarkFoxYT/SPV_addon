package net.dark.spv_addon.init;

import net.dark.spv_addon.items.custom.CanteenItem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ModKeybinds {
    public static KeyBinding DRAIN_CANTEEN;

    public static void registerKeybinds() {
        DRAIN_CANTEEN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spv_addon.drain_canteen",
                GLFW.GLFW_KEY_R,
                "key.categories.gameplay"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (DRAIN_CANTEEN.wasPressed()) {
                if (client.player != null && client.player.getMainHandStack().getItem() instanceof CanteenItem) {
                    client.player.getMainHandStack().getOrCreateNbt().putString("State", "Empty");
                    client.player.sendMessage(Text.literal("Canteen emptied."), true);
                }
            }
        });
    }
}
