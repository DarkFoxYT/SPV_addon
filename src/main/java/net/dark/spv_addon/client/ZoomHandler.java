// ZoomHandler.java
package net.dark.spv_addon.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ZoomHandler {
    private static float targetFov = 70.0f; // FOV vanilla
    private static float currentFov = 70.0f;
    private static boolean zooming = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Vérifie si la touche Z est pressée (peux remplacer par KeyBinding si besoin)
            zooming = org.lwjgl.glfw.GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_J) == org.lwjgl.glfw.GLFW.GLFW_PRESS;

            targetFov = zooming ? 30.0f : 70.0f;
            // Interpolation pour le zoom smooth
            currentFov += (targetFov - currentFov) * 0.2f;
            client.options.getFov().setValue((int) currentFov);
        });
    }
}
