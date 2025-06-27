package net.dark.spv_addon.entities.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class StalkerEntity extends PathAwareEntity {
    private static final TagKey<Block> IGNORED_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("spv_addon", "stalker_passable"));

    public StalkerEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.setStepHeight(1.1f);
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
        this.goalSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0F, true));
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
