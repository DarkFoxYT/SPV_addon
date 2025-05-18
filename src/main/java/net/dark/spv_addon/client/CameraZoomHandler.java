package net.dark.spv_addon.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class CameraZoomHandler {
    public static float currentZoom = 1.0f;     // 1 = normal, max = zoomed
    public static float targetZoom = 1.0f;
    private static final float ZOOM_SPEED = 0.09f;
    private static final float ZOOMED = 2.0f;    // ← Change la force du zoom ici
    private static final float NORMAL = 1.0f;

    public static KeyBinding zoomKey;

    public static void register() {
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.spv_addon.zoom",
                GLFW.GLFW_KEY_Z,
                "key.categories.misc"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (zoomKey.isPressed()) {
                targetZoom = ZOOMED;
            } else {
                targetZoom = NORMAL;
            }
            // Interpolation smooth
            currentZoom += (targetZoom - currentZoom) * ZOOM_SPEED;
        });
    }

    // À utiliser dans ton mixin ou renderer custom pour modifier FOV/caméra
    public static float getCameraZoom() {
        return currentZoom;
    }
}
