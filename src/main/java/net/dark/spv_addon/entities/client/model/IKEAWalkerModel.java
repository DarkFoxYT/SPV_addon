package net.dark.spv_addon.entities.client.model;

import com.sp.entity.ik.model.GeckoLib.GeoModelAccessor;
import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.entities.custom.IkeaWalkerEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class IKEAWalkerModel extends GeoModel<IkeaWalkerEntity> {
	private static final Identifier MODEL = new Identifier(Spv_addon.MOD_ID,
			"geo/entity/ikea_employe.geo.json");
	private static final Identifier TEXTURE = new Identifier(Spv_addon.MOD_ID,
			"textures/entity/ikeawalker/ikea_employe.png");
	private static final Identifier ANIMATION = new Identifier(Spv_addon.MOD_ID,
			"animation/ikea_employe.animation.json");

	@Override
	public Identifier getModelResource(IkeaWalkerEntity object) {
		return MODEL;
	}

	@Override
	public Identifier getTextureResource(IkeaWalkerEntity object) {
		return TEXTURE;
	}

	@Override
	public Identifier getAnimationResource(IkeaWalkerEntity animatable) {
		return ANIMATION;
	}

	@Override
	public void setCustomAnimations(IkeaWalkerEntity animatable, long instanceId, AnimationState<IkeaWalkerEntity> state) {
		super.setCustomAnimations(animatable, instanceId, state);
		animatable.tickComponentsClient(animatable, new GeoModelAccessor(this));
	}
}
