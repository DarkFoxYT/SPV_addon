package net.dark.spv_addon.init.grass;

import com.mojang.blaze3d.systems.RenderSystem;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.VeilFramebuffers;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL30C;

public class GodrayRenderer {
    private static final Identifier SHADER_ID = new Identifier("spv_addon", "godray");
    private VertexBuffer fullscreenQuad;

    public GodrayRenderer() {
        createFullscreenQuad();
    }

    private void createFullscreenQuad() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(DrawMode.QUADS, VertexFormats.POSITION);
        buffer.vertex(-1.0, -1.0, 0.0).next();
        buffer.vertex(1.0, -1.0, 0.0).next();
        buffer.vertex(1.0, 1.0, 0.0).next();
        buffer.vertex(-1.0, 1.0, 0.0).next();
        fullscreenQuad = new VertexBuffer(VertexBuffer.Usage.STATIC);
        fullscreenQuad.bind();
        fullscreenQuad.upload(buffer.end());
        VertexBuffer.unbind();
    }

    public void render() {
        VeilEventPlatform.INSTANCE.onVeilRenderTypeStageRender((stage, levelRenderer, bufferSource, poseStack, projectionMatrix, renderTick, partialTicks, camera, frustum) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null) return;

            if (client.world.getRegistryKey() == BackroomsLevels.LEVEL188_WORLD_KEY && stage == VeilRenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
                AdvancedFbo fbo = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(VeilFramebuffers.OPAQUE);
                if (fbo == null) return;

                ShaderProgram shader = VeilRenderSystem.setShader(SHADER_ID);
                if (shader != null) {
                    int sceneTex = fbo.getColorAttachment(0).getFormat();
                    GL30C.glBindTexture(GL30C.GL_TEXTURE_2D, sceneTex);
                    shader.addSampler("u_Scene", sceneTex);

                    shader.setVector("ScreenSize", (float) client.getWindow().getScaledWidth(), (float) client.getWindow().getScaledHeight());
                    shader.setVector("LightPos", 0.5f, 0.4f);
                    shader.setFloat("Exposure", 0.35f);
                    shader.setFloat("Decay", 0.93f);
                    shader.setFloat("Density", 0.97f);
                    shader.setFloat("Weight", 0.4f);

                    shader.setup(); // Important

                    fullscreenQuad.bind();
                    fullscreenQuad.draw();
                    VertexBuffer.unbind();
                }
            }
        });
    }


    public void close() {
        if (fullscreenQuad != null) {
            fullscreenQuad.close();
        }
    }
}
