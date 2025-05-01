package net.dark.spv_addon;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.WorldEvents;
import com.sp.compat.modmenu.ConfigDefinitions;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.definition.ShaderPreDefinitions;
import foundry.veil.platform.VeilEventPlatform;
import net.dark.spv_addon.Additions.Sanity.SanityClient;
import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.client.ClientFlashlightRendererAddon;
import net.dark.spv_addon.client.gui.BatteryHud;
import net.dark.spv_addon.client.gui.ThirstHud;
import net.dark.spv_addon.entities.client.renderer.BellWalkerRenderer;
import net.dark.spv_addon.entities.client.renderer.KittyRenderer;
import net.dark.spv_addon.entities.client.renderer.SaniRenderer;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.entities.custom.KittyEntity;
import net.dark.spv_addon.entities.custom.Sani_ty;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.init.ModEntities;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

import static net.dark.spv_addon.compat.modmenu.ConfigDefinitions.definitions;


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

        FabricDefaultAttributeRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerRenderer::new);

        FabricDefaultAttributeRegistry.register(ModEntities.SANI_TY, Sani_ty.createAttributes());
        EntityRendererRegistry.register(ModEntities.SANI_TY, SaniRenderer::new);

        FabricDefaultAttributeRegistry.register(ModEntities.KITTY, KittyEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.KITTY, KittyRenderer::new);


        new SanityClient().onInitializeClient(); // or just rely on Fabric’s automatic discovery
        BatteryHud.register();
        ThirstHud.register();

        VeilEventPlatform.INSTANCE.preVeilPostProcessing((name, pipeline, context) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;
            ShaderPreDefinitions defs = VeilRenderSystem.renderer().getShaderDefinitions();



            // 2) Apply all of your config flags:
            ConfigDefinitions.definitions.forEach((key, supplier) -> {
                if (supplier.get()) {
                    defs.define(key);
                } else {
                    defs.remove(key);
                }
            });

            // 3) Apply all of your per‐world flags:
            BackroomsLevels.definitions.forEach((key, worldKey) -> {
                if (client.world != null && client.world.getRegistryKey() == worldKey) {
                    defs.define(key);
                } else {
                    defs.remove(key);
                }
            });
        });
    }
}
