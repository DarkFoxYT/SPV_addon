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

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public float headYaw = 0.0F;
    public float headPitch = 0.0F;

    private int followTicks = 0;
    private boolean isAggressive = false;
    private PlayerEntity targetPlayer = null;
    private static final int AGGRO_TICKS = 3600;

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
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0);
    }

    public double getSize() {
        return 2.8;
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
            double range = 64.0;
            PlayerEntity nearest = null;
            double nearestDist = Double.MAX_VALUE;
            boolean isSeen = false;

            for (PlayerEntity player : this.getWorld().getPlayers()) {
                double dist = this.squaredDistanceTo(player);
                if (dist <= range * range) {
                    if (player.canSee(this)) {
                        isSeen = true;
                        break;
                    }
                    if (dist < nearestDist) {
                        nearest = player;
                        nearestDist = dist;
                    }
                }
            }

            if (!isSeen && nearest != null) {
                double px = nearest.getX() + (this.random.nextDouble() - 0.5) * 2.5;
                double py = nearest.getY();
                double pz = nearest.getZ() + (this.random.nextDouble() - 0.5) * 2.5;
                this.requestTeleport(px, py, pz);
                this.setYaw(nearest.getYaw());
                this.setHeadYaw(nearest.getYaw());
                this.setBodyYaw(nearest.getYaw());
                this.headYaw = nearest.getYaw();
            }
        }

        PlayerEntity closest = null;
        double minDist = Double.MAX_VALUE;
        for (PlayerEntity player : this.getWorld().getPlayers()) {
            double dist = this.squaredDistanceTo(player);
            if (dist < minDist) {
                closest = player;
                minDist = dist;
            }
        }

        if (closest != null && minDist < 16 * 16) {
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
        } else {
            followTicks = 0;
            isAggressive = false;
            targetPlayer = null;
        }

        if (closest != null) {
            double dx = closest.getX() - this.getX();
            double dy = closest.getEyeY() - this.getEyeY();
            double dz = closest.getZ() - this.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            float targetYaw = (float)(Math.toDegrees(Math.atan2(dx, dz)));
            float targetPitch = (float)(Math.atan2(dy, dist) * (180F / Math.PI));
            this.headYaw += MathHelper.wrapDegrees(targetYaw - this.headYaw) * 0.2F;
            this.headPitch += (targetPitch - this.headPitch) * 0.2F;
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

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override public double getTick(Object o) { return age; }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!player.getWorld().isClient && stack.isOf(ModBlocks.KITTY_PLUSHIE1.asItem())) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.teleport(serverPlayer.getServer().getWorld(BackroomsLevels.POOLROOMS_WORLD_KEY),
                        15, 90, 15,
                        serverPlayer.getYaw(), -90);
                return ActionResult.success(true);
            }
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
                    serverPlayer.teleport(serverPlayer.getServer().getWorld(BackroomsLevels.POOLROOMS_WORLD_KEY),
                            15, 90, 15,
                            serverPlayer.getYaw(), -90);
                    return ActionResult.success(true);
                }
            }
        }

        return super.interactMob(player, hand);
    }
}