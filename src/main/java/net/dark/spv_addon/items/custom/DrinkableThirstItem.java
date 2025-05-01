package net.dark.spv_addon.items.custom;

import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.cca.ThirstComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class DrinkableThirstItem extends PotionItem {
    public DrinkableThirstItem(Settings settings) {
        super(settings);
    }

    // Called when you right-click and hold
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    // How long it takes to drink (vanilla potions are 32 ticks)
    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 64; // twice as long as a normal potion
    }

    // Drink animation
    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    // When finished drinking:
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity entity) {
        // 1) server‐side: add thirst
        if (!world.isClient && entity instanceof ServerPlayerEntity serverPlayer) {
            ThirstComponent comp = InitializeComponents.THIRST.get(serverPlayer);
            // random between 10 and 25 inclusive
            int gain = world.getRandom().nextBetween(10, 25);
            comp.addThirst(gain);
            SanityComponent sc = InitializeComponents.SANITY.get(serverPlayer);
            int bonus = world.getRandom().nextBetween(5, 25);
            sc.addSanity(bonus);
        }

        // 2) let PotionItem do its normal thing (decrement stack, etc)
        ItemStack result = super.finishUsing(stack, world, entity);

        // 3) give back a glass bottle if not in creative
        if (entity instanceof PlayerEntity player && !player.getAbilities().creativeMode) {
            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
            if (result.isEmpty()) {
                // if this was the last bottle, return the empty bottle directly
                return bottle;
            }
            // otherwise try to add to inventory or drop if full
            if (!player.getInventory().insertStack(bottle)) {
                player.dropItem(bottle, false);
            }
        }

        return result;
    }
}
