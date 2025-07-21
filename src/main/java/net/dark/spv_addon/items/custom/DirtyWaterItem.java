package net.dark.spv_addon.items.custom;

import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.dark.spv_addon.cca.InitializeComponents;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/**
 * Dirty Water Item - Risky hydration with potential negative effects
 * Provides some thirst relief but may cause harmful effects
 */
public class DirtyWaterItem extends Item {
    private final int thirstRestoration;
    private final boolean hasNegativeEffects;
    private final Random random = Random.create();
    
    public DirtyWaterItem(Settings settings, int thirstRestoration, boolean hasNegativeEffects) {
        super(settings);
        this.thirstRestoration = thirstRestoration;
        this.hasNegativeEffects = hasNegativeEffects;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            // Always allow drinking dirty water (desperation)
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
        
        return TypedActionResult.consume(itemStack);
    }
    
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient && user instanceof ServerPlayerEntity serverPlayer) {
            // Restore some thirst (less than clean water)
            ThirstManager.restoreThirst(serverPlayer, thirstRestoration, true);
            
            // Apply negative effects if applicable
            if (hasNegativeEffects) {
                applyNegativeEffects(serverPlayer);
            }
            
            // Play drinking sound (different from clean water)
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_GENERIC_DRINK, 
                SoundCategory.PLAYERS, 0.8f, 0.7f); // Lower pitch for dirty water
            
            // Consume the item and return empty bottle
            if (!serverPlayer.getAbilities().creativeMode) {
                stack.decrement(1);
                
                // Give empty bottle back
                ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
                if (!serverPlayer.getInventory().insertStack(emptyBottle)) {
                    serverPlayer.dropItem(emptyBottle, false);
                }
            }
        }
        
        return stack;
    }
    
    /**
     * Apply negative effects from drinking dirty water
     */
    private void applyNegativeEffects(ServerPlayerEntity player) {
        // Various negative effects with different probabilities
        
        // 60% chance of nausea
        if (random.nextFloat() < 0.6f) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 400, 0)); // 20 seconds
            player.sendMessage(Text.literal("The water tastes awful...").formatted(Formatting.YELLOW), true);
        }
        
        // 40% chance of poison
        if (random.nextFloat() < 0.4f) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 300, 0)); // 15 seconds
            player.sendMessage(Text.literal("You feel sick from the dirty water").formatted(Formatting.RED), true);
        }
        
        // 30% chance of weakness
        if (random.nextFloat() < 0.3f) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 600, 0)); // 30 seconds
            player.sendMessage(Text.literal("The contaminated water weakens you").formatted(Formatting.GRAY), true);
        }
        
        // 20% chance of hunger (dehydration paradox)
        if (random.nextFloat() < 0.2f) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 400, 0)); // 20 seconds
            player.sendMessage(Text.literal("The dirty water makes you feel worse").formatted(Formatting.DARK_RED), true);
        }
        
        // 15% chance of sanity loss
        if (random.nextFloat() < 0.15f) {
            try {
                var sanityComp = InitializeComponents.SANITY.get(player);
                sanityComp.decreaseSanity(5 + random.nextInt(10)); // 5-15 sanity loss
                player.sendMessage(Text.literal("The foul water disturbs your mind").formatted(Formatting.DARK_PURPLE), true);
            } catch (Exception e) {
                // Silently handle sanity errors
            }
        }
        
        if (random.nextFloat() < 0.1f) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 10, 0));
            player.sendMessage(Text.literal("The contaminated water severely affects your body!").formatted(Formatting.DARK_RED), true);
        }

        if (random.nextFloat() < 0.05f) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 200, 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 300, 0));
            player.sendMessage(Text.literal("The toxic water overwhelms your senses!").formatted(Formatting.BLACK), true);

            try {
                var sanityComp = InitializeComponents.SANITY.get(player);
                sanityComp.decreaseSanity(15 + random.nextInt(15));
            } catch (Exception e) {
            }
        }

        if (random.nextFloat() < 0.1f) {
            String[] warnings = {
                "This water doesn't taste right...",
                "You probably shouldn't have drunk that",
                "The water has a strange aftertaste",
                "You feel uneasy after drinking that",
                "That water was definitely contaminated"
            };
            String warning = warnings[random.nextInt(warnings.length)];
            player.sendMessage(Text.literal(warning).formatted(Formatting.YELLOW), true);
        }
    }
    
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }
    
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 40;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }
}
