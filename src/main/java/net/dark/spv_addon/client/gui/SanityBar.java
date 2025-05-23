package net.dark.spv_addon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class SanityBar implements HudRenderCallback {
    private static final Identifier SANITY_ICONS = new Identifier("spv_addon", "textures/gui/sanity_icon.png");
    // Texture : 88x64px = barre vide à gauche (0,0), barre pleine à droite (88,0)
    private static final int ICON_W = 32, ICON_H = 32;
    private static final float SCALE = 1f;
    private static final int MARGIN_X = 6, MARGIN_Y = 6;

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        SanityComponent sanity = InitializeComponents.SANITY.getNullable(player);
        if (sanity == null) return;

        int level = sanity.getSanityLevel(); // 0 à 100
        float norm = Math.max(0, Math.min(level, 100)) / 100f;
        int filledHeight = Math.round(norm * ICON_H);

        // Fade-out & pulse alpha si sanity critique
        float alpha = (level <= 15) ? getPulseAlpha() : 1f;

        int x = MARGIN_X;
        int y = MARGIN_Y;

        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(SCALE, SCALE, SCALE);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);

        // Barre vide (background)
        context.drawTexture(SANITY_ICONS, 0, 0, 0, 0, ICON_W, ICON_H, 64, 32);
        // Barre pleine (remplissage vertical)
        if (filledHeight > 0) {
            context.drawTexture(SANITY_ICONS, 0, ICON_H - filledHeight, ICON_W, ICON_H - filledHeight, ICON_W, filledHeight, 64, 32);
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        context.getMatrices().pop();

        // Texte %
        String txt = level + "%";
        int tw = client.textRenderer.getWidth(txt);
        context.drawText(client.textRenderer, Text.literal(txt), x + (int)(ICON_W * SCALE) / 2 - tw / 2, y + (int)(ICON_H * SCALE), 0xFFFFFF, false);
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
