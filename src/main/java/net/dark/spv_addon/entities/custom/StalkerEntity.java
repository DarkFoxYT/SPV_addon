package net.dark.spv_addon.entities.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.dark.spv_addon.entities.ai.procedural.PredatorBrain;
import net.dark.spv_addon.entities.ai.procedural.PredatorBrainConfig;

public class StalkerEntity extends PathAwareEntity {
    private static final TagKey<Block> IGNORED_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("spv_addon", "stalker_passable"));
    private final PredatorBrain predatorBrain;
    private int disengageCooldown = 0;

    public StalkerEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.setStepHeight(1.1f);
        this.predatorBrain = new PredatorBrain(
                this,
                new PredatorBrainConfig(
                        36.0,
                        0.06f,
                        6,
                        120,
                        0.42,
                        0.66,
                        1.08,
                        14
                )
        );
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40000.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.8)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1000.0);
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
        this.goalSelector.add(0, new MeleeAttackGoal(this, 1.15F, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            predatorBrain.tick();
            if (disengageCooldown > 0) {
                disengageCooldown--;
            }

            // Environmental awareness: stalker calms down slightly in bright areas.
            int skyLight = this.getWorld().getLightLevel(this.getBlockPos());
            if (skyLight >= 12 && this.getTarget() != null && disengageCooldown <= 0) {
                if (this.random.nextFloat() < 0.15f) {
                    this.setTarget(null);
                    disengageCooldown = 40;
                }
            }
        }
    }

    public boolean collides() {
        BlockState state = getWorld().getBlockState(this.getBlockPos());
        return !state.isIn(IGNORED_BLOCKS);
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (target instanceof PlayerEntity) {
            return super.tryAttack(target);
        }
        return false;
    }
}
