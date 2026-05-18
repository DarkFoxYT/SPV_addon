package net.dark.spv_addon.init.grass;


import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.mixininterfaces.RenderIndirectExtension;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilRenderer;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.framebuffer.VeilFramebuffers;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.dark.spv_addon.init.config.SpvAddonConfig;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.gl.VertexBuffer.Usage;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL42C;
import org.lwjgl.opengl.GL43C;

import java.nio.ByteBuffer;

public class GrassRenderer {
    public static final VertexFormat POSITION_NORMAL;
    private static final Identifier shaderPath = new Identifier("spv_addon", "grass/grass");
    private static final Identifier windTexture = new Identifier("spv_addon", "textures/environment/puddle_noise.png");
    private static final Identifier computeShaderPath = new Identifier("spv_addon", "grass/compute/positions");

    static {
        POSITION_NORMAL = new VertexFormat(ImmutableMap.of(
                "Position", VertexFormats.POSITION_ELEMENT,
                "Normal", VertexFormats.NORMAL_ELEMENT
        ));
    }

    private final int positionsVbo;
    private final int indirectVbo;
    VertexBuffer vertexBuffer;
    private int lastGrassCount;
    private int lastMeshResolution;
    private final ByteBuffer indirectCommand = BufferUtils.createByteBuffer(20);

    public GrassRenderer() {
        this.vertexBuffer = new VertexBuffer(Usage.STATIC);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(DrawMode.QUADS, POSITION_NORMAL);
        this.createGrassModel(bufferBuilder);
        BufferBuilder.BuiltBuffer builtBuffer = bufferBuilder.end();
        this.vertexBuffer.bind();
        this.vertexBuffer.upload(builtBuffer);
        VertexBuffer.unbind();
        this.positionsVbo = GL15C.glGenBuffers();
        this.indirectVbo = GL15C.glGenBuffers();
        this.updateBuffers(true);
    }

    private float getGrassHeight() {
        return 0.4f;
    }

    public void render() {
        if (getGrassCount() <= 0) {
            return;
        }

        AdvancedFbo fbo = VeilRenderSystem.renderer().getFramebufferManager().getFramebuffer(VeilFramebuffers.OPAQUE);
        if (fbo != null) {
            fbo.bind(false);
            if (getGrassCount() != this.lastGrassCount || ConfigStuff.grassQuality.getResolution() != this.lastMeshResolution) {
                if (this.vertexBuffer != null) {
                    this.vertexBuffer.close();
                }

                this.vertexBuffer = new VertexBuffer(Usage.STATIC);
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferBuilder = tessellator.getBuffer();
                bufferBuilder.begin(DrawMode.QUADS, POSITION_NORMAL);
                this.createGrassModel(bufferBuilder);
                this.vertexBuffer.bind();
                this.vertexBuffer.upload(bufferBuilder.end());
                VertexBuffer.unbind();
            }

            this.updateBuffers(false);
            this.computeGrassPositions();
            ShaderProgram shader = VeilRenderSystem.setShader(shaderPath);
            if (shader != null) {
                shader.setFloat("GameTime", RenderSystem.getShaderGameTime());
                shader.setInt("NumOfInstances", MathHelper.floor(MathHelper.sqrt((float) getGrassCount())));
                shader.setFloat("grassHeight", this.getGrassHeight());
                shader.setFloat("density", ConfigStuff.grassQuality.getDensity());
                RenderSystem.setShaderTexture(0, windTexture);
                shader.addSampler("WindNoise", RenderSystem.getShaderTexture(0));
                shader.applyShaderSamplers(0);
                this.vertexBuffer.bind();
                GL15C.glBindBuffer(36671, this.indirectVbo);
                GL42C.glBindBufferBase(37074, 0, this.positionsVbo);
                shader.bind();
                ((RenderIndirectExtension) this.vertexBuffer).spb_revamped_1_20_1$drawIndirect();
                ShaderProgram.unbind();
                shader.clearSamplers();
                GL42C.glBindBufferBase(37074, 0, 0);
                GL15C.glBindBuffer(36671, 0);
                VertexBuffer.unbind();
                AdvancedFbo.unbind();
            }
        }
    }

    private void updateBuffers(boolean init) {
        int currentGrassCount = getGrassCount();
        int currentMeshResolution = ConfigStuff.grassQuality.getResolution();
        boolean countChange = currentGrassCount != this.lastGrassCount;
        boolean resolutionChange = currentMeshResolution != this.lastMeshResolution;
        if (countChange || init) {
            GL15C.glBindBuffer(37074, this.positionsVbo);
            GL42C.glBufferData(37074, 4L * (long) currentGrassCount * 4L, 35048);
            GL15C.glBindBuffer(37074, 0);
        }

        GL15C.glBindBuffer(36671, this.indirectVbo);
        this.indirectCommand.clear();
        this.indirectCommand.putInt(VeilRenderSystem.getIndexCount(this.vertexBuffer));
        this.indirectCommand.putInt(0);
        this.indirectCommand.putInt(0);
        this.indirectCommand.putInt(0);
        this.indirectCommand.putInt(0);
        this.indirectCommand.flip();
        GL15C.glBufferData(36671, this.indirectCommand, 35048);
        GL15C.glBindBuffer(36671, 0);
        if (countChange) {
            this.lastGrassCount = currentGrassCount;
        }

        if (resolutionChange) {
            this.lastMeshResolution = currentMeshResolution;
        }

    }

