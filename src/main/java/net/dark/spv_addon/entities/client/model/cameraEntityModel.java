package net.dark.spv_addon.entities.client.model;

import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.entities.custom.KittyEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class cameraEntityModel extends GeoModel<KittyEntity> {
    private static final Identifier MODEL = new Identifier(Spv_addon.MOD_ID,
            "geo/entity/cam.geo.json");
    private static final Identifier TEXTURE = new Identifier(Spv_addon.MOD_ID,
            "textures/entity/cam/cam.png");
    private static final Identifier ANIMATION = new Identifier(Spv_addon.MOD_ID,
            "animation/cam.animation.json");

    @Override
    public Identifier getModelResource(KittyEntity object) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(KittyEntity object) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(KittyEntity animatable) {
        return ANIMATION;
    }

    @Override
    public void setCustomAnimations(KittyEntity entity, long uniqueID, AnimationState<KittyEntity> animationState) {
        super.setCustomAnimations(entity, uniqueID, animationState);
        CoreGeoBone head = getAnimationProcessor().getBone("head");
        if (head != null) {
            head.setRotY(entity.headYaw * ((float) Math.PI / 180F));
            head.setRotX(entity.headPitch * ((float) Math.PI / 180F));
        }
    }
}