package net.dark.spv_addon;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.networking.InitializePackets;
import net.dark.spv_addon.battery.BatteryManager;
import net.dark.spv_addon.client.ClientFlashlightRendererAddon;
import net.dark.spv_addon.client.gui.BatteryHud;
import net.dark.spv_addon.client.gui.CustomDeathScreen;
import net.dark.spv_addon.entities.client.renderer.BellWalkerRenderer;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.init.ModEntities;
import net.dark.spv_addon.render.CutsceneManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class Spv_addonClient implements ClientModInitializer {
    private static CutsceneManager cutsceneManager;

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


        flashlightRenderer.tick(10);

        BatteryHud.register();
        CutsceneManager.init();

        FabricDefaultAttributeRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerEntity.createAttributes());

        EntityRendererRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerRenderer::new);

    }
}
