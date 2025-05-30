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
    private static final Identifier THIRST_EMPTY = new Identifier("spv_addon", "textures/gui/thirst_0.png");
    private static final Identifier THIRST_FULL  = new Identifier("spv_addon", "textures/gui/thirst_icon.png");
    // Les textures doivent faire 44x64px (fond et remplissage)
    private static final int ICON_W = 64, ICON_H = 44;
    private static final float SCALE = 0.75f; // Ajuste pour la taille finale affichée
    private static final int X_MARGIN = 70, Y_MARGIN = 40; // Position à caler selon ta GUI

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

        float alpha = (level <= 15) ? getPulseAlpha() : 1f;

        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();

        int x = 16; // 16px depuis le bord gauche
        int y = sh - (int)(ICON_H * SCALE) - 16; // 16px depuis le bas

        dc.getMatrices().push();
        dc.getMatrices().translate(x, y, 0);
        dc.getMatrices().scale(SCALE, SCALE, SCALE);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);

        dc.drawTexture(THIRST_EMPTY, 0, 0, 0, 0, ICON_W, ICON_H, ICON_W, ICON_H);

        if (filledHeight > 0) {
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
