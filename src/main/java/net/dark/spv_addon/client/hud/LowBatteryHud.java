// File: net.dark.spv_addon.client.hud.LowBatteryHud.java

package net.dark.spv_addon.client.hud;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.dark.spv_addon.battery.BatteryManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class LowBatteryHud implements HudRenderCallback {

    private static final Identifier ICON = new Identifier("spv_addon", "textures/gui/battery_warning.png");

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden || !client.isWindowFocused()) return;

        PlayerComponent comp = InitializeComponents.PLAYER.get(client.player);
        int battery = BatteryManager.getBattery(client.player.getUuid());

        if (battery <= 10 && battery > 0) {
            float time = (System.currentTimeMillis() % 1000) / 1000f;  // time from 0.0 to 1.0
            float alpha = (float) ((Math.sin(time * Math.PI * 2) + 1.0) / 2.0); // flicker

            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(0, 0, 0);
            drawContext.setShaderColor(1f, 1f, 1f, alpha); // flicker effect

            int width = 32;
            int height = 32;
            int x = client.getWindow().getScaledWidth() / 2 - width / 2;
            int y = client.getWindow().getScaledHeight() - 60;

            drawContext.drawTexture(ICON, x, y, 0, 0, width, height, 32, 32);

            drawContext.setShaderColor(1f, 1f, 1f, 1f);
            drawContext.getMatrices().pop();
        }
    }
}
