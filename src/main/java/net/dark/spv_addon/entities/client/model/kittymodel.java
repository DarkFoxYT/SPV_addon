package net.dark.spv_addon.entities.client.model;

import com.sp.entity.ik.model.GeckoLib.GeoModelAccessor;
import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.entities.custom.KittyEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class kittymodel extends GeoModel<KittyEntity> {
	private static final Identifier MODEL = new Identifier(Spv_addon.MOD_ID,
			"geo/entity/kitty.geo.json");
	private static final Identifier TEXTURE = new Identifier(Spv_addon.MOD_ID,
			"textures/entity/bellwalker/kitty.png");
	private static final Identifier ANIMATION = new Identifier(Spv_addon.MOD_ID,
			"animation/kitty.animation.json");

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
	public void setCustomAnimations(KittyEntity animatable, long instanceId, AnimationState<KittyEntity> state) {
		super.setCustomAnimations(animatable, instanceId, state);
		animatable.tickComponentsClient(animatable, new GeoModelAccessor(this));
	}
}