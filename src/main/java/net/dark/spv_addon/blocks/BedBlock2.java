// src/main/java/net/dark/spv_addon/blocks/BedBlock2.java
package net.dark.spv_addon.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.ShapeContext;

public class BedBlock2 extends Block {
    private static final VoxelShape SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(9/16.0,   -2/16.0,  -7/16.0, 11/16.0,   7/16.0,  -5/16.0),
            VoxelShapes.cuboid(21.3/16.0, -2.3/16.0, 2/16.0, 23.3/16.0, 6.7/16.0, 4/16.0),
            VoxelShapes.cuboid(-7.1/16.0, -2/16.0, 16/16.0, -5.1/16.0,  7/16.0, 18/16.0),
            VoxelShapes.cuboid(-2.1/16.0, -2/16.0, 22.9/16.0, -0.1/16.0, 7/16.0, 24.9/16.0),
            VoxelShapes.cuboid(-16/16.0,   6/16.0,  -7/16.0,  0/16.0,  10/16.0, 25/16.0),
            VoxelShapes.cuboid(-1.1/16.0, 6.6/16.0, -5.7/16.0, 15.9/16.0, 10.6/16.0, 20.4/16.0),
            VoxelShapes.cuboid(-11.7/16.0, 8.3/16.0, 7.9/16.0, 1.7/16.0, 11.6/16.0, 15.4/16.0)
    );

    public BedBlock2(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}