package net.dark.spv_addon.entities.custom;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.init.BackroomsLevels;
import com.sp.world.levels.BackroomsLevel;
import net.dark.spv_addon.init.ModBlocks;
import net.dark.spv_addon.world.levels.custom.LevelKittyBackroomsLevel;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class KittyEntity extends PathAwareEntity implements GeoAnimatable {

    private static final int AGGRO_TICKS = 3600;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public float headYaw = 0.0F;
    public float headPitch = 0.0F;
    private int followTicks = 0;
    private boolean isAggressive = false;
    private PlayerEntity targetPlayer = null;

    public KittyEntity(EntityType<? extends KittyEntity> type, World world) {
        super(type, world);
        if (!world.isClient && world.getRegistryKey() == net.dark.spv_addon.init.BackroomsLevels.LEVEL_KITTY_WORLD_KEY) {
            this.refreshPositionAndAngles(15.0, 2.0, 15.0, this.getYaw(), this.getPitch());
        }
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40000.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1000.0);
    }

    public double getSize() {
        return 2.8;
    }

    @Override
    public boolean damage(net.minecraft.entity.damage.DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(net.minecraft.entity.damage.DamageSource damageSource) {
        return true;
    }


    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new MeleeAttackGoal(this, 1.0, true) {
            @Override
            public boolean canStart() {
                return isAggressive && targetPlayer != null && KittyEntity.this.squaredDistanceTo(targetPlayer) < 4.0D;
            }

            @Override
            public void start() {
                KittyEntity.this.setTarget(targetPlayer);
                super.start();
            }
        });
        this.goalSelector.add(1, new LookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            PlayerEntity closest = null;
            double minDist = Double.MAX_VALUE;

            for (PlayerEntity player : this.getWorld().getPlayers()) {
                double dist = this.squaredDistanceTo(player);
                if (dist < minDist) {
                    closest = player;
                    minDist = dist;
                }
            }

            if (closest != null && minDist < 256) {
                if (targetPlayer == closest) {
                    followTicks++;
                } else {
                    targetPlayer = closest;
                    followTicks = 0;
                    isAggressive = false;
                }

                if (followTicks > AGGRO_TICKS) {
                    isAggressive = true;
                }

                // Rotate body toward player
                this.lookAtEntity(closest, 30.0f, 30.0f);

                // Calculate yaw and pitch for head rotation
                double dx = closest.getX() - this.getX();
                double dz = closest.getZ() - this.getZ();
                double dy = closest.getEyeY() - this.getEyeY();
                double distXZ = Math.sqrt(dx * dx + dz * dz);

                float targetHeadYaw = (float) Math.toDegrees(Math.atan2(dx, dz)) - this.bodyYaw;
                float targetHeadPitch = (float) Math.toDegrees(Math.atan2(dy, distXZ));

                // Clamp angles to prevent spinning
                targetHeadYaw = MathHelper.clamp(targetHeadYaw, -45f, 45f);
                targetHeadPitch = MathHelper.clamp(targetHeadPitch, -30f, 30f);

                // Smooth interpolation
                this.headYaw += (targetHeadYaw - this.headYaw) * 0.1f;
                this.headPitch += (targetHeadPitch - this.headPitch) * 0.1f;
            } else {
                followTicks = 0;
                isAggressive = false;
                targetPlayer = null;

                this.headYaw *= 0.8f; // Gradually center when idle
                this.headPitch *= 0.8f;
            }
        }
    }



    @Override
    public float getHeadYaw() {
        return this.headYaw;
    }

    @Override
    public float getPitch(float tickDelta) {
        return this.headPitch;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object o) {
        return age;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        if (!stack.isOf(ModBlocks.KITTY_PLUSHIE1.asItem())) {
            return super.interactMob(player, hand);
        }

        if (!player.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
            if (serverPlayer.getWorld().getRegistryKey() == net.dark.spv_addon.init.BackroomsLevels.LEVEL_KITTY_WORLD_KEY) {
                var level = (LevelKittyBackroomsLevel) net.dark.spv_addon.init.BackroomsLevels.LEVEL_KITTY_BACKROOMS_LEVEL;
                var pc = InitializeComponents.PLAYER.get(serverPlayer);
                var tp = new BackroomsLevel.CrossDimensionTeleport(
                        serverPlayer.getWorld(),
                        pc,
                        level.getSpawnPos(),
                        net.dark.spv_addon.init.BackroomsLevels.LEVEL_KITTY_BACKROOMS_LEVEL,
                        BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL
                );

                if (level.transitionOut(tp)) {
                    serverPlayer.teleport(
                            serverPlayer.getServer().getWorld(BackroomsLevels.POOLROOMS_WORLD_KEY),
                            15, 90, 15,
                            serverPlayer.getYaw(), -90
                    );
                    return ActionResult.success(true);
                }
            }
        }

        return super.interactMob(player, hand);
    }
}