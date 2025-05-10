package net.dark.spv_addon.init;

import net.dark.spv_addon.blocks.BedBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.block.Blocks;


public class ModBlocks {
    public static final Block HOTEL_WALL = new Block(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.STONE));
    public static final Block HOTEL_FLOOR = new Block(FabricBlockSettings.copyOf(Blocks.RED_WOOL).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.WOOL));

    public static final Block TABLE = new BedBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.WOOD).dropsNothing().solidBlock((state, world, pos) -> true).nonOpaque().suffocates((state, world, pos) -> true).blockVision((state, world, pos) -> true));
    public static final Block BED1 = new BedBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.WOOD).dropsNothing().solidBlock((state, world, pos) -> true).nonOpaque().suffocates((state, world, pos) -> true).blockVision((state, world, pos) -> true));
    public static final Block BED2 = new BedBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.WOOD).dropsNothing().solidBlock((state, world, pos) -> true).nonOpaque().suffocates((state, world, pos) -> true).blockVision((state, world, pos) -> true));

    public static void registerModBlocks() {
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "hotel_wall"), HOTEL_WALL);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "hotel_floor"), HOTEL_FLOOR);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "table"), TABLE);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "bed1"), BED1);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "bed2"), BED2);
    }
}
