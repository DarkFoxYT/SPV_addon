package net.dark.spv_addon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.ThirstComponent;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ThirstHud implements HudRenderCallback {
    private static final Identifier THIRST_EMPTY = new Identifier("spv_addon", "textures/gui/thirst_0.png");
    private static final Identifier THIRST_FULL = new Identifier("spv_addon", "textures/gui/thirst_icon.png");
    // Les textures doivent faire 44x64px (fond et remplissage)
    private static final int ICON_W = 64, ICON_H = 44;
    private static final float SCALE = 0.75f; // Ajuste pour la taille finale affichée
    private static final int X_MARGIN = 70, Y_MARGIN = 40; // Position à caler selon ta GUI

    public static void register() {
        HudRenderCallback.EVENT.register(new ThirstHud());
    }

    @Override
    public void onHudRender(DrawContext dc, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        ThirstComponent thirst = InitializeComponents.THIRST.getNullable(player);
        if (thirst == null) return;

        int level = thirst.getThirst();
        float norm = Math.max(0, Math.min(level, 100)) / 100f;
        int filledHeight = Math.round(norm * ICON_H);

        // Enhanced visual feedback based on thirst level
        float alpha = getAlphaForThirst(level);
        float[] color = getColorForThirst(level);

        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();

        int x = 16; // 16px depuis le bord gauche
        int y = sh - (int) (ICON_H * SCALE) - 16; // 16px depuis le bas

        dc.getMatrices().push();
        dc.getMatrices().translate(x, y, 0);
        dc.getMatrices().scale(SCALE, SCALE, SCALE);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);

        dc.drawTexture(THIRST_EMPTY, 0, 0, 0, 0, ICON_W, ICON_H, ICON_W, ICON_H);

        if (filledHeight > 0) {
            // Apply color tinting based on thirst level
            RenderSystem.setShaderColor(color[0], color[1], color[2], alpha);
            dc.drawTexture(
                    THIRST_FULL,
                    0, ICON_H - filledHeight,
                    0, ICON_H - filledHeight,
                    ICON_W, filledHeight,
                    ICON_W, ICON_H
            );
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        dc.getMatrices().pop();

        // Enhanced thirst display with status
        String txt = level + "%";
        String statusText = ThirstManager.getThirstStatusText(level);

        int tw = client.textRenderer.getWidth(txt);
        int statusTw = client.textRenderer.getWidth(statusText);

        // Main percentage text
        int textColor = ThirstManager.getThirstColor(level);
        dc.drawText(client.textRenderer, txt, x + (int) (ICON_W * SCALE) / 2 - tw / 2, y + (int) (ICON_H * SCALE) + 2, textColor, true);

        // Status text below percentage
        if (!statusText.isEmpty()) {
            dc.drawText(client.textRenderer, statusText, x + (int) (ICON_W * SCALE) / 2 - statusTw / 2, y + (int) (ICON_H * SCALE) + 14, textColor, true);
        }
    }

    private float getPulseAlpha() {
        double t = Util.getMeasuringTimeMs() / 600.0;
        double sway = (Math.sin(t) + 1.0) / 2.0;
        return 0.3f + (float) (sway * 0.7f);
    }

    /**
     * Get alpha value based on thirst level with enhanced effects
     */
    private float getAlphaForThirst(int thirst) {
        if (thirst <= 0) {
            // Extreme flashing when dying of thirst
            double t = Util.getMeasuringTimeMs() / 150.0;
            double flash = (Math.sin(t * 4.0) + 1.0) / 2.0;
            return 0.1f + (float) (flash * 0.9f);
        } else if (thirst <= 10) {
            // Fast pulsing at dangerous levels
            double t = Util.getMeasuringTimeMs() / 300.0;
            double pulse = (Math.sin(t * 2.0) + 1.0) / 2.0;
            return 0.2f + (float) (pulse * 0.8f);
        } else if (thirst <= 20) {
            // Regular pulsing at critical levels
            return getPulseAlpha();
        } else if (thirst <= 40) {
            // Slow pulsing at low levels
            double t = Util.getMeasuringTimeMs() / 1000.0;
            double sway = (Math.sin(t) + 1.0) / 2.0;
            return 0.6f + (float) (sway * 0.4f);
        } else {
            return 1.0f;
        }
    }

    /**
     * Get color tint based on thirst level
     */
    private float[] getColorForThirst(int thirst) {
        if (thirst <= 0) {
            // Dark red for dying
            return new float[]{0.5f, 0.1f, 0.1f};
        } else if (thirst <= 10) {
            // Red for dangerous
            return new float[]{1.0f, 0.2f, 0.2f};
        } else if (thirst <= 20) {
            // Orange red for critical
            return new float[]{1.0f, 0.3f, 0.1f};
        } else if (thirst <= 40) {
            // Orange for low
            return new float[]{1.0f, 0.6f, 0.2f};
        } else if (thirst <= 60) {
            // Yellow for moderate
            return new float[]{1.0f, 0.9f, 0.4f};
        } else {
            // Blue for hydrated
            return new float[]{0.4f, 0.8f, 1.0f};
        }
    }
}
