package net.dark.spv_addon.entities.client.model;

import com.sp.entity.ik.model.GeckoLib.GeoModelAccessor;
import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.cca.BellWalkerComponent;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.Animation.LoopType;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.model.GeoModel;

public class BellWalkerModel extends GeoModel<BellWalkerEntity> {
	private static final Identifier MODEL = new Identifier(Spv_addon.MOD_ID,
			"geo/entity/the_bell_walker.geo.json");
	private static final Identifier TEXTURE = new Identifier(Spv_addon.MOD_ID,
			"textures/entity/bellwalker/the_bell_walker.png");
	private static final Identifier ANIMATION = new Identifier(Spv_addon.MOD_ID,
			"animation/bell_walker.animation.json");

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
		return ANIMATION;
	}

	@Override
	public void setCustomAnimations(BellWalkerEntity animatable, long instanceId, AnimationState<BellWalkerEntity> state) {
		super.setCustomAnimations(animatable, instanceId, state);
		// 1) Tick your IK component
		animatable.tickComponentsClient(animatable, new GeoModelAccessor(this));
		// 2) (Optional) access your CCA component
		BellWalkerComponent comp = InitializeComponents.BELL_WALKER.get(animatable);
		// No manual setAnimation call here—your JSON’s animation_controllers will loop "idle".
	}
}