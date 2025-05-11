package net.dark.spv_addon.Additions.Sanity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class SanityLightDebugRenderer {
    private static final TagKey<Block> SANITY_LIGHT_TAG =
            TagKey.of(Registries.BLOCK.getKey(), new Identifier("spv_addon", "sanity_lights"));
    private static final int RADIUS = 10;

    public static void init() {
        WorldRenderEvents.AFTER_ENTITIES.register((context) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientWorld world = client.world;
            if (world == null || client.player == null) return;

            MatrixStack matrices = context.matrixStack();
            Camera camera = context.camera();
            Vec3d camPos = camera.getPos();

            BlockPos origin = client.player.getBlockPos();

            matrices.push();
            Matrix4f matrix = matrices.peek().getPositionMatrix();

            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);

            buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

            BlockPos.iterateOutwards(origin, 32, 3, 32).forEach(pos -> {
                if (world.getBlockState(pos).isIn(SANITY_LIGHT_TAG)) {
                    double dx = pos.getX() + 0.5 - camPos.x;
                    double dy = pos.getY() + 0.1 - camPos.y;
                    double dz = pos.getZ() + 0.5 - camPos.z;
                    drawCircleXZ(buffer, dx, dy, dz, RADIUS, 60);
                }
            });

            BufferRenderer.draw(buffer.end());
            matrices.pop();
        });
    }

    private static void drawCircleXZ(BufferBuilder buffer, double cx, double cy, double cz, float radius, int segments) {
        for (int i = 0; i <= segments; i++) {
            double angle = 2.0 * Math.PI * i / segments;
            double x = cx + radius * Math.cos(angle);
            double z = cz + radius * Math.sin(angle);
            buffer.vertex(x, cy, z).color(0f, 1f, 0f, 1f).next();
        }
    }
}
