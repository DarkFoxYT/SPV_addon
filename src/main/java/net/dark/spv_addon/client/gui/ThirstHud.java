package net.dark.spv_addon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.ThirstComponent;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class ThirstHud implements HudRenderCallback {
    private static final Identifier THIRST_ICONS = new Identifier("spv_addon", "textures/gui/thirst_icon.png");
    // Texture : 32x32px, barre vide gauche (0,0), barre pleine droite (32,0)
    private static final int ICON_W = 44, ICON_H = 64;
    private static final float SCALE = 0.5f;
    private static final int Y_OFFSET = 55; // Distance du bas de l'écran

    @Override
    public void onHudRender(DrawContext dc, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        ThirstComponent thirst = InitializeComponents.THIRST.getNullable(player);
        if (thirst == null) return;

        int level = thirst.getThirst(); // 0–100
        float norm = Math.max(0, Math.min(level, 100)) / 100f;
        int filledHeight = Math.round(norm * ICON_H);

        float alpha = (level <= 15) ? getPulseAlpha() : 1f;

        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();
        int x = (sw - (int)(ICON_W * SCALE)) / 2;
        int y = sh - Y_OFFSET;

        dc.getMatrices().push();
        dc.getMatrices().translate(x, y, 0);
        dc.getMatrices().scale(SCALE, SCALE, SCALE);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);

        // Barre vide (background)
        dc.drawTexture(THIRST_ICONS, 0, 0, 0, 0, ICON_W, ICON_H, 88, 64);

        // Barre pleine (remplissage vertical)
        if (filledHeight > 0) {
            dc.drawTexture(THIRST_ICONS, 0, ICON_H - filledHeight, ICON_W, ICON_H - filledHeight, ICON_W, filledHeight, 88, 64);
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        dc.getMatrices().pop();

        // Texte %
        String txt = level + "%";
        int tw = client.textRenderer.getWidth(txt);
        dc.drawText(client.textRenderer, txt, x + (int)(ICON_W * SCALE) / 2 - tw / 2, y + (int)(ICON_H * SCALE) + 2, 0xFFFFFF, true);
    }

    private float getPulseAlpha() {
        double t = Util.getMeasuringTimeMs() / 600.0;
        double sway = (Math.sin(t) + 1.0) / 2.0;
        return 0.3f + (float)(sway * 0.7f);
    }

    public static void register() {
        HudRenderCallback.EVENT.register(new ThirstHud());
    }
}
