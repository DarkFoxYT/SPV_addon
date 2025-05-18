package net.dark.spv_addon;

import com.sp.compat.modmenu.ConfigDefinitions;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.client.render.shader.definition.ShaderPreDefinitions;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.platform.VeilEventPlatform;
import net.dark.spv_addon.Additions.Sanity.SanityLightDebugRenderer;
import net.dark.spv_addon.client.CameraZoomHandler;
import net.dark.spv_addon.client.ClientFlashlightRendererAddon;
import net.dark.spv_addon.client.ZoomHandler;
import net.dark.spv_addon.client.gui.BatteryHud;
import net.dark.spv_addon.client.gui.SanityBar;
import net.dark.spv_addon.client.gui.ThirstHud;
import net.dark.spv_addon.entities.client.renderer.BellWalkerRenderer;
import net.dark.spv_addon.entities.client.renderer.KittyRenderer;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.entities.custom.KittyEntity;
import net.dark.spv_addon.init.ModEntities;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

import static net.dark.spv_addon.client.CameraZoomHandler.*;

@Environment(EnvType.CLIENT)
public class Spv_addonClient implements ClientModInitializer {
    private final ClientFlashlightRendererAddon flashlightRenderer = new ClientFlashlightRendererAddon();
    public static final Identifier CAMERA_BLUR_POST = new Identifier("spv_addon", "camera_blur");

    @Override
    public void onInitializeClient() {
        CameraZoomHandler.register();




        VeilEventPlatform.INSTANCE.preVeilPostProcessing((name, pipeline, context) -> {
            if (CAMERA_BLUR_POST.equals(name)) {
                ShaderProgram shaderProgram = context.getShader(new Identifier("spv_addon:camera_blur"));
                if (shaderProgram != null) {
                    float blur = Math.max(0, (CameraZoomHandler.currentZoom - 1.0f)) * 6.0f; // Plus zoom = plus flou
                    shaderProgram.setFloat("BlurStrength", blur);
                    shaderProgram.setFloat("Zoom", CameraZoomHandler.currentZoom);
                }
            }
        });


        // Les autres inits de ton mod
        ClientTickEvents.END_CLIENT_TICK.register(client -> flashlightRenderer.tick(client.getTickDelta()));
        BatteryHud.register();
        ThirstHud.register();
        ThirstManager.register();
        SanityLightDebugRenderer.init();

        FabricDefaultAttributeRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerRenderer::new);

        FabricDefaultAttributeRegistry.register(ModEntities.KITTY, KittyEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.KITTY, KittyRenderer::new);

        HudRenderCallback.EVENT.register(new SanityBar());

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
    }
}
