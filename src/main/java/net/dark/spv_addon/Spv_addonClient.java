package net.dark.spv_addon;

import com.sp.render.ShadowMapRenderer;
import com.sp.render.pbr.BlockIdMap;
import com.sp.render.pbr.PbrRegistry;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.dark.spv_addon.blocks.entities.rend.PlateBlockEntityRenderer;
import net.dark.spv_addon.Additions.api.ClientFlashlightRendererAddon;
import net.dark.spv_addon.client.effects.GlitchedLevelRenderController;
import net.dark.spv_addon.client.effects.StaticifyController;
import net.dark.spv_addon.client.gui.UnifiedHud;
import net.dark.spv_addon.Additions.sanity.SanityEffectsManager;
import net.dark.spv_addon.entities.client.renderer.BellWalkerRenderer;
import net.dark.spv_addon.entities.client.renderer.IKEAWalkerRenderer;
import net.dark.spv_addon.entities.client.renderer.KittyRenderer;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.entities.custom.IkeaWalkerEntity;
import net.dark.spv_addon.entities.custom.KittyEntity;
import net.dark.spv_addon.entities.custom.StalkerEntity;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.init.ModBlockEntities;
import net.dark.spv_addon.init.ModBlocks;
import net.dark.spv_addon.init.ModEntities;
import net.dark.spv_addon.init.config.SpvAddonConfig;
import net.dark.spv_addon.init.grass.GrassRenderer;
import net.dark.spv_addon.world.events.level207.Level207AmbienceEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.registry.RegistryKey;

import java.util.Set;

@Environment(EnvType.CLIENT)
public class Spv_addonClient implements ClientModInitializer {
    private static final Set<RegistryKey<World>> SHADOW_MAP_LEVELS = Set.of(
            BackroomsLevels.LEVEL188_WORLD_KEY,
            BackroomsLevels.LEVEL_KITTY_WORLD_KEY,
            BackroomsLevels.GLITCHED_WORLD_KEY
    );

    private final ClientFlashlightRendererAddon flashlightRenderer = new ClientFlashlightRendererAddon();
    private GrassRenderer grassRenderer;
    public static Camera camera;

    @Override
    public void onInitializeClient() {
        SpvAddonConfig.init("spv_addon", SpvAddonConfig.class);

        BlockEntityRendererFactories.register(ModBlockEntities.PLATE_BLOCK_ENTITY, PlateBlockEntityRenderer::new);

        UnifiedHud.register();

        net.dark.spv_addon.client.gui.BatteryHud.register();
        net.dark.spv_addon.client.gui.SanityBar.register();
        net.dark.spv_addon.client.gui.ThirstHud.register();
        new net.dark.spv_addon.init.crawl.CrawlClient().onInitializeClient();
        StaticifyController.init();
        GlitchedLevelRenderController.init();
        SanityEffectsManager.initialize();
        Level207AmbienceEvent.init();




        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            flashlightRenderer.tick(client.getTickDelta());
            SanityEffectsManager.getInstance().updateEffects(client.getTickDelta());
        });

        FabricDefaultAttributeRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerRenderer::new);
        FabricDefaultAttributeRegistry.register(ModEntities.KITTY, KittyEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.KITTY, KittyRenderer::new);
        FabricDefaultAttributeRegistry.register(ModEntities.IKEA_WALKER, IkeaWalkerEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.IKEA_WALKER, IKEAWalkerRenderer::new);
        FabricDefaultAttributeRegistry.register(ModEntities.STALKER_ENTITY, StalkerEntity.createAttributes());


        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return new Identifier("spv_addon", "after_resources");
                    }

                    @Override
                    public void reload(ResourceManager manager) {

                        BlockIdMap.registerBlockID(blockIdMap -> {
                            blockIdMap.put(ModBlocks.KITTY_FLOOR, 4500);
                            blockIdMap.put(ModBlocks.HOTEL_FLOOR, 4501);
                        });


                        PbrRegistry.registerPBR(ModBlocks.KITTY_FLOOR, new PbrRegistry.PbrMaterial(false, 0, 2, 256));
                        //PbrRegistry.registerPBR(ModBlocks.HOTEL_FLOOR, new PbrRegistry.PbrMaterial(false, 0, 2, 256));
                    }

                });


        VeilEventPlatform.INSTANCE.onVeilRenderTypeStageRender((stage, levelRenderer, bufferSource, poseStack, projectionMatrix, renderTick, partialTicks, camera, frustum) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            World clientWorld = client.world;
            if (clientWorld != null) {

                if (SHADOW_MAP_LEVELS.contains(clientWorld.getRegistryKey())
                        && stage == VeilRenderLevelStageEvent.Stage.AFTER_SKY
                        && camera != null) {
                    ShadowMapRenderer.renderShadowMap(camera, partialTicks, clientWorld);
                }


                if (clientWorld.getRegistryKey() != net.dark.spv_addon.init.BackroomsLevels.LEVEL207_WORLD_KEY) {
                    if (this.grassRenderer != null) {
                        this.grassRenderer.close();
                        this.grassRenderer = null;
                    }
                } else if (stage == VeilRenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
                    if (this.grassRenderer == null) {
                        this.grassRenderer = new GrassRenderer();
                    }

                    this.grassRenderer.render();
                }


            }
        });

    }


}
