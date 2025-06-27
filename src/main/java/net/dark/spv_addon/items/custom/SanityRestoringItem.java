package net.dark.spv_addon.items.custom;

import net.dark.spv_addon.Additions.api.SanityLightStore;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SanityRestoringItem extends Item {
    private final int sanityChange;
    private final int thirstChange;
    private final boolean poisoned;
    private final boolean decreaseSanity;
    private final boolean decreaseThirst;

    /**
     * @param settings       Item settings
     * @param sanityChange   Amount of sanity to change (positive or negative)
     * @param thirstChange   Amount of thirst to change (positive or negative)
     * @param poisoned       Whether the item causes poison effect
     * @param decreaseSanity Whether the item decreases sanity
     * @param decreaseThirst Whether the item decreases thirst
     */

    public SanityRestoringItem(Settings settings, int sanityChange, int thirstChange, boolean poisoned, boolean decreaseSanity, boolean decreaseThirst) {
        super(settings);
        this.sanityChange = sanityChange;
        this.thirstChange = thirstChange;
        this.poisoned = poisoned;
        this.decreaseSanity = decreaseSanity;
        this.decreaseThirst = decreaseThirst;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        SanityComponent sanity = InitializeComponents.SANITY.get(player);

        if (!world.isClient) {
            if (thirstChange != 0) {
                int value = decreaseThirst ? -Math.abs(thirstChange) : Math.abs(thirstChange);
                ThirstManager.increaseThirst(player, value);
            }
            if (sanityChange != 0) {
                if (!SanityLightStore.isPlayerInLightRange(world, player)) {
                    int value = decreaseSanity ? -Math.abs(sanityChange) : Math.abs(sanityChange);
                    if (value > 0) {
                        sanity.increaseSanity(value);
                    } else {
                        sanity.decreaseSanity(-value);
                    }
                }
            }
            stack.decrement(1);

            if (poisoned) {
                player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                        net.minecraft.entity.effect.StatusEffects.POISON,
                        100,
                        0,
                        true,
                        false
                ));
            }
        }
        return TypedActionResult.success(stack, world.isClient());

    }
}
