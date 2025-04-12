package net.dark.spv_addon;

import net.dark.spv_addon.battery.BatteryManager;
import net.dark.spv_addon.client.ClientFlashlightRendererAddon;
import net.dark.spv_addon.client.KeyInterceptorClient;
import net.dark.spv_addon.client.gui.BatteryEmptyHUD;
import net.dark.spv_addon.client.gui.BatteryHudRenderer;
import net.dark.spv_addon.client.hud.LowBatteryHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class Spv_addonClient implements ClientModInitializer {

    private final ClientFlashlightRendererAddon flashlightRenderer = new ClientFlashlightRendererAddon();

    public static int getBatteryLevel(UUID uuid) {
        return BatteryManager.getBattery(uuid);
    }

    public static int getLocalBatteryLevel() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            return BatteryManager.getBattery(player.getUuid());
        }
        return 0;
    }
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            flashlightRenderer.tick(client.getTickDelta());
        });
        new KeyInterceptorClient().onInitializeClient();
        HudRenderCallback.EVENT.register(new BatteryEmptyHUD());
        HudRenderCallback.EVENT.register(new BatteryHudRenderer());
        HudRenderCallback.EVENT.register(new LowBatteryHud());

    }
}
