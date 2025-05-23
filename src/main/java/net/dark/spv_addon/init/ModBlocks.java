package net.dark.spv_addon.init;

import com.sp.block.SprintBlockSoundGroup;
import com.sp.block.custom.CeilingLight;
import com.sp.block.custom.FluorescentLightBlock;
import com.sp.init.BackroomsLevels;
import net.dark.spv_addon.blocks.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import com.sp.block.custom.CarpetBlock;
import com.sp.block.custom.WallBlock;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.block.Blocks;

import static software.bernie.example.registry.BlockRegistry.registerBlock;


public class ModBlocks {
    public static final Block HOTEL_WALL = new WallBlock(FabricBlockSettings.copyOf(com.sp.init.ModBlocks.WallBlock).hardness(-1f).noBlockBreakParticles().sounds(SprintBlockSoundGroup.WALL));
    public static final Block KITTY_WALL = new WallBlock(FabricBlockSettings.copyOf(com.sp.init.ModBlocks.WallBlock).hardness(-1f).noBlockBreakParticles().sounds(SprintBlockSoundGroup.WALL));
    public static final Block KITTY_FLOOR = new CarpetBlock(FabricBlockSettings.copyOf(com.sp.init.ModBlocks.CarpetBlock).hardness(-1f).noBlockBreakParticles().sounds(SprintBlockSoundGroup.CARPET));
    public static final Block HOTEL_FLOOR = new CarpetBlock(FabricBlockSettings.copyOf(com.sp.init.ModBlocks.CarpetBlock).hardness(-1f).noBlockBreakParticles().sounds(SprintBlockSoundGroup.CARPET));
    public static final Block TESTS = new lightblocktest(FabricBlockSettings.copyOf(com.sp.init.ModBlocks.CarpetBlock).hardness(-1f).noBlockBreakParticles().sounds(SprintBlockSoundGroup.CARPET));
    public static final Block IKEA_SHELF = new TableBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.COPPER).dropsNothing().nonOpaque());
    public static final Block IKEA_SHELF1 = new TableBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.COPPER).dropsNothing().nonOpaque());
    public static final Block IKEA_SHELF2 = new TableBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.COPPER).dropsNothing().nonOpaque());
    public static final Block KITTY_PLUSHIE = new TableBlock(FabricBlockSettings.copyOf(com.sp.init.ModBlocks.CarpetBlock).hardness(-1f).noBlockBreakParticles().nonOpaque().sounds(SprintBlockSoundGroup.CARPET));
    public static final Block EXIT_SIGN = new CeilingLight(FabricBlockSettings.copyOf(com.sp.init.ModBlocks.CarpetBlock).collidable(false).hardness(-1f).noBlockBreakParticles().nonOpaque());


    public static final Block TABLE = new TableBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.WOOD).dropsNothing().nonOpaque());
    public static final Block BED1 = new TableBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.WOOD).dropsNothing().nonOpaque());
    public static final Block BED2 = new TableBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.WOOD).dropsNothing().nonOpaque());


    public static final Block IKEA_EXIT = new Ikea_Exit_Block(FabricBlockSettings.copyOf(com.sp.init.ModBlocks.ConcreteBlock1)
            .hardness(-1f)
            .noBlockBreakParticles()
            .collidable(false)
            .sounds(BlockSoundGroup.WOOL)
            .nonOpaque().air(),
            BackroomsLevels.OVERWORLD_REPRESENTING_BACKROOMS_LEVEL.getWorldKey(),
            com.sp.init.BackroomsLevels.LEVEL0_WORLD_KEY);


    public static final Block LEVELTRANSFERBLOCK = registerBlock("leveltransferblock",
            new Level_Transfer_Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)
                    .hardness(-1f)
                    .solid()
                    .noBlockBreakParticles()
                    .collidable(false)));


    public static void registerModBlocks() {
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "hotel_wall"), HOTEL_WALL);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "kitty_plushie"), KITTY_PLUSHIE);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "exit_sign"), EXIT_SIGN);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "kitty_wall"), KITTY_WALL);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "kitty_floor"), KITTY_FLOOR);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "ikea_exit"), IKEA_EXIT);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "ikea_shelf"), IKEA_SHELF);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "ikea_shelf1"), IKEA_SHELF1);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "ikea_shelf2"), IKEA_SHELF2);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "hotel_floor"), HOTEL_FLOOR);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "tests"), TESTS);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "table"), TABLE);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "bed1"), BED1);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "bed2"), BED2);
    }
}
