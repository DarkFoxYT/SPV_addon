package net.dark.spv_addon;

import com.sp.SPBRevamped;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import net.dark.spv_addon.battery.BatteryManager;
import net.dark.spv_addon.client.ClientFlashlightRendererAddon;
import net.dark.spv_addon.client.gui.BatteryHud;
import net.dark.spv_addon.entities.client.renderer.BellWalkerRenderer;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.init.ModEntities;
import net.dark.spv_addon.render.CutsceneManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.util.Identifier;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class Spv_addonClient implements ClientModInitializer {
    public static Framebuffer runShadowFbo;
    private final ClientFlashlightRendererAddon flashlightRenderer = new ClientFlashlightRendererAddon();

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


        MinecraftClient client = MinecraftClient.getInstance();

    }
}
