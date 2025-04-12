// File: net/dark/spv_addon/items/custom/BatteryItem.java

package net.dark.spv_addon.items.custom;

import net.dark.spv_addon.battery.BatteryManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BatteryItem2 extends Item {

    public BatteryItem2(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            int current = BatteryManager.getBattery(user.getUuid());
            if (current < 100) {
                int added = Math.min(100, 100 - current);
                BatteryManager.setBattery(user.getUuid(), current + added);
                user.getStackInHand(hand).decrement(1);
                user.sendMessage(Text.literal("Battery charged by " + added + "%. Now at " + (current + added) + "%"), false);
                return TypedActionResult.success(user.getStackInHand(hand));
            } else {
                user.sendMessage(Text.literal("Battery is already full."), false);
                return TypedActionResult.fail(user.getStackInHand(hand));
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
