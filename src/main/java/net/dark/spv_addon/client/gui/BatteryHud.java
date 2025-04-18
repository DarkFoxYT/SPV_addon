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
    private static final Identifier TEX_FULL = new Identifier(MOD_ID, "textures/gui/battery_100.png");
    private static final Identifier TEX_75   = new Identifier(MOD_ID, "textures/gui/battery_75.png");
    private static final Identifier TEX_50   = new Identifier(MOD_ID, "textures/gui/battery_50.png");
    private static final Identifier TEX_25   = new Identifier(MOD_ID, "textures/gui/battery_25.png");
    private static final Identifier TEX_0    = new Identifier(MOD_ID, "textures/gui/battery_0.png");

    private static final int ICON_W = 88, ICON_H = 64;
    private static final float SCALE = 0.25f;
    private static final int MARGIN = 5;

    @Override
    public void onHudRender(DrawContext dc, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        int level = BatteryManager.getBattery(player.getUuid());
        PlayerComponent comp = InitializeComponents.PLAYER.get(player);

        // Show if battery==0 (even flashlight off), otherwise only if flashlight is on:
        if (level != 0 && (comp == null || !comp.isFlashLightOn())) return;

        Identifier tex = (level == 0)   ? TEX_0
                : (level <= 25) ? TEX_25
                : (level <= 50) ? TEX_50
                : (level <= 75) ? TEX_75
                : TEX_FULL;

        // pulsing alpha at low battery or zero
        float alpha = 1f;
        if (level <= 25) {
            double t    = Util.getMeasuringTimeMs() / 500.0;
            double sway = (Math.sin(t) + 1.0) / 2.0;
            alpha = 0.25f + (float)(sway * 0.75f);
        }

        int sw = client.getWindow().getScaledWidth();
        int x  = sw - (int)(ICON_W * SCALE) - MARGIN;
        int y  = MARGIN;

        // draw the full 88×64 texture scaled down by SCALE
        dc.getMatrices().push();
        dc.getMatrices().translate(x, y, 0);
        dc.getMatrices().scale(SCALE, SCALE, 1f);

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        dc.drawTexture(tex, 0, 0, 0, 0, ICON_W, ICON_H, ICON_W, ICON_H);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();

        dc.getMatrices().pop();

        // draw percentage under the icon
        String txt = level + "%";
        int tw = client.textRenderer.getWidth(txt);
        int tx = x + (int)(ICON_W * SCALE - tw);
        int ty = y + (int)(ICON_H * SCALE) + 1;

        dc.getMatrices().push();
        RenderSystem.enableBlend();
        dc.drawText(client.textRenderer, txt, tx, ty, 0xFFFFFF, true);
        RenderSystem.disableBlend();
        dc.getMatrices().pop();
    }

    /** Call once in your ClientModInitializer **/
    public static void register() {
        HudRenderCallback.EVENT.register(new BatteryHud());
    }
}
