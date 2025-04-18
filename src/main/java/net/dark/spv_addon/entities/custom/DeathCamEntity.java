package net.dark.spv_addon.entities.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.Animation.LoopType;
import net.minecraft.util.Identifier;


/*
Player dies → Mixin cancels DeathScreen and calls startDeathCutscene().

CutsceneManager spawns a DeathCamEntity at the player’s eye.

GeckoLib kicks off the "fall" animation on cameraBone, moving it down 2blocks over 2s.

As cameraBone moves, the renderer’s GeoEntityRenderer automatically updates your DeathCamEntity model
but since the camera is attached to the entity’s position, your camera visually follows that bone.

When "fall" finishes, the walker’s "eat" animation (on its jawBone) plays, optionally with a bite


 */


public class DeathCamEntity extends Entity implements GeoAnimatable<DeathCamEntity> {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final RawAnimation FALL = RawAnimation.begin().then("fall", LoopType.PLAY_ONCE);
    public static final RawAnimation EAT  = RawAnimation.begin().then("eat",  LoopType.PLAY_ONCE);

    public DeathCamEntity(EntityType<? extends DeathCamEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            // automatic: play “fall” first, then “eat”
            if (!state.hasControllerFinishedLoading()) return PlayState.CONTINUE;
            if (!state.getCurrentAnimation().equals(FALL)) {
                return state.setAndContinue(FALL);
            } else {
                return state.setAndContinue(EAT);
            }
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object o) {
        return 0;
    }

    @Override
    public void tick() {
        super.tick();
        // nothing else here
    }

    @Override
    protected void initDataTracker() {}
    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {}
    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {}
}
