package net.dark.spv_addon.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class CustomSkyboxRenderer {
    private static final Identifier[] SKYBOX_TEXTURES = new Identifier[] {
            new Identifier("spv_addon:textures/environment/forest_skybox_right.png"),
            new Identifier("spv_addon:textures/environment/forest_skybox_left.png"),
            new Identifier("spv_addon:textures/environment/forest_skybox_top.png"),
            new Identifier("spv_addon:textures/environment/forest_skybox_bottom.png"),
            new Identifier("spv_addon:textures/environment/forest_skybox_front.png"),
            new Identifier("spv_addon:textures/environment/forest_skybox_back.png")
    };

    public static void render(MatrixStack matrices, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();

        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.disableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);

        matrices.push();
        // Centre la skybox sur la caméra
        matrices.peek().getPositionMatrix().rotateY((float) Math.toRadians(-(mc.player.getYaw(tickDelta))));
        matrices.peek().getPositionMatrix().rotateX((float) Math.toRadians(mc.player.getPitch(tickDelta)));

        Matrix4f mat = matrices.peek().getPositionMatrix();
        float size = 100F;

        for (int face = 0; face < 6; face++) {
            RenderSystem.setShaderTexture(0, SKYBOX_TEXTURES[face]);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

            switch (face) {
                case 0: // +X
                    buffer.vertex(mat,  size, -size, -size).texture(0, 1).next();
                    buffer.vertex(mat,  size, -size,  size).texture(1, 1).next();
                    buffer.vertex(mat,  size,  size,  size).texture(1, 0).next();
                    buffer.vertex(mat,  size,  size, -size).texture(0, 0).next();
                    break;
                case 1: // -X
                    buffer.vertex(mat, -size, -size,  size).texture(0, 1).next();
                    buffer.vertex(mat, -size, -size, -size).texture(1, 1).next();
                    buffer.vertex(mat, -size,  size, -size).texture(1, 0).next();
                    buffer.vertex(mat, -size,  size,  size).texture(0, 0).next();
                    break;
                case 2: // +Y
                    buffer.vertex(mat, -size,  size, -size).texture(0, 1).next();
                    buffer.vertex(mat,  size,  size, -size).texture(1, 1).next();
                    buffer.vertex(mat,  size,  size,  size).texture(1, 0).next();
                    buffer.vertex(mat, -size,  size,  size).texture(0, 0).next();
                    break;
                case 3: // -Y
                    buffer.vertex(mat, -size, -size,  size).texture(0, 1).next();
                    buffer.vertex(mat,  size, -size,  size).texture(1, 1).next();
                    buffer.vertex(mat,  size, -size, -size).texture(1, 0).next();
                    buffer.vertex(mat, -size, -size, -size).texture(0, 0).next();
                    break;
                case 4: // +Z
                    buffer.vertex(mat, -size, -size, size).texture(0, 1).next();
                    buffer.vertex(mat, -size,  size, size).texture(0, 0).next();
                    buffer.vertex(mat,  size,  size, size).texture(1, 0).next();
                    buffer.vertex(mat,  size, -size, size).texture(1, 1).next();
                    break;
                case 5: // -Z
                    buffer.vertex(mat,  size, -size, -size).texture(0, 1).next();
                    buffer.vertex(mat,  size,  size, -size).texture(0, 0).next();
                    buffer.vertex(mat, -size,  size, -size).texture(1, 0).next();
                    buffer.vertex(mat, -size, -size, -size).texture(1, 1).next();
                    break;
            }
            tessellator.draw();
        }
        matrices.pop();

        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableBlend();
    }
}
