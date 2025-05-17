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
    private final int restoreAmount;
    private final int thirstAmount;


    public SanityRestoringItem(Settings settings, int restoreAmount, int thirstAmount) {
        super(settings);
        this.restoreAmount = restoreAmount;
        this.thirstAmount = thirstAmount;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        SanityComponent sanity = InitializeComponents.SANITY.get(player);

        if (!world.isClient) {
            ThirstManager.increaseThirst(player, 15); // Restore 15
            player.getStackInHand(hand).decrement(1);
            if (!SanityLightStore.isPlayerInLightRange(world, player)) {
                sanity.increaseSanity(10); // adjust value
                stack.decrement(1);
            }
        }
        if (!world.isClient) {

        }
        return TypedActionResult.success(stack, world.isClient());
    }
}
