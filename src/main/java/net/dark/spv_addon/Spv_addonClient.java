package net.dark.spv_addon;

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
import net.dark.spv_addon.init.ModEntities;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class Spv_addonClient implements ClientModInitializer {
    private final ClientFlashlightRendererAddon flashlightRenderer = new ClientFlashlightRendererAddon();

    @Override
    public void onInitializeClient() {
        FocusHandler.register();



        // Les autres inits de ton mod
        ClientTickEvents.END_CLIENT_TICK.register(client -> flashlightRenderer.tick(client.getTickDelta()));


        BatteryHud.register();
        ThirstHud.register();
        SanityBar.register();

        ThirstManager.register();
        SanityLightDebugRenderer.init();

        FabricDefaultAttributeRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.SIX_LEG_ENTITY, BellWalkerRenderer::new);

        FabricDefaultAttributeRegistry.register(ModEntities.KITTY, KittyEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.KITTY, KittyRenderer::new);

        FabricDefaultAttributeRegistry.register(ModEntities.IKEA_WALKER, IkeaWalkerEntity.createAttributes());
        EntityRendererRegistry.register(ModEntities.IKEA_WALKER, IKEAWalkerRenderer::new);



        KeyBinding PRONE_KEY = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.spv_addon.prone", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_C, "category.spv_addon")
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (PRONE_KEY.wasPressed()) {
                MinecraftClient.getInstance().player.networkHandler.sendPacket(
                        new CustomPayloadC2SPacket(new Identifier("spv_addon", "prone_toggle"), PacketByteBufs.create())
                );
            }
        });



    }
}
