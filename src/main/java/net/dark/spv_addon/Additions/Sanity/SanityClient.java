package net.dark.spv_addon.Additions.Sanity;

import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.client.gui.SanityHud;
import net.dark.spv_addon.entities.client.model.SaniModel;
import net.dark.spv_addon.entities.custom.SanityStalkerEntity;
import net.dark.spv_addon.init.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.PhantomEntityRenderer;
import net.minecraft.util.math.Box;

public class SanityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        // 1) HUD
        HudRenderCallback.EVENT.register(new SanityHud());

        // 3) spawn a single ghost when your sanity hits 0
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity p = client.player;
            if (p == null) return;
            SanityComponent sc = InitializeComponents.SANITY.get(p);
            if (sc.getSanity() == 0) {
                boolean hasGhost = !client.world
                        .getNonSpectatingEntities(SanityStalkerEntity.class,
                                p.getBoundingBox().expand(3))
                        .isEmpty();
                if (!hasGhost) {
                    SanityStalkerEntity ghost = new SanityStalkerEntity(ModEntities.SANI_TY, client.world);
                    ghost.updatePosition(p.getX() + 1.5, p.getY(), p.getZ() + 1.5);
                    client.world.addEntity(-1, ghost);
                }
            }
        });
    }
}
