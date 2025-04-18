package net.dark.spv_addon.entities.client.model;

import net.dark.spv_addon.Spv_addon;
import net.dark_spv_addon.entities.custom.DeathCamEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class DeathCamModel extends GeoModel<DeathCamEntity> {
    private static final Identifier MODEL   = new Identifier(Spv_addon.MOD_ID, "geo/entity/death_cam.geo.json");
    private static final Identifier TEXTURE = new Identifier(Spv_addon.MOD_ID, "textures/entity/death_cam.png");
    private static final Identifier ANIM    = new Identifier(Spv_addon.MOD_ID, "animation/death_cam.animation.json");

    @Override
    public Identifier getModelResource(DeathCamEntity object) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(DeathCamEntity object) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(DeathCamEntity animatable) {
        return ANIM;
    }
}
