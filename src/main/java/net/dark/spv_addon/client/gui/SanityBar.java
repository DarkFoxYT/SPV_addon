package net.dark.spv_addon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dark.spv_addon.cca.SanityComponent;
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
    private static final Identifier SANITY_FULL  = new Identifier("spv_addon", "textures/gui/sanity_icon.png");
    private static final int ICON_W = 32, ICON_H = 32; // Taille de la texture
    private static final float SCALE = 1.0f; // Pour garder même rendu que les autres
    private static final int X_MARGIN = 16 + (int)(44 * 0.75f) + 12; // Décale à droite de la ThirstHud (qui fait ~33px avec scale)
    private static final int Y_MARGIN = 16; // Même base en bas de l'écran

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

        float alpha = (sanity <= 15) ? getPulseAlpha() : 1f;

        int sh = client.getWindow().getScaledHeight();
        int x = X_MARGIN; // Juste à droite de la ThirstHud
        int y = sh - (int)(ICON_H * SCALE) - 16; // Même base que la soif

        dc.getMatrices().push();
        dc.getMatrices().translate(x, y, 0);
        dc.getMatrices().scale(SCALE, SCALE, SCALE);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);

        // Fond vide
        dc.drawTexture(SANITY_EMPTY, 0, 0, 0, 0, ICON_W, ICON_H, ICON_W, ICON_H);

        // Remplissage horizontal
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
        dc.drawText(client.textRenderer, txt, x + (int)(ICON_W * SCALE) / 2 - tw / 2, y + (int)(ICON_H * SCALE) + 2, 0xFFFFFF, true);
    }

    private float getPulseAlpha() {
        double t = Util.getMeasuringTimeMs() / 600.0;
        double sway = (Math.sin(t) + 1.0) / 2.0;
        return 0.3f + (float)(sway * 0.7f);
    }

    public static void register() {
        HudRenderCallback.EVENT.register(new SanityBar());
    }
}
