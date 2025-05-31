package net.dark.spv_addon;

import com.sp.SPBRevamped;
import com.sp.render.pbr.BlockIdMap;
import com.sp.render.pbr.PbrRegistry;
import net.dark.spv_addon.Additions.Sanity.SanityLightDebugRenderer;
import net.dark.spv_addon.client.ClientFlashlightRendererAddon;
import net.dark.spv_addon.client.FocusHandler;
import net.dark.spv_addon.client.gui.BatteryHud;
import net.dark.spv_addon.client.gui.SanityBar;
import net.dark.spv_addon.client.gui.ThirstHud;
import net.dark.spv_addon.entities.client.renderer.BellWalkerRenderer;
import net.dark.spv_addon.entities.client.renderer.IKEAWalkerRenderer;
import net.dark.spv_addon.entities.client.renderer.KittyRenderer;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.entities.custom.IkeaWalkerEntity;
import net.dark.spv_addon.entities.custom.KittyEntity;
import net.dark.spv_addon.init.ModBlocks;
import net.dark.spv_addon.init.ModEntities;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.dark.spv_addon.init.ModParticles;
import net.dark.spv_addon.world.events.LevelRunTicker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class Spv_addonClient implements ClientModInitializer {
    private final ClientFlashlightRendererAddon flashlightRenderer = new ClientFlashlightRendererAddon();
    public static final DefaultParticleType RAIN_PARTICLE = FabricParticleTypes.simple();

    @Override
    public void onInitializeClient() {
        FocusHandler.register();
        ClientTickEvents.END_CLIENT_TICK.register(client -> flashlightRenderer.tick(client.getTickDelta()));


        BatteryHud.register();
        ThirstHud.register();
        SanityBar.register();
        LevelRunTicker.init();

        ThirstManager.register();
        SanityLightDebugRenderer.init();

        FabricDefaultAttributeRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerRenderer::new);

        FabricDefaultAttributeRegistry.register(ModEntities.KITTY, KittyEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.KITTY, KittyRenderer::new);

        FabricDefaultAttributeRegistry.register(ModEntities.IKEA_WALKER, IkeaWalkerEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.IKEA_WALKER, IKEAWalkerRenderer::new);

        ModParticles.registerClientParticles();
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.KITTY_PLUSHIE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.EXIT_SIGN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TABLE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BED1, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BED2, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.IKEA_SHELF, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.IKEA_SHELF1, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.IKEA_SHELF2, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.KITTY_PLUSHIE1, RenderLayer.getTranslucent());

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return new Identifier("spv_addon", "after_resources");
                    }
                    @Override
                    public void reload(ResourceManager manager) {

                        // Register custom block IDs
                        BlockIdMap.registerBlockID(blockIdMap -> {
                            // Add other blocks as needed

                        });
                        // Register PBR materials
                        PbrRegistry.registerPBR(ModBlocks.HOTEL_WALL, new PbrRegistry.PbrMaterial(false, 0.35F, 2.0F, 512));

                        BlockIdMap.init = false;
                        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                            SPBRevamped.LOGGER.error("This mod is not compatible with MacOS. Please use Windows or Linux (wayland).");
                            MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.UNSECURE_SERVER_WARNING, Text.of("Potential Incompatibility found"), Text.of("This mod is not compatible with MacOS. Please use Windows or Linux (wayland).")));
                        }
                    }

                });


    }
}
