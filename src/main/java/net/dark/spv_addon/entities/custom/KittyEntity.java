package net.dark.spv_addon.entities.custom;

import com.sp.init.BackroomsLevels;
import com.sp.world.levels.BackroomsLevel;
import net.dark.spv_addon.init.ModBlocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class KittyEntity extends PathAwareEntity implements GeoAnimatable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public float headYaw = 0.0F;
    public float headPitch = 0.0F;

    public KittyEntity(EntityType<? extends KittyEntity> type, World world) {
        super(type, world);
        if (!world.isClient && world.getRegistryKey() == net.dark.spv_addon.init.BackroomsLevels.LEVEL_KITTY_WORLD_KEY) {
            this.refreshPositionAndAngles(15.0, 2.0, 15.0, this.getYaw(), this.getPitch());
        }
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 4000.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0);
    }

    public double getSize() {
        return 2;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new MeleeAttackGoal(this, 1.0, true));
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

            // Si aucun joueur ne voit Kitty, téléporte-le près du joueur le plus proche
            if (!isSeen && nearest != null) {
                double px = nearest.getX() + (this.random.nextDouble() - 0.5) * 2.5;
                double py = nearest.getY();
                double pz = nearest.getZ() + (this.random.nextDouble() - 0.5) * 2.5;
                this.requestTeleport(px, py, pz);
                this.setYaw(nearest.getYaw());
                this.setHeadYaw(nearest.getYaw());
                this.setBodyYaw(nearest.getYaw());
                // Réinitialise la tête pour qu'elle regarde le joueur après la rotation du corps
                this.headYaw = nearest.getYaw();
            }
        }

        // Toujours regarder le joueur le plus proche
        PlayerEntity closest = null;
        double minDist = Double.MAX_VALUE;
        for (PlayerEntity player : this.getWorld().getPlayers()) {
            double dist = this.squaredDistanceTo(player);
            if (dist < minDist) {
                closest = player;
                minDist = dist;
            }
        }
        if (closest != null) {
            double dx = closest.getX() - this.getX();
            double dy = closest.getEyeY() - this.getEyeY();
            double dz = closest.getZ() - this.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            float targetYaw = (float)(Math.toDegrees(Math.atan2(dx, dz)));
            float targetPitch = (float)-(Math.atan2(dy, dist) * (180F / Math.PI));
            // Si le corps a tourné, réinitialise la tête pour qu'elle regarde le joueur
            if (Math.abs(this.getYaw() - this.headYaw) > 1.0F) {
                this.headYaw = this.getYaw();
            }
            this.headYaw = targetYaw;
            this.headPitch = targetPitch;
        }
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; }
    @Override public double getTick(Object o) { return age; }
    @Override public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!player.getWorld().isClient) {
            if (player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
                // Vérifie si on est bien dans LevelKittyBackroomsLevel
                if (serverPlayer.getWorld().getRegistryKey() == net.dark.spv_addon.init.BackroomsLevels.LEVEL_KITTY_WORLD_KEY) {
                    // Récupère le niveau courant
                    var level = (net.dark.spv_addon.world.levels.custom.LevelKittyBackroomsLevel)
                            net.dark.spv_addon.init.BackroomsLevels.LEVEL_KITTY_BACKROOMS_LEVEL;
                    // Récupère le PlayerComponent (à adapter selon votre API)
                    var playerComponent = com.sp.cca_stuff.InitializeComponents.PLAYER.get(serverPlayer);
                    // Crée la transition
                    var teleport = new com.sp.world.levels.BackroomsLevel.CrossDimensionTeleport(
                            serverPlayer.getWorld(),
                            playerComponent,
                            level.getSpawnPos(),
                            net.dark.spv_addon.init.BackroomsLevels.LEVEL_KITTY_BACKROOMS_LEVEL,
                            com.sp.init.BackroomsLevels.LEVEL324_BACKROOMS_LEVEL
                    );
                    // Appelle la transition out
                    if (level.transitionOut(teleport)) {
                        // Effectue la téléportation
                        serverPlayer.teleport(
                                serverPlayer.getServer().getWorld(com.sp.init.BackroomsLevels.LEVEL324_WORLD_KEY),
                                serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                                serverPlayer.getYaw(), serverPlayer.getPitch()
                        );
                        return ActionResult.success(true);
                    }
                }
            }
        }
        return super.interactMob(player, hand);
    }
}
