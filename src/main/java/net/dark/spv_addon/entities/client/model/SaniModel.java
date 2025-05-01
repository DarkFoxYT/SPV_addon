package net.dark.spv_addon.entities.client.model;

import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.entities.custom.Sani_ty;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SaniModel extends GeoModel<Sani_ty> {
	private static final Identifier MODEL = new Identifier(Spv_addon.MOD_ID,
			"geo/entity/sanity.geo.json");
	private static final Identifier TEXTURE = new Identifier(Spv_addon.MOD_ID,
			"textures/entity/bellwalker/sani-ty.png");
	private static final Identifier ANIMATION = new Identifier(Spv_addon.MOD_ID,
			"animation/sani-ty.animation.json");

	@Override
	public Identifier getModelResource(Sani_ty object) {
		return MODEL;
	}

	@Override
	public Identifier getTextureResource(Sani_ty object) {
		return TEXTURE;
	}

	@Override
	public Identifier getAnimationResource(Sani_ty animatable) {
		return ANIMATION;
	}


}