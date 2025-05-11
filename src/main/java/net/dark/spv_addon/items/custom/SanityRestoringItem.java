// SanityRestoringItem.java

package net.dark.spv_addon.items.custom;

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
    private final int restoreAmount;


    public SanityRestoringItem(Settings settings, int restoreAmount) {
        super(settings);
        this.restoreAmount = restoreAmount;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        SanityComponent sanity = InitializeComponents.SANITY.get(player);

        if (!world.isClient) {
            if (!SanityLightStore.isPlayerInLightRange(world, player)) {
                sanity.increaseSanity(10); // adjust value
                stack.decrement(1);
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }
}
