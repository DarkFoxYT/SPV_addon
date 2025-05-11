package net.dark.spv_addon;

import com.sp.compat.modmenu.ConfigDefinitions;
import com.sp.render.pbr.PbrRegistry;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.definition.ShaderPreDefinitions;
import foundry.veil.platform.VeilEventPlatform;
import net.dark.spv_addon.Additions.Sanity.SanityLightDebugRenderer;
import net.dark.spv_addon.client.ClientFlashlightRendererAddon;
import net.dark.spv_addon.client.gui.BatteryHud;
import net.dark.spv_addon.client.gui.SanityBar;
import net.dark.spv_addon.client.gui.ThirstHud;
import net.dark.spv_addon.entities.client.renderer.BellWalkerRenderer;
import net.dark.spv_addon.entities.client.renderer.KittyRenderer;
import net.dark.spv_addon.entities.client.renderer.SaniRenderer;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.entities.custom.KittyEntity;
import net.dark.spv_addon.entities.custom.SanityStalkerEntity;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.init.ModBlocks;
import net.dark.spv_addon.init.ModEntities;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.dark.spv_addon.init.ModKeybinds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.Random;


@Environment(EnvType.CLIENT)
public class Spv_addonClient implements ClientModInitializer {
    public static Framebuffer runShadowFbo;
    private final ClientFlashlightRendererAddon flashlightRenderer = new ClientFlashlightRendererAddon();
    private static final Random RANDOM = new Random();

    @Override
    public void onInitializeClient() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            flashlightRenderer.tick(client.getTickDelta());
        });
        flashlightRenderer.tick(10);
        BatteryHud.register();
        ThirstHud.register();
        ThirstManager.register();
        SanityLightDebugRenderer.init();

        FabricDefaultAttributeRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerRenderer::new);

        FabricDefaultAttributeRegistry.register(ModEntities.SANI_TY, SanityStalkerEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.SANI_TY, SaniRenderer::new);

        FabricDefaultAttributeRegistry.register(ModEntities.KITTY, KittyEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.KITTY, KittyRenderer::new);


        HudRenderCallback.EVENT.register(new SanityBar());
        BatteryHud.register();
        ThirstHud.register();




        VeilEventPlatform.INSTANCE.preVeilPostProcessing((name, pipeline, context) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;
            ShaderPreDefinitions defs = VeilRenderSystem.renderer().getShaderDefinitions();


            ConfigDefinitions.definitions.forEach((key, supplier) -> {
                if (supplier.get()) {
                    defs.define(key);
                } else {
                    defs.remove(key);
                }
            });


            com.sp.init.BackroomsLevels.definitions.forEach((key, worldKey) -> {
                if (client.world != null && client.world.getRegistryKey() == worldKey) {
                    defs.define(key);
                } else {
                    defs.remove(key);
                }
            });
        });





        PbrRegistry.registerPBR(ModBlocks.HOTEL_WALL, new PbrRegistry.PbrMaterial(false, 0.1f, 0.9f, 1024));
        PbrRegistry.registerPBR(ModBlocks.HOTEL_FLOOR, new PbrRegistry.PbrMaterial(false, 0.1f, 0.9f, 1024));


    }
}
