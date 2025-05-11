package net.dark.spv_addon.blocks;

import net.dark.spv_addon.registry.SanityLightStore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class lightblocktest extends Block {
    public lightblocktest(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            SanityLightStore.addLight(pos);
        }
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (!world.isClient()) {
            SanityLightStore.removeLight(pos);
        }
        super.onBroken(world, pos, state);
    }
}
