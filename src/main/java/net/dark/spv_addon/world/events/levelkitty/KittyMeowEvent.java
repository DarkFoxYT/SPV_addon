package net.dark.spv_addon.world.events.levelkitty;

import com.sp.world.events.AbstractEvent;
import net.dark.spv_addon.init.ModItems;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class KittyMeowEvent extends AbstractEvent {
    public void trigger(World world, ServerPlayerEntity player) {
        Random rand = new Random();

        int dropCount = 1 + rand.nextInt(3);
        for (int i = 0; i < dropCount; i++) {
            int dx = rand.nextInt(11) + 10;
            int dz = rand.nextInt(11) + 10;
            if (rand.nextBoolean()) dx = -dx;
            if (rand.nextBoolean()) dz = -dz;
            BlockPos dropPos = new BlockPos(
                    player.getBlockPos().getX() + dx,
                    2,
                    player.getBlockPos().getZ() + dz
            );
            world.spawnEntity(new net.minecraft.entity.ItemEntity(
                    world,
                    dropPos.getX() + 0.5,
                    dropPos.getY() + 0.5,
                    dropPos.getZ() + 0.5,
                    new net.minecraft.item.ItemStack(ModItems.KITTY_PLUSH1)
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