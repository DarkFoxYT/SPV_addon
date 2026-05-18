package net.dark.spv_addon.client.effects;

import com.mojang.blaze3d.systems.RenderSystem;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.gl.VertexBuffer.Usage;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public final class StaticifyController {
    private static final Identifier TARGET_LEVEL_ID = BackroomsLevels.LEVELRUN_WORLD_KEY.getValue();
    private static final Identifier SHADER_PATH = new Identifier("spv_addon", "static/static");
    private static VertexBuffer screenVB;
    private static VertexFormat screenFmt = VertexFormats.POSITION;
    private StaticifyController() {}

    public static void init() {
        VeilEventPlatform.INSTANCE.onVeilRenderTypeStageRender((stage, levelRenderer, bufferSource, poseStack, projectionMatrix, renderTick, partialTicks, camera, frustum) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.world == null) return;
            if (stage != VeilRenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) return;
            boolean inTarget = mc.world.getRegistryKey().getValue().equals(TARGET_LEVEL_ID);
            if (!inTarget) return;

            if (screenVB == null) {
                screenVB = new VertexBuffer(Usage.STATIC);
                Tessellator t = Tessellator.getInstance();
                BufferBuilder b = t.getBuffer();
                b.begin(net.minecraft.client.render.VertexFormat.DrawMode.TRIANGLES, screenFmt);
                b.vertex(-1.0, -1.0, 0.0).next();
                b.vertex( 3.0, -1.0, 0.0).next();
                b.vertex(-1.0,  3.0, 0.0).next();
                screenVB.bind();
                screenVB.upload(b.end());
                VertexBuffer.unbind();
            }

            ShaderProgram shader = VeilRenderSystem.setShader(SHADER_PATH);
            if (shader == null) return;

            float time = RenderSystem.getShaderGameTime();
            int w = mc.getWindow().getFramebufferWidth();
            int h = mc.getWindow().getFramebufferHeight();

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);

            shader.setFloat("uEnable", 1.0f);
            shader.setFloat("uTime", time);
            shader.setFloats("uScreenSize", new float[]{(float)w, (float)h});
            shader.setFloat("uStrength", 0.28f);
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
}
