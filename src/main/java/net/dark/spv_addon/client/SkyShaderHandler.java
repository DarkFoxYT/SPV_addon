package net.dark.spv_addon.client;



import com.mojang.blaze3d.systems.RenderSystem;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.platform.VeilEventPlatform;
import net.dark.spv_addon.init.BackroomsLevels;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class SkyShaderHandler {
    public static final Identifier SKY_POST = new Identifier("spv_addon", "forest_sky");

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null) return;
            VeilRenderer renderer = VeilRenderSystem.renderer();
            if (renderer == null) return;
            PostProcessingManager ppm = renderer.getPostProcessingManager();
            boolean inLevel = client.world.getRegistryKey().equals(BackroomsLevels.LEVEL207_WORLD_KEY);
            if (inLevel) {
                if (!ppm.isActive(SKY_POST)) {
                    ppm.add(SKY_POST);
                }
            } else {
                if (ppm.isActive(SKY_POST)) {
                    ppm.remove(SKY_POST);
                }
            }
        });

        VeilEventPlatform.INSTANCE.preVeilPostProcessing((name, pipeline, context) -> {
            if (SKY_POST.equals(name)) {
                ShaderProgram shader = context.getShader(new Identifier("spv_addon:sky/sky"));
                if (shader != null) {
                    shader.setFloat("GameTime", RenderSystem.getShaderGameTime());
                    shader.setFloat("sunsetTimer", 0.5f);
                }
            }
        });
    }
}