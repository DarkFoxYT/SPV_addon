package net.dark.spv_addon.init;

import com.sp.block.SprintBlockSoundGroup;
import com.sp.block.custom.CarpetBlock;
import com.sp.block.custom.WallBlock;
import com.sp.block.custom.WallText;
import net.dark.spv_addon.blocks.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;


public class ModBlocks {
    public static final Block HOTEL_WALL = new WallBlock(FabricBlockSettings.copyOf(com.sp.init.ModBlocks.WallBlock).hardness(-1.0F).solid().noBlockBreakParticles().sounds(SprintBlockSoundGroup.WALL));
    public static final Block C_WALL = new WallBlock(FabricBlockSettings.copyOf(com.sp.init.ModBlocks.WallBlock).hardness(-1.0F).solid().noBlockBreakParticles().sounds(SprintBlockSoundGroup.CARPET));
    public static final Block KITTY_WALL = new WallBlock(FabricBlockSettings.copyOf(com.sp.init.ModBlocks.WallBlock).hardness(-1.0F).solid().noBlockBreakParticles().sounds(SprintBlockSoundGroup.WALL));
    public static final Block KITTY_FLOOR = new CarpetBlock(FabricBlockSettings.copyOf(com.sp.init.ModBlocks.CarpetBlock).hardness(-1f).noBlockBreakParticles().sounds(SprintBlockSoundGroup.CARPET));
    public static final Block KITTY_ROOF = new CarpetBlock(FabricBlockSettings.copyOf(com.sp.init.ModBlocks.CeilingTile).hardness(-1f).noBlockBreakParticles().sounds(SprintBlockSoundGroup.CARPET));
    public static final Block GRASS1 = new Block(FabricBlockSettings.copyOf(Blocks.DIRT).strength(-1.0F).noBlockBreakParticles().sounds(SprintBlockSoundGroup.GRASS2));
    public static final Block HOTEL_FLOOR = new CarpetBlock(FabricBlockSettings.copyOf(com.sp.init.ModBlocks.CarpetBlock).hardness(-1f).noBlockBreakParticles().sounds(SprintBlockSoundGroup.CARPET));
    public static final Block IKEA_SHELF = new ShelfBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.COPPER).dropsNothing().nonOpaque());
    public static final Block IKEA_SHELF1 = new ShelfBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.COPPER).dropsNothing().nonOpaque());
    public static final Block IKEA_SHELF2 = new ShelfBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.COPPER).dropsNothing().nonOpaque());
    public static final Block KITTY_PLUSHIE = new PlushieBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).hardness(0.5f).noBlockBreakParticles().nonOpaque().sounds(SprintBlockSoundGroup.CARPET));
    public static final Block KITTY_LAMP = new Kittylamp(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).hardness(-1f).noBlockBreakParticles().nonOpaque());
    public static final Block KITTY_PLUSHIE1 = new PlushieBlock(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).hardness(0.5f).noBlockBreakParticles().nonOpaque().sounds(SprintBlockSoundGroup.CARPET));
    public static final Block KITTY_PLUSHIE_DEV = new PlushieBlock_bonk(FabricBlockSettings.copyOf(Blocks.WHITE_WOOL).hardness(0.5f).noBlockBreakParticles().nonOpaque().sounds(SprintBlockSoundGroup.CARPET));
    public static final Block CROSS = new CrossBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS).hardness(-1f).noBlockBreakParticles().nonOpaque().sounds(SprintBlockSoundGroup.CARPET));
    public static final Block EXIT_SIGN = new ExitSignBlock(FabricBlockSettings.copyOf(com.sp.init.ModBlocks.RED_METAL_CASING).hardness(-1f).noBlockBreakParticles().nonOpaque());
    public static final Block TAPE_RECORDER = new TapeRecorderBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).hardness(-1f).noBlockBreakParticles().nonOpaque());
    public static final Block VENT = new VentBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).hardness(-1f).noBlockBreakParticles().nonOpaque());
    public static final Block IKEA_ARROW = new WallText(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).hardness(-1f).noBlockBreakParticles().nonOpaque().collidable(false).sounds(SprintBlockSoundGroup.SILENT));


    public static final Block TABLE = new TableBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.WOOD).dropsNothing().nonOpaque());
    public static final Block BED1 = new BedBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.WOOD).dropsNothing().nonOpaque());
    public static final Block BED2 = new BedBlock2(FabricBlockSettings.copyOf(Blocks.OAK_WOOD).hardness(-1f).noBlockBreakParticles().sounds(BlockSoundGroup.WOOD).dropsNothing().nonOpaque());



    public static void registerModBlocks() {
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "hotel_wall"), HOTEL_WALL);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "kitty_lamp"), KITTY_LAMP);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "c_wall"), C_WALL);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "grass1"), GRASS1);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "kitty_plushie"), KITTY_PLUSHIE);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "kitty_plushie1"), KITTY_PLUSHIE1);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "kitty_plushie_dev"), KITTY_PLUSHIE_DEV);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "cross"), CROSS);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "exit_sign"), EXIT_SIGN);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "kitty_wall"), KITTY_WALL);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "kitty_floor"), KITTY_FLOOR);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "ikea_shelf"), IKEA_SHELF);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "ikea_shelf1"), IKEA_SHELF1);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "ikea_shelf2"), IKEA_SHELF2);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "hotel_floor"), HOTEL_FLOOR);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "table"), TABLE);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "bed1"), BED1);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "bed2"), BED2);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "tape_recorder"), TAPE_RECORDER);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "vent1"), VENT);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "kitty_roof"), KITTY_ROOF);
        Registry.register(Registries.BLOCK, new Identifier("spv_addon", "ikea_arrow"), IKEA_ARROW);
    }
}
