package net.dark.spv_addon;

import com.sp.render.PoolroomsDayCycle;
import com.sp.render.PreviousUniforms;
import com.sp.render.pbr.BlockIdMap;
import com.sp.render.pbr.PbrRegistry;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.dark.spv_addon.client.ClientFlashlightRendererAddon;
import net.dark.spv_addon.client.gui.BatteryHud;
import net.dark.spv_addon.client.gui.SanityBar;
import net.dark.spv_addon.client.gui.ThirstHud;
import net.dark.spv_addon.commands.WindowCutsceneCommand;
import net.dark.spv_addon.entities.client.renderer.BellWalkerRenderer;
import net.dark.spv_addon.entities.client.renderer.IKEAWalkerRenderer;
import net.dark.spv_addon.entities.client.renderer.KittyRenderer;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.entities.custom.IkeaWalkerEntity;
import net.dark.spv_addon.entities.custom.KittyEntity;
import net.dark.spv_addon.entities.custom.StalkerEntity;
import net.dark.spv_addon.init.ModBlocks;
import net.dark.spv_addon.init.ModEntities;
import net.dark.spv_addon.init.grass.GrassRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class Spv_addonClient implements ClientModInitializer {
    private static final Identifier EVERYTHING_SHADER = new Identifier("spb-revamped", "vhs/everything");
    private static final Identifier VHS_POST = new Identifier("spb-revamped", "vhs");
    private static final Identifier POST_VHS = new Identifier("spb-revamped", "vhs/vhs_post");
    private final ClientFlashlightRendererAddon flashlightRenderer = new ClientFlashlightRendererAddon();
    private GrassRenderer grassRenderer;

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                WindowCutsceneCommand.register(dispatcher));


        ClientTickEvents.END_CLIENT_TICK.register(client ->
                WindowCutsceneCommand.tick());

        BatteryHud.register();
        ThirstHud.register();
        SanityBar.register();
        ThirstManager.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> flashlightRenderer.tick(client.getTickDelta()));

        FabricDefaultAttributeRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerRenderer::new);
        FabricDefaultAttributeRegistry.register(ModEntities.KITTY, KittyEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.KITTY, KittyRenderer::new);
        FabricDefaultAttributeRegistry.register(ModEntities.IKEA_WALKER, IkeaWalkerEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.IKEA_WALKER, IKEAWalkerRenderer::new);
        FabricDefaultAttributeRegistry.register(ModEntities.STALKER_ENTITY, StalkerEntity.createAttributes());


        //BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.KITTY_PLUSHIE, RenderLayer.getTranslucent());
        //BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.EXIT_SIGN, RenderLayer.getTranslucent());
        //BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TABLE, RenderLayer.getTranslucent());
        //BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BED1, RenderLayer.getTranslucent());
        //BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BED2, RenderLayer.getTranslucent());
        //BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.IKEA_SHELF, RenderLayer.getTranslucent());
        //BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.IKEA_SHELF1, RenderLayer.getTranslucent());
        //BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.IKEA_SHELF2, RenderLayer.getTranslucent());
        //BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.KITTY_PLUSHIE1, RenderLayer.getTranslucent());


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
                            blockIdMap.put(ModBlocks.KITTY_PLUSHIE, 46);
                            blockIdMap.put(ModBlocks.KITTY_PLUSHIE1, 47);
                            blockIdMap.put(ModBlocks.KITTY_PLUSHIE_DEV, 48);
                            blockIdMap.put(ModBlocks.VENT, 49);

                        });


                        PbrRegistry.registerPBR(ModBlocks.KITTY_FLOOR, new PbrRegistry.PbrMaterial(false, 0.5F, 2.0F, 256));
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

        VeilEventPlatform.INSTANCE.preVeilPostProcessing((name, pipeline, context) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = MinecraftClient.getInstance().player;


            if (player != null && client.world != null) {
                if (VHS_POST.equals(name)) {
                    ShaderProgram shaderProgram = context.getShader(POST_VHS);
                    if (shaderProgram != null) {


                        shaderProgram = context.getShader(EVERYTHING_SHADER);
                        if (shaderProgram != null) {
                            if (client.world.getRegistryKey() == net.dark.spv_addon.init.BackroomsLevels.LEVEL207_WORLD_KEY) {
                                shaderProgram.setInt("FogToggle", 1);
                            } else {
                                shaderProgram.setInt("FogToggle", 0);
                            }


                            if (client.world.getRegistryKey() == net.dark.spv_addon.init.BackroomsLevels.LEVEL207_WORLD_KEY) {
                                shaderProgram.setInt("TogglePuddles", 1);
                            } else {
                                shaderProgram.setInt("TogglePuddles", 0);
                            }

                            shaderProgram.setVector("shadowColor", PoolroomsDayCycle.getLightColor());
                        }
                    }
                    PreviousUniforms.update();
                }
            }
        });
    }
}
