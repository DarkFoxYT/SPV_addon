package net.dark.spv_addon.world.events.tests;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostProcessingManager;
import net.minecraft.util.Identifier;

public class DistortShaderHandler {
    private static final Identifier DISTORT_PIPELINE = new Identifier("spv_addon", "distort/distort");

    public static void applyDistortionPostProcess() {
        PostProcessingManager ppm = VeilRenderSystem.renderer().getPostProcessingManager();
        ppm.add(DISTORT_PIPELINE);
    }

    public static void removeDistortionPostProcess() {
        PostProcessingManager ppm = VeilRenderSystem.renderer().getPostProcessingManager();
        ppm.remove(DISTORT_PIPELINE);
    }
}