    private void computeGrassPositions() {
        ShaderProgram shader = VeilRenderSystem.setShader(computeShaderPath);
        if (shader != null) {
            if (shader.isCompute()) {
                GL42C.glBindBufferBase(37074, 0, this.positionsVbo);
                GL42C.glBindBufferBase(37074, 1, this.indirectVbo);
                int numOfInst = MathHelper.floor(MathHelper.sqrt((float) getGrassCount()));
                if (numOfInst <= 0) {
                    return;
                }
                shader.setInt("NumOfInstances", numOfInst);
                shader.setFloat("density", ConfigStuff.grassQuality.getDensity());
                float maxDist = (float) numOfInst / (ConfigStuff.grassQuality.getDensity() * 1.85F);
                shader.setFloat("maxDist", maxDist);
                Vector4fc[] planes = VeilRenderer.getCullingFrustum().getPlanes();
                float[] values = new float[4 * planes.length];

                for (int i = 0; i < planes.length; ++i) {
                    Vector4fc plane = planes[i];
                    values[i * 4] = plane.x();
                    values[i * 4 + 1] = plane.y();
                    values[i * 4 + 2] = plane.z();
                    values[i * 4 + 3] = plane.w();
                }

                shader.setFloats("FrustumPlanes", values);
                shader.bind();
                int grass = MathHelper.floor(MathHelper.sqrt((float) ConfigStuff.grassQuality.getCount()) / 8.0F);
                int x = Math.max(1, Math.min(grass, VeilRenderSystem.maxComputeWorkGroupCountX()));
                int y = Math.max(1, Math.min(grass, VeilRenderSystem.maxComputeWorkGroupCountY()));
                GL43C.glDispatchCompute(x, y, 1);
                GL42C.glMemoryBarrier(GL43C.GL_SHADER_STORAGE_BARRIER_BIT | GL43C.GL_COMMAND_BARRIER_BIT);
                ShaderProgram.unbind();
                GL42C.glBindBufferBase(37074, 0, 0);
                GL42C.glBindBufferBase(37074, 1, 0);
            }

            ShaderProgram.unbind();
        }
    }

    private void createGrassModel(BufferBuilder bufferBuilder) {
        int segments = ConfigStuff.grassQuality.getResolution();
        float xStep = 0.1F / (float) segments;

        for (int i = 0; i < segments; ++i) {
            bufferBuilder.vertex(0.6 - (double) (xStep * (float) (i + 1)), this.getGrassHeight() / (float) segments * (float) (i + 1), 0.0F).normal(0.0F, 0.0F, 1.0F).next();
            bufferBuilder.vertex(0.4 + (double) (xStep * (float) (i + 1)), this.getGrassHeight() / (float) segments * (float) (i + 1), 0.0F).normal(0.0F, 0.0F, 1.0F).next();
            bufferBuilder.vertex(0.4 + (double) (xStep * (float) i), this.getGrassHeight() / (float) segments * (float) i, 0.0F).normal(0.0F, 0.0F, 1.0F).next();
            bufferBuilder.vertex(0.6 - (double) (xStep * (float) i), this.getGrassHeight() / (float) segments * (float) i, 0.0F).normal(0.0F, 0.0F, 1.0F).next();
            bufferBuilder.vertex(0.6 - (double) (xStep * (float) i), this.getGrassHeight() / (float) segments * (float) i, 0.0F).normal(0.0F, 0.0F, -1.0F).next();
            bufferBuilder.vertex(0.4 + (double) (xStep * (float) i), this.getGrassHeight() / (float) segments * (float) i, 0.0F).normal(0.0F, 0.0F, -1.0F).next();
            bufferBuilder.vertex(0.4 + (double) (xStep * (float) (i + 1)), this.getGrassHeight() / (float) segments * (float) (i + 1), 0.0F).normal(0.0F, 0.0F, -1.0F).next();
            bufferBuilder.vertex(0.6 - (double) (xStep * (float) (i + 1)), this.getGrassHeight() / (float) segments * (float) (i + 1), 0.0F).normal(0.0F, 0.0F, -1.0F).next();
        }

    }

    public void close() {
        if (this.vertexBuffer != null) {
            this.vertexBuffer.close();
            this.vertexBuffer = null;
        }
        GL42C.glDeleteBuffers(this.positionsVbo);
        GL42C.glDeleteBuffers(this.indirectVbo);
        this.indirectCommand.clear();
    }

    private static int getGrassCount() {
        int configuredCount = Math.max(0, ConfigStuff.grassQuality.getCount());
        int cap = Math.max(0, SpvAddonConfig.maxGrassInstances);
        return Math.min(configuredCount, cap);
    }
}
