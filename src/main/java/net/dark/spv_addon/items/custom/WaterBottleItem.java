package net.dark.spv_addon.items.custom;

import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

/**
 * Enhanced Water Bottle Item for the thirst system
 * Provides hydration and optional beneficial effects
 */
public class WaterBottleItem extends Item {
    private final int thirstRestoration;
    private final boolean hasSpecialEffects;
    
    public WaterBottleItem(Settings settings, int thirstRestoration, boolean hasSpecialEffects) {
        super(settings);
        this.thirstRestoration = thirstRestoration;
        this.hasSpecialEffects = hasSpecialEffects;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            // Check if player needs hydration
            if (canDrink(serverPlayer)) {
                user.setCurrentHand(hand);
                return TypedActionResult.consume(itemStack);
            } else {
                user.sendMessage(Text.literal("You're not thirsty right now").formatted(Formatting.GRAY), true);
                return TypedActionResult.fail(itemStack);
            }
        }
        
        return TypedActionResult.consume(itemStack);
    }
    
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            // Restore thirst
            ThirstManager.restoreThirst(serverPlayer, thirstRestoration, true);
            
            // Apply special effects if applicable
            if (hasSpecialEffects) {
                applySpecialEffects(serverPlayer);
            }
            
            // Play drinking sound
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_GENERIC_DRINK, 
                SoundCategory.PLAYERS, 1.0f, 1.0f);
            
            // Consume the item and return empty bottle
            if (!serverPlayer.getAbilities().creativeMode) {
                stack.decrement(1);
                
                // Give empty bottle back (glass bottle)
                ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
                if (!serverPlayer.getInventory().insertStack(emptyBottle)) {
                    serverPlayer.dropItem(emptyBottle, false);
                }
            }
        }
        
        return stack;
    }
    
    /**
     * Apply special effects for enhanced water types
     */
    private void applySpecialEffects(ServerPlayerEntity player) {
        // Different effects based on water type
        if (thirstRestoration >= 60) {
            // Energy drink effects
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1200, 0)); // 1 minute speed
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 1200, 0)); // 1 minute haste
            player.sendMessage(Text.literal("You feel energized!").formatted(Formatting.GREEN), true);
        } else if (thirstRestoration >= 40) {
            // Purified water effects
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0)); // 10 seconds regen
            player.removeStatusEffect(StatusEffects.POISON);
            player.removeStatusEffect(StatusEffects.NAUSEA);
            player.sendMessage(Text.literal("You feel purified").formatted(Formatting.AQUA), true);
        }
    }
    
    /**
     * Check if the player can drink (has less than 100% thirst)
     */
    private boolean canDrink(ServerPlayerEntity player) {
        try {
            var thirstOpt = net.dark.spv_addon.cca.InitializeComponents.THIRST.maybeGet(player);
            if (thirstOpt.isPresent()) {
                return thirstOpt.get().getThirst() < 100;
            }
        } catch (Exception e) {
            // If we can't check thirst, allow drinking
        }
        return true;
    }
    
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }
    
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32; // Same as vanilla potions
    }
    
    @Override
    public boolean hasGlint(ItemStack stack) {
        return hasSpecialEffects; // Special water types have enchantment glint
    }
}
