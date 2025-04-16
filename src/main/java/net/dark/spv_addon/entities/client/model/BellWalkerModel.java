package net.dark.spv_addon.entities.client.model;

import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.cca.BellWalkerComponent;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class BellWalkerModel extends GeoModel<BellWalkerEntity> {
	private static final Identifier MODEL =
			new Identifier(Spv_addon.MOD_ID, "geo/entity/the_bell_walker.geo.json");
	private static final Identifier TEXTURE =
			new Identifier(Spv_addon.MOD_ID, "textures/entity/the_bell_walker.png");
	private static final Identifier ANIM =
			new Identifier(Spv_addon.MOD_ID, "animations/entity/bellwalker.animation.json");

	@Override
	public Identifier getModelResource(BellWalkerEntity object) {
		return MODEL;
	}

	@Override
	public Identifier getTextureResource(BellWalkerEntity object) {
		return TEXTURE;
	}

	@Override
	public Identifier getAnimationResource(BellWalkerEntity animatable) {
		return ANIM;
	}

	@Override
	public void setCustomAnimations(BellWalkerEntity animatable, long instanceId,
									AnimationState<BellWalkerEntity> state) {
		super.setCustomAnimations(animatable, instanceId, state);
		// Exemple : récupérer votre composant si vous voulez piloter des bones
		BellWalkerComponent comp = InitializeComponents.BELL_WALKER.get(animatable);
		// … ajoutez ici vos interactions d’animation personnalisées si nécessaire
	}
}