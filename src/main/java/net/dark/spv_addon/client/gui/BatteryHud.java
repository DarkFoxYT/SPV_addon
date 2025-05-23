package net.dark.spv_addon.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.dark.spv_addon.battery.BatteryManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import static net.dark.spv_addon.Spv_addon.MOD_ID;

public class BatteryHud implements HudRenderCallback {
    private static final Identifier BATTERY_1 = new Identifier(MOD_ID, "textures/gui/battery1.png");
    private static final Identifier BATTERY_2 = new Identifier(MOD_ID, "textures/gui/battery2.png");
    // ATTENTION : texture attendue 22x16 (barre vide à gauche, barre pleine à droite)
    private static final int BAR_W = 44, BAR_H = 44;
    private static final float SCALE = 1f;
    private static final int X_MARGIN = 6, Y_MARGIN = 40; // top right, sous la mini-carte ou autre HUD

    @Override
    public void onHudRender(DrawContext dc, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        int level = BatteryManager.getBattery(player.getUuid());
        PlayerComponent comp = InitializeComponents.PLAYER.getNullable(player);

        // Affiche seulement si lampe torche allumée OU batterie à 0
        if (level != 0 && (comp == null || !comp.isFlashLightOn())) return;


        float norm = Math.max(0, Math.min(level, 100)) / 100f;
        int filledHeight = Math.round(norm * BAR_H);

        float alpha = (level <= 15) ? getPulseAlpha() : 1f;

        int sw = client.getWindow().getScaledWidth();
        int x = sw - (int)(BAR_W * SCALE) - X_MARGIN;
        int y = Y_MARGIN;

        dc.getMatrices().push();
        dc.getMatrices().translate(x, y, 0);
        dc.getMatrices().scale(0.75f, 0.75f, 0.75f);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);

        // Dessine la barre vide (background)
        dc.drawTexture(BATTERY_1, 0, -1, 0, 0, BAR_W, BAR_H, 44, 44);

        // Dessine la barre pleine (remplissage horizontal)
        if (filledHeight > 0) {
            dc.drawTexture(BATTERY_2, 0, BAR_H - filledHeight, BAR_W, BAR_H - filledHeight, BAR_W, filledHeight, 44, 44);
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        dc.getMatrices().pop();

        // Affiche le pourcentage juste sous la barre
        String txt = level + "%";
        int tw = client.textRenderer.getWidth(txt);
        int tx = x + (int)(BAR_W * 1f) / 2 - tw / 2;
        int ty = y + (int)(BAR_H * 1f) / 2;
        dc.drawText(client.textRenderer, txt, tx, ty, 0xFFFFFF, true);
    }

    private float getPulseAlpha() {
        double t = Util.getMeasuringTimeMs() / 600.0;
        double sway = (Math.sin(t) + 1.0) / 2.0;
        return 0.3f + (float)(sway * 0.7f);
    }

    public static void register() {
        HudRenderCallback.EVENT.register(new BatteryHud());
    }
}
