// SanityRestoringItem.java

package net.dark.spv_addon.items.custom;

import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.registry.SanityLightStore;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SanityRestoringItem extends Item {
    private final int sanityChange;
    private final int thirstChange;
    private final boolean poisoned;
    private final boolean decreaseSanity;
    private final boolean decreaseThirst;

    public SanityRestoringItem(Settings settings, int sanityChange, int thirstChange) {
        this(settings, sanityChange, thirstChange, false, false, false);
    }

    public SanityRestoringItem(Settings settings, int sanityChange, int thirstChange, boolean poisoned) {
        this(settings, sanityChange, thirstChange, poisoned, false, false);
    }

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
            // Gérer la soif
            if (thirstChange != 0) {
                int value = decreaseThirst ? -Math.abs(thirstChange) : Math.abs(thirstChange);
                ThirstManager.increaseThirst(player, value);
            }
            // Gérer la santé mentale
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

            // Effet poison custom
            if (poisoned) {
                player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                        net.minecraft.entity.effect.StatusEffects.POISON,
                        100, // durée en ticks (5 secondes)
                        0,   // niveau
                        true, // ambient (pas d'icône)
                        false // pas de particules
                ));
        }
        }
        return TypedActionResult.success(stack, world.isClient());
    }
}
