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
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
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

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public void reload(ResourceManager manager) {
                        // Reinitialize block ID map
                        BlockIdMap.init();

                        // Register custom block IDs
                        BlockIdMap.registerBlockID(blockIdMap -> {
                            blockIdMap.put(ModBlocks.KITTY_WALL, 1001);
                            // Add other blocks as needed
                        });

                        // Register PBR materials
                        PbrRegistry.registerPBR(ModBlocks.KITTY_WALL, new PbrRegistry.PbrMaterial(true, 0.2F, 2.0F, 1024));

                        SPBRevamped.LOGGER.info("[SPV] Reloaded SPV PBR blocks & block ID map (client resource reload)");
                        BlockIdMap.init = false;
                    }

                    @Override
                    public Identifier getFabricId() {
                        return new Identifier("spv_addon", "after_resources");
                    }
                });


    }
}
