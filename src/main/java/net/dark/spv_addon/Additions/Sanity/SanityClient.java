package net.dark.spv_addon.Additions.Sanity;

import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.client.gui.SanityHud;
import net.dark.spv_addon.entities.custom.Sani_ty;
import net.dark.spv_addon.init.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.network.ClientPlayerEntity;

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
                        .getNonSpectatingEntities(Sani_ty.class,
                                p.getBoundingBox().expand(3))
                        .isEmpty();
                if (!hasGhost) {
                    Sani_ty ghost = new Sani_ty(ModEntities.SANI_TY, client.world);
                    ghost.updatePosition(p.getX() + 1.5, p.getY(), p.getZ() + 1.5);
                    client.world.addEntity(-1, ghost);
                }
            }
        });
    }
}
