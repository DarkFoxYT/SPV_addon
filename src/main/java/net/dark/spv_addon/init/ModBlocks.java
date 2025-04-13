package net.dark.spv_addon.init;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;

public class ModBlocks {
    public static final Block HOTEL_WALL = new Block(
            AbstractBlock.Settings.copy(Blocks.STONE)
                    .strength(2.0f, 3.0f)
                    .sounds(BlockSoundGroup.STONE)
    );

    public static final Block HOTEL_FLOOR = new Block(
            AbstractBlock.Settings.copy(Blocks.SMOOTH_STONE)
                    .strength(1.5f, 3.0f)
                    .sounds(BlockSoundGroup.STONE)
    );

    public static void registerModBlocks() {
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "hotel_wall"), HOTEL_WALL);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "hotel_floor"), HOTEL_FLOOR);
    }
}
