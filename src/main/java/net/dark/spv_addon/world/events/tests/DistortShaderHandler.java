// src/main/java/net/dark/spv_addon/world/events/tests/DistortShaderHandler.java
package net.dark.spv_addon.world.events.tests;

import foundry.veil.api.client.render.VeilRenderSystem;
import net.minecraft.util.Identifier;

public class DistortShaderHandler {
    private static final String SHADER_ID = "spv_addon:distort/distort";

    public static void applyDistortionPostProcess() {
        if (VeilRenderSystem.renderer() != null) {
            VeilRenderSystem.setShader(Identifier.tryParse(SHADER_ID));
        }
    }
}