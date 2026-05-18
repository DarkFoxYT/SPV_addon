package net.dark.spv_addon.world.generation.level188;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dark.spv_addon.init.ModBlocks;
import net.dark.spv_addon.world.generation.framework.StructurePlacementHelper;
import net.dark.spv_addon.world.generation.framework.TemplateBackroomsChunkGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.util.math.random.Random;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Level188ChunkGenerator extends TemplateBackroomsChunkGenerator {
    private static final int CELL_SIZE = 32;
    private static final int CELL_CENTER = CELL_SIZE / 2;
    private static final int HALL_HALF_WIDTH = 3;
    private static final int FLOOR_Y = 56;
    private static final int CEILING_Y = 64;
    private static final BlockState LAMP_STATE = Blocks.REDSTONE_LAMP.getDefaultState();

    public static final Codec<Level188ChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(gen -> gen.settings)
            ).apply(instance, Level188ChunkGenerator::new)
    );

    public Level188ChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource, settings);
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        int cx = chunk.getPos().x;
        int cz = chunk.getPos().z;
        Random random = StructurePlacementHelper.chunkRandom(cx, cz, 0x31_38_38L);
        int baseX = chunk.getPos().getStartX();
        int baseZ = chunk.getPos().getStartZ();

        generateHotelGrid(world, baseX, baseZ);
        placeRoomTemplateIfAvailable(world, random, baseX, baseZ);
    }

    private void generateHotelGrid(StructureWorldAccess world, int baseX, int baseZ) {
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int x = baseX + lx;
                int z = baseZ + lz;
                boolean wall = isWallColumn(x, z);
                boolean corridor = isCorridorColumn(x, z);

                pos.set(x, FLOOR_Y, z);
                world.setBlockState(pos, floorState(), 2);
                pos.set(x, CEILING_Y, z);
                world.setBlockState(pos, wallState(), 2);

                for (int y = FLOOR_Y + 1; y < CEILING_Y; y++) {
                    pos.set(x, y, z);
                    world.setBlockState(pos, wall ? wallState() : Blocks.AIR.getDefaultState(), 2);
                }

                if (corridor && shouldPlaceLamp(x, z)) {
                    pos.set(x, CEILING_Y - 1, z);
                    world.setBlockState(pos, LAMP_STATE, 2);
                }
            }
        }
    }

    private void placeRoomTemplateIfAvailable(StructureWorldAccess world, Random random, int baseX, int baseZ) {
        if (Math.floorMod(baseX, CELL_SIZE) != 0 || Math.floorMod(baseZ, CELL_SIZE) != 0) {
            return;
        }

        Identifier id = new Identifier("spv_addon", "level188/room1");
        Optional<StructureTemplate> tpl = StructurePlacementHelper.template(world, id);
        if (tpl.isEmpty()) return;

        int markerYOffset = StructurePlacementHelper.markerYOffset(id, tpl.get(), Blocks.LIME_WOOL);
        BlockPos base = new BlockPos(baseX, FLOOR_Y - markerYOffset, baseZ);
        StructurePlacementData data = new StructurePlacementData()
                .setMirror(BlockMirror.NONE)
                .setRotation(BlockRotation.NONE)
                .setIgnoreEntities(true);

        tpl.get().place(world, base, base, data, random, 2);
    }

    private static boolean isCorridorColumn(int x, int z) {
        int localX = Math.floorMod(x, CELL_SIZE);
        int localZ = Math.floorMod(z, CELL_SIZE);
        return Math.abs(localX - CELL_CENTER) <= HALL_HALF_WIDTH
                || Math.abs(localZ - CELL_CENTER) <= HALL_HALF_WIDTH;
    }

    private static boolean isWallColumn(int x, int z) {
        int localX = Math.floorMod(x, CELL_SIZE);
        int localZ = Math.floorMod(z, CELL_SIZE);
        boolean verticalCellWall = localX == 0 || localX == CELL_SIZE - 1;
        boolean horizontalCellWall = localZ == 0 || localZ == CELL_SIZE - 1;
        boolean verticalDoorway = Math.abs(localZ - CELL_CENTER) <= HALL_HALF_WIDTH;
        boolean horizontalDoorway = Math.abs(localX - CELL_CENTER) <= HALL_HALF_WIDTH;
        boolean cellWall = (verticalCellWall && !verticalDoorway) || (horizontalCellWall && !horizontalDoorway);
        boolean interiorPillar = (localX == 8 || localX == 24) && (localZ == 8 || localZ == 24);
        return cellWall || interiorPillar;
    }

    private static boolean shouldPlaceLamp(int x, int z) {
        return Math.floorMod(x + z * 31, 17) == 0;
    }

    private static BlockState floorState() {
        return ModBlocks.LE_FLOOR.getDefaultState();
    }

    private static BlockState wallState() {
        return ModBlocks.HOTEL_WALL.getDefaultState();
    }

    @Override
    public int getWorldHeight() {
        return 256;
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return CEILING_Y + 1;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        int h = world.getHeight();
        BlockState[] states = new BlockState[h];
        int bottom = world.getBottomY();
        for (int i = 0; i < h; i++) {
            int y = bottom + i;
            states[i] = (y == 0) ? Blocks.BEDROCK.getDefaultState() : Blocks.AIR.getDefaultState();
        }
        return new VerticalBlockSample(bottom, states);
    }

}
