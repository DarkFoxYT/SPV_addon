package net.dark.spv_addon.client.effects;

import com.mojang.blaze3d.systems.RenderSystem;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.init.config.SpvAddonConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.gl.VertexBuffer.Usage;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

/**
 * Veil-based full-screen corruption pass for the Glitched level.
 */
public final class GlitchedLevelRenderController {
    private static final Identifier SHADER_PATH = new Identifier("spv_addon", "glitched/glitched");
    private static VertexBuffer screenVB;
    private static final VertexFormat SCREEN_FMT = VertexFormats.POSITION;

    private GlitchedLevelRenderController() {
    }

    public static void init() {
        VeilEventPlatform.INSTANCE.onVeilRenderTypeStageRender((stage, levelRenderer, bufferSource, poseStack, projectionMatrix, renderTick, partialTicks, camera, frustum) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null) {
                return;
            }
            if (!client.world.getRegistryKey().equals(BackroomsLevels.GLITCHED_WORLD_KEY)) {
                return;
            }
            if (stage != VeilRenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
                return;
            }
            if (!SpvAddonConfig.isGlitchedEffectEnabled()) {
                return;
            }

            ensureFullscreenBuffer();

            ShaderProgram shader = VeilRenderSystem.setShader(SHADER_PATH);
            if (shader == null) {
                return;
            }

            float time = RenderSystem.getShaderGameTime();
            int width = client.getWindow().getFramebufferWidth();
            int height = client.getWindow().getFramebufferHeight();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            shader.setFloat("uTime", time);
            shader.setFloat("uChaos", SpvAddonConfig.getGlitchChaos());
            shader.setFloat("uDistortionStrength", SpvAddonConfig.getGlitchDistortion());
            shader.setFloat("uChromaStrength", SpvAddonConfig.getGlitchChroma());
            shader.setFloat("uFlashStrength", SpvAddonConfig.getGlitchFlashStrength());
            shader.setFloat("uNoiseScale", SpvAddonConfig.getGlitchNoiseScale());
            shader.setFloats("uScreenSize", new float[]{(float) width, (float) height});
            shader.bind();

            screenVB.bind();
            screenVB.draw();
            VertexBuffer.unbind();

            ShaderProgram.unbind();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
        });
    }

    private static void ensureFullscreenBuffer() {
        if (screenVB != null) {
            return;
        }
        screenVB = new VertexBuffer(Usage.STATIC);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.TRIANGLES, SCREEN_FMT);
        buffer.vertex(-1.0, -1.0, 0.0).next();
        buffer.vertex(3.0, -1.0, 0.0).next();
        buffer.vertex(-1.0, 3.0, 0.0).next();
        screenVB.bind();
        screenVB.upload(buffer.end());
        VertexBuffer.unbind();
    }
}
