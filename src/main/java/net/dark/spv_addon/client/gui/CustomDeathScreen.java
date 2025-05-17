package net.dark.spv_addon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Found Footage DeathScreen:
 * - Death static with dynamic noise & flicker
 * - Enlarged centered text
 * - 10s timer, auto respawn
 * - Glitch at the end
 */
@Environment(EnvType.CLIENT)
public class CustomDeathScreen extends Screen {
    private static final Identifier BACKGROUND = new Identifier("spv_addon", "textures/gui/death_full.png");
    private static final Identifier STATIC_OVERLAY = new Identifier("spv_addon", "textures/gui/static_overlay.png");
    // Optional scanlines
    // private static final Identifier SCANLINES = new Identifier("spv_addon", "textures/gui/scanlines.png");

    private final String playerName;
    private int ticksElapsed = 0;
    private int glitchTicks = 0;
    private boolean isGlitching = false;

    // Static dynamic
    private NativeImage staticImage = null;
    private NativeImageBackedTexture staticTexture = null;
    private final Identifier dynamicStaticId = new Identifier("spv_addon", "dynamic_static");

    public CustomDeathScreen(String playerName) {
        super(Text.empty());
        this.playerName = playerName;
    }

    @Override
    public void tick() {
        if (!isGlitching) {
            ticksElapsed++;
            if (ticksElapsed >= 200) {
                startGlitch();
            }
        } else {
            glitchTicks++;
            if (glitchTicks >= 16) { // glitch lasts 16 ticks (~0.8s)
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.player != null) mc.player.requestRespawn();
                mc.setScreen(null);
                closeStaticImage();
            }
        }
    }

    public boolean isPauseScreen() {
        return false;
    }

    // Helper: Make dynamic static
    private void updateStaticTexture(int w, int h) {
        if (staticImage == null || staticImage.getWidth() != w || staticImage.getHeight() != h) {
            closeStaticImage();
            staticImage = new NativeImage(w, h, false);
            staticTexture = new NativeImageBackedTexture(staticImage);
        }
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                // Flicker: stronger on glitch
                int val = isGlitching ? (int)(Math.random() * 255) : (int)(Math.random() * 192 + 32);
                int color = 0xFF000000 | (val << 16) | (val << 8) | val;
                staticImage.setColor(x, y, color);
            }
        }
        staticTexture.upload();
        MinecraftClient.getInstance().getTextureManager().registerTexture(dynamicStaticId, staticTexture);
    }

    private void closeStaticImage() {
        if (staticImage != null) staticImage.close();
        staticImage = null;
        staticTexture = null;
    }

    // Glitch effect
    private void startGlitch() {
        isGlitching = true;
        glitchTicks = 0;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int w = mc.getWindow().getScaledWidth();
        int h = mc.getWindow().getScaledHeight();

        // 1. Draw background
        RenderSystem.enableBlend();
        ctx.drawTexture(BACKGROUND, 0, 0, 0, 0, w, h, w, h);
        RenderSystem.disableBlend();

        // 2. Text: bigger, centered, more spacing
        int fontHeight = mc.textRenderer.fontHeight + 8; // bigger
        int fontScale = 2; // double size

        String line1 = playerName + " was";
        String line2 = "Never Found";
        int color = 0xFFFFFF;

        ctx.getMatrices().push();
        ctx.getMatrices().translate(w / 2f, h / 2f - fontHeight, 0f);
        ctx.getMatrices().scale(fontScale, fontScale, 1f);
        int x1 = -mc.textRenderer.getWidth(line1) / 2;
        int y1 = 0;
        ctx.drawText(mc.textRenderer, Text.literal(line1), x1, y1, color, false);

        int x2 = -mc.textRenderer.getWidth(line2) / 2;
        int y2 = (fontHeight + 2) / fontScale; // spacing
        ctx.drawText(mc.textRenderer, Text.literal(line2), x2, y2, color, false);
        ctx.getMatrices().pop();

        // 3. Fade-in static overlay (200 ticks), then glitch
        float frac = Math.min(ticksElapsed / 200f, 1f);
        float flicker = (float)(Math.sin(ticksElapsed * 0.6 + Math.random()) * 0.15 + 0.85);
        float alpha = isGlitching
                ? (float)(Math.random() * 0.7 + 0.3f) // during glitch, static jumps up
                : frac * flicker;

        updateStaticTexture(w, h);

        // Static dynamique
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        ctx.drawTexture(dynamicStaticId, 0, 0, 0, 0, w, h, w, h);

        // PNG static overlay par dessus (optionnel pour boost le look)
        RenderSystem.setShaderColor(1f, 1f, 1f, Math.min(1, alpha * 0.5f));
        ctx.drawTexture(STATIC_OVERLAY, 0, 0, 0, 0, w, h, w, h);

        // Scanlines si tu veux (décommente si besoin)
        // RenderSystem.setShaderColor(1f, 1f, 1f, 0.10f * alpha);
        // ctx.drawTexture(SCANLINES, 0, 0, 0, 0, w, h, w, h);

        // Glitch flash (flash blanc ultra rapide)
        if (isGlitching && glitchTicks % 4 == 0) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 0.55f);
            ctx.fill(0, 0, w, h, 0x99FFFFFF);
        }

        // Reset blend/color
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }

    @Override
    public void close() {
        super.close();
        closeStaticImage();
    }
}
