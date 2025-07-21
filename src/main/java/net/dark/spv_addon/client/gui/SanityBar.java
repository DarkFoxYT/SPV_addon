package net.dark.spv_addon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.config.SpvAddonConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class SanityBar implements HudRenderCallback {
    private static final Identifier SANITY_EMPTY = new Identifier("spv_addon", "textures/gui/sanity_0.png");
    private static final Identifier SANITY_FULL = new Identifier("spv_addon", "textures/gui/sanity_icon.png");
    private static final int ICON_W = 32, ICON_H = 32; // Taille de la texture
    private static final float SCALE = 1.0f; // Pour garder même rendu que les autres
    private static final int X_MARGIN = 16 + (int) (44 * 0.75f) + 12; // Décale à droite de la ThirstHud (qui fait ~33px avec scale)
    private static final int Y_MARGIN = 16; // Même base en bas de l'écran

    public static void register() {
        HudRenderCallback.EVENT.register(new SanityBar());
    }

    @Override
    public void onHudRender(DrawContext dc, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        var compOpt = SanityComponent.KEY1.maybeGet(player);
        if (compOpt.isEmpty()) return;
        int sanity = Math.max(0, Math.min(100, compOpt.get().getSanityLevel()));

        float norm = sanity / 100f;
        int fillWidth = Math.round(norm * ICON_W);

        // Enhanced visual feedback based on sanity level
        float alpha = getAlphaForSanity(sanity);
        float[] color = getColorForSanity(sanity);

        int sh = client.getWindow().getScaledHeight();
        int x = X_MARGIN; // Juste à droite de la ThirstHud
        int y = sh - (int) (ICON_H * SCALE) - 16; // Même base que la soif

        dc.getMatrices().push();
        dc.getMatrices().translate(x, y, 0);
        dc.getMatrices().scale(SCALE, SCALE, SCALE);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(color[0], color[1], color[2], alpha);

        // Fond vide
        dc.drawTexture(SANITY_EMPTY, 0, 0, 0, 0, ICON_W, ICON_H, ICON_W, ICON_H);

        // Remplissage horizontal with color tinting
        if (fillWidth > 0) {
            dc.drawTexture(
                    SANITY_FULL,
                    0, 0,
                    0, 0,
                    fillWidth, ICON_H,
                    ICON_W, ICON_H
            );
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        dc.getMatrices().pop();

        // % texte sous la barre
        String txt = sanity + "%";
        int tw = client.textRenderer.getWidth(txt);
        dc.drawText(client.textRenderer, txt, x + (int) (ICON_W * SCALE) / 2 - tw / 2, y + (int) (ICON_H * SCALE) + 2, 0xFFFFFF, true);
    }

    private float getPulseAlpha() {
        double t = Util.getMeasuringTimeMs() / 600.0;
        double sway = (Math.sin(t) + 1.0) / 2.0;
        return 0.3f + (float) (sway * 0.7f);
    }

    /**
     * Get alpha value based on sanity level with enhanced effects
     */
    private float getAlphaForSanity(int sanity) {
        if (sanity <= 5) {
            // Extreme flashing at nightmare levels
            double t = Util.getMeasuringTimeMs() / 200.0;
            double flash = (Math.sin(t * 3.0) + 1.0) / 2.0;
            return 0.2f + (float) (flash * 0.8f);
        } else if (sanity <= 15) {
            // Fast pulsing at critical levels
            return getPulseAlpha();
        } else if (sanity <= 30) {
            // Slow pulsing at low levels
            double t = Util.getMeasuringTimeMs() / 1000.0;
            double sway = (Math.sin(t) + 1.0) / 2.0;
            return 0.6f + (float) (sway * 0.4f);
        } else {
            return 1.0f;
        }
    }

    /**
     * Get color tint based on sanity level using configuration presets
     */
    private float[] getColorForSanity(int sanity) {
        return SpvAddonConfig.getSanityColorFloat(sanity);
    }

    /**
     * Get text color based on sanity level
     */
    private int getTextColorForSanity(int sanity) {
        if (sanity <= 5) {
            return 0xFF4444; // Dark red
        } else if (sanity <= 15) {
            return 0xFF6666; // Red
        } else if (sanity <= 30) {
            return 0xFFAA44; // Orange
        } else if (sanity <= 50) {
            return 0xFFDD66; // Yellow
        } else {
            return 0xFFFFFF; // White
        }
    }

    /**
     * Get status text based on sanity level
     */
    private String getSanityStatusText(int sanity) {
        if (sanity <= 5) {
            return "NIGHTMARE";
        } else if (sanity <= 15) {
            return "CRITICAL";
        } else if (sanity <= 30) {
            return "UNSTABLE";
        } else if (sanity <= 50) {
            return "STRESSED";
        } else {
            return "";
        }
    }
}
