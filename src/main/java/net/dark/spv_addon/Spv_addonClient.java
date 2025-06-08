package net.dark.spv_addon;

import com.mojang.blaze3d.systems.RenderSystem;
import com.sp.render.pbr.BlockIdMap;
import com.sp.render.pbr.PbrRegistry;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.dark.spv_addon.client.ClientFlashlightRendererAddon;
import net.dark.spv_addon.client.CustomSkyboxRenderer;
import net.dark.spv_addon.client.FocusHandler;
import net.dark.spv_addon.client.SkyShaderHandler;
import net.dark.spv_addon.client.gui.BatteryHud;
import net.dark.spv_addon.client.gui.SanityBar;
import net.dark.spv_addon.client.gui.ThirstHud;
import net.dark.spv_addon.entities.client.renderer.BellWalkerRenderer;
import net.dark.spv_addon.entities.client.renderer.IKEAWalkerRenderer;
import net.dark.spv_addon.entities.client.renderer.KittyRenderer;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.entities.custom.IkeaWalkerEntity;
import net.dark.spv_addon.entities.custom.KittyEntity;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.init.ModBlocks;
import net.dark.spv_addon.init.ModEntities;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.dark.spv_addon.init.grass.GrassRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class Spv_addonClient implements ClientModInitializer {
    private GrassRenderer grassRenderer;

    private final ClientFlashlightRendererAddon flashlightRenderer = new ClientFlashlightRendererAddon();
    public static final DefaultParticleType RAIN_PARTICLE = FabricParticleTypes.simple();

    @Override
    public void onInitializeClient() {

        // Registers Hud elements and other client-side features
        BatteryHud.register();
        ThirstHud.register();
        SanityBar.register();
        SkyShaderHandler.register();
        FocusHandler.register();
        ThirstManager.register();

        // Register the flashlight renderer
        ClientTickEvents.END_CLIENT_TICK.register(client -> flashlightRenderer.tick(client.getTickDelta()));


        // Stuff for entities using IK
        FabricDefaultAttributeRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerRenderer::new);

        FabricDefaultAttributeRegistry.register(ModEntities.KITTY, KittyEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.KITTY, KittyRenderer::new);

        FabricDefaultAttributeRegistry.register(ModEntities.IKEA_WALKER, IkeaWalkerEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.IKEA_WALKER, IKEAWalkerRenderer::new);

        // Registers Blocks and their render layers
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.KITTY_PLUSHIE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.EXIT_SIGN, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TABLE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BED1, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BED2, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.IKEA_SHELF, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.IKEA_SHELF1, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.IKEA_SHELF2, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.KITTY_PLUSHIE1, RenderLayer.getTranslucent());

        // PBR Materials and Block IDs but sill figuring it out caus // the PBR system is still in development
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return new Identifier("spv_addon", "after_resources");
                    }
                    @Override
                    public void reload(ResourceManager manager) {

                        BlockIdMap.registerBlockID(blockIdMap -> {
                        blockIdMap.put(ModBlocks.KITTY_FLOOR, 45);

                        });


                        PbrRegistry.registerPBR(ModBlocks.KITTY_FLOOR, new PbrRegistry.PbrMaterial(false, 0.35F, 2.0F, 256));
                    }

                });


        VeilEventPlatform.INSTANCE.onVeilRenderTypeStageRender((stage, levelRenderer, bufferSource, poseStack, projectionMatrix, renderTick, partialTicks, camera, frustum) -> {
                        MinecraftClient client = MinecraftClient.getInstance();
            World clientWorld = client.world;
            if (clientWorld != null) {

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

        WorldRenderEvents.START.register(ctx -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world != null
                    && client.world.getRegistryKey().equals(BackroomsLevels.LEVEL207_WORLD_KEY)) {

                RenderSystem.disableBlend();
                RenderSystem.depthMask(false);
                CustomSkyboxRenderer.render(ctx.matrixStack(), ctx.tickDelta());
                RenderSystem.depthMask(true);
                RenderSystem.enableBlend();
            }
        });
    }
}
