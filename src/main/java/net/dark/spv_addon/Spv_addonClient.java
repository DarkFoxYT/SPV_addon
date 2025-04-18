package net.dark.spv_addon;

import net.dark.spv_addon.battery.BatteryManager;
import net.dark.spv_addon.client.ClientFlashlightRendererAddon;
import net.dark.spv_addon.client.gui.BatteryHud;
import net.dark.spv_addon.entities.client.renderer.BellWalkerRenderer;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.init.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
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

        BatteryHud.register();

        FabricDefaultAttributeRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerEntity.createAttributes());

        EntityRendererRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerRenderer::new);
    }
}
