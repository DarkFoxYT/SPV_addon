package net.dark.spv_addon.entities.client.renderer;

import net.dark.spv_addon.entities.client.model.DeathCamModel;
import net.dark.spv_addon.entities.custom.DeathCamEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;        // <— for v4
import net.minecraft.client.render.entity.EntityRendererFactory.Context;

public class DeathCamRenderer extends GeoEntityRenderer<DeathCamEntity> {
    public DeathCamRenderer(Context ctx) {
        super(ctx, new DeathCamModel());
        this.shadowOpacity = 0F;
    }
}

