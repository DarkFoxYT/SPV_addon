package net.dark.spv_addon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dark.spv_addon.init.ModSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;


@Environment(EnvType.CLIENT)
public class CustomDeathScreen extends Screen {
    private static final Identifier BACKGROUND = new Identifier("spv_addon", "textures/gui/death_full.png");
    private static final Identifier SCANLINES = new Identifier("spv_addon", "textures/gui/scanlines.png");
    private static final int STATIC_REFRESH_INTERVAL = 2;
    private static final float STATIC_SCALE = 0.25f;
    private final String playerName;
    private final Identifier dynamicStaticId = new Identifier("spv_addon", "dynamic_static");
    private int ticksElapsed = 0;
    private int glitchTicks = 0;
    private int lastStaticUpdateTick = -1;
    private boolean isGlitching = false;
    private NativeImage staticImage = null;
    private NativeImageBackedTexture staticTexture = null;

    public CustomDeathScreen(String playerName) {
        super(Text.empty());
        this.playerName = playerName;
        playDeathSound();
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
            if (glitchTicks >= 16) {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.player != null) mc.player.requestRespawn();
                mc.setScreen(null);
                closeStaticImage();
            }
        }
    }

    private void updateStaticTexture(int w, int h) {
        int textureWidth = Math.max(64, Math.round(w * STATIC_SCALE));
        int textureHeight = Math.max(64, Math.round(h * STATIC_SCALE));
        if (staticImage == null || staticImage.getWidth() != textureWidth || staticImage.getHeight() != textureHeight) {
            closeStaticImage();
            staticImage = new NativeImage(textureWidth, textureHeight, false);
            staticTexture = new NativeImageBackedTexture(staticImage);
        }
        for (int y = 0; y < textureHeight; y++) {
            for (int x = 0; x < textureWidth; x++) {
                int val = isGlitching ? (int) (Math.random() * 255) : (int) (Math.random() * 192 + 32);
                int color = 0xFF000000 | (val << 16) | (val << 8) | val;
                staticImage.setColor(x, y, color);
            }
        }
        staticTexture.upload();
        MinecraftClient.getInstance().getTextureManager().registerTexture(dynamicStaticId, staticTexture);
    }

    private void closeStaticImage() {
        if (staticImage != null) staticImage.close();
        if (staticTexture != null) staticTexture.close();
        staticImage = null;
        staticTexture = null;
    }

    private void startGlitch() {
        isGlitching = true;
        glitchTicks = 0;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int w = mc.getWindow().getScaledWidth();
        int h = mc.getWindow().getScaledHeight();

        RenderSystem.enableBlend();
        ctx.drawTexture(BACKGROUND, 0, 0, 0, 0, w, h, w, h);
        RenderSystem.disableBlend();

        String line1 = playerName + " was";
        String line2 = "Never Found";
        String line3 = isGlitching ? "RESPAWNING" : "Signal lost";
        int color = 0xFFFFFF;
        float titleScale = MathHelper.clamp(Math.min(w, h) / 180.0f, 1.2f, 2.6f);
        float subtitleScale = Math.max(1.0f, titleScale * 0.55f);
        int fontHeight = mc.textRenderer.fontHeight + 8;

        ctx.getMatrices().push();
        ctx.getMatrices().translate(w / 2f, h / 2f - fontHeight * titleScale * 0.6f, 0f);
        ctx.getMatrices().scale(titleScale, titleScale, 1f);
        int x1 = -mc.textRenderer.getWidth(line1) / 2;
        int y1 = 0;
        ctx.drawText(mc.textRenderer, Text.literal(line1), x1, y1, color, false);

        int x2 = -mc.textRenderer.getWidth(line2) / 2;
        int y2 = Math.round((fontHeight + 2) / titleScale);
        ctx.drawText(mc.textRenderer, Text.literal(line2), x2, y2, color, false);
        ctx.getMatrices().pop();

        ctx.getMatrices().push();
        ctx.getMatrices().translate(w / 2f, h / 2f + fontHeight * 1.35f, 0f);
        ctx.getMatrices().scale(subtitleScale, subtitleScale, 1f);
        int x3 = -mc.textRenderer.getWidth(line3) / 2;
        ctx.drawText(mc.textRenderer, Text.literal(line3), x3, 0, 0xCFCFCF, false);
        ctx.getMatrices().pop();

        float frac = Math.min(ticksElapsed / 200f, 1f);
        float flicker = (float) (Math.sin(ticksElapsed * 0.6 + Math.random()) * 0.15 + 0.85);
        float alpha = isGlitching
                ? (float) (Math.random() * 0.7 + 0.3f) // during glitch, static jumps up
                : frac * flicker;
        int animationTick = ticksElapsed + glitchTicks;

        if (lastStaticUpdateTick != animationTick) {
            if (animationTick % STATIC_REFRESH_INTERVAL == 0 || staticTexture == null) {
                updateStaticTexture(w, h);
                lastStaticUpdateTick = animationTick;
            }
        }

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        if (staticImage != null) {
            ctx.drawTexture(dynamicStaticId, 0, 0, 0, 0, w, h, staticImage.getWidth(), staticImage.getHeight());
        }


        RenderSystem.setShaderColor(1f, 1f, 1f, 0.10f * alpha);
        ctx.drawTexture(SCANLINES, 0, 0, 0, 0, w, h, w, h);


        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }

    @Override
    public void close() {
        super.close();
        stopDeathSound();
        closeStaticImage();
    }

    private void playDeathSound() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) {
            mc.getSoundManager().play(net.minecraft.client.sound.PositionedSoundInstance.music(ModSounds.DEATH_SOUND));
        }
    }

    private void stopDeathSound() {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.getSoundManager().stopSounds(ModSounds.DEATH_SOUND.getId(), net.minecraft.sound.SoundCategory.MUSIC);

    }
}
