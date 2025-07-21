package net.dark.spv_addon.world.events.levelkitty;

import com.sp.world.events.AbstractEvent;
import net.dark.spv_addon.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class KittyMeowEvent extends AbstractEvent {

    // Array of items that can be dropped in Level Kitty
    private static final Item[] KITTY_DROPS = {
        ModItems.KITTY_PLUSH1,      // Original kitty plushie
        ModItems.WATER_BOTTLE,      // Hydration item
        ModItems.CANTEEN,           // Sanity restoring item
        ModItems.ALMOND_BOTTLE,     // Almond water
        ModItems.BATTERY_ITEM,      // Battery for flashlight
        ModItems.PURIFIED_WATER,    // Better hydration
        ModItems.ENERGY_DRINK       // High-tier hydration
    };

    public void trigger(World world, ServerPlayerEntity player) {
        Random rand = new Random();

        int dropCount = 1 + rand.nextInt(2); // 1-2 items instead of 1-3
        for (int i = 0; i < dropCount; i++) {
            // Random position around player (closer range)
            int dx = rand.nextInt(7) + 3; // 3-9 blocks away
            int dz = rand.nextInt(7) + 3;
            if (rand.nextBoolean()) dx = -dx;
            if (rand.nextBoolean()) dz = -dz;

            BlockPos dropPos = new BlockPos(
                    player.getBlockPos().getX() + dx,
                    2,
                    player.getBlockPos().getZ() + dz
            );

            // Select random item from the drops array
            Item selectedItem = KITTY_DROPS[rand.nextInt(KITTY_DROPS.length)];

            // Create item stack with random count for some items
            ItemStack itemStack;
            if (selectedItem == ModItems.WATER_BOTTLE || selectedItem == ModItems.PURIFIED_WATER) {
                itemStack = new ItemStack(selectedItem, 1 + rand.nextInt(3)); // 1-3 bottles
            } else if (selectedItem == ModItems.BATTERY_ITEM) {
                itemStack = new ItemStack(selectedItem, 1); // Always 1 battery
            } else {
                itemStack = new ItemStack(selectedItem, 1); // 1 of other items
            }

            world.spawnEntity(new net.minecraft.entity.ItemEntity(
                    world,
                    dropPos.getX() + 0.5,
                    dropPos.getY() + 0.5,
                    dropPos.getZ() + 0.5,
                    itemStack
            ));
        }
    }

    @Override
    public void init(World world) {

    }

    @Override
    public int duration() {
        return 30;
    }
}