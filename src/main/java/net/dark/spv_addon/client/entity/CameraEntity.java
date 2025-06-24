package net.dark.spv_addon.client.entity;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.model.DefaultedGeoModel;

import java.util.Optional;

public class CameraEntity extends ArmorStandEntity implements GeoEntity {
    private static final DefaultedGeoModel<CameraEntity> MODEL = new CameraEntityModel();
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public CameraEntity(ClientWorld world) {
        super(EntityType.ARMOR_STAND, world);
        this.setInvisible(true);
        this.noClip = true;
    }

    public DefaultedGeoModel<CameraEntity> getGeoModel() {
        return MODEL;
    }

    public Vec3d getCameraBonePosition() {
        Optional<GeoBone> bone = this.getGeoModel().getBone("camera_bone");
        if (bone.isEmpty()) return this.getPos();
        GeoBone geoBone = bone.get();
        return new Vec3d(geoBone.getWorldPosition().x, geoBone.getWorldPosition().y, geoBone.getWorldPosition().z);
    }

    public Vec3d getCameraBoneRotation() {
        Optional<GeoBone> bone = this.getGeoModel().getBone("camera_bone");
        if (bone.isEmpty()) return Vec3d.ZERO;
        GeoBone geoBone = bone.get();
        return new Vec3d(geoBone.getWorldPosition().x, geoBone.getWorldPosition().y, geoBone.getWorldPosition().z);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        // Ajoute tes animations ici
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // Classe modèle interne
    public static class CameraEntityModel extends DefaultedGeoModel<CameraEntity> {
        public CameraEntityModel() {
            super("spv_addon", "camera_model");
        }
    }
}