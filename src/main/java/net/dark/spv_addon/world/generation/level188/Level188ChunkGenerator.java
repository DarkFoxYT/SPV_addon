package net.dark.spv_addon.world.generation.level188;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sp.init.ModBlocks;
import com.sp.world.generation.chunk_generator.BackroomsChunkGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Level188ChunkGenerator extends BackroomsChunkGenerator {
    public static final Codec<Level188ChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(gen -> gen.settings)
            ).apply(instance, Level188ChunkGenerator::new)
    );

    private final RegistryEntry<ChunkGeneratorSettings> settings;
    private final BiomeSource biomeSource;
    private static final int STRUCTURE_X = 0;
    private static final int STRUCTURE_Y = 60;
    private static final int STRUCTURE_Z = 0;

    public Level188ChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource);
        this.biomeSource = biomeSource;
        this.settings = settings;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {}

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {}

    @Override
    public void populateEntities(ChunkRegion region) {}

    @Override
    public int getWorldHeight() {
        return 384;
    }

    @Override
    public int getSeaLevel() {
        return 0;
    }

    @Override
    public int getMinimumY() {
        return 0;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.supplyAsync(() -> {
            generateLevel188Terrain(chunk);
            return chunk;
        }, executor);
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        // Check if we're at the structure location
        if (x == STRUCTURE_X && z == STRUCTURE_Z) {
            return STRUCTURE_Y + 10; // Structure height estimate
        }
        return 1; // Flat ground level
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        BlockState[] states = new BlockState[world.getHeight()];

        for (int y = 0; y < states.length; y++) {
            int worldY = world.getBottomY() + y;
            if (worldY == 0) {
                states[y] = ModBlocks.CONCRETE_BLOCK_1.getDefaultState(); // Flat ground
            } else if (x == STRUCTURE_X && z == STRUCTURE_Z && worldY >= STRUCTURE_Y && worldY < STRUCTURE_Y + 10) {
                states[y] = ModBlocks.CONCRETE_BLOCK_1.getDefaultState(); // Structure blocks
            } else {
                states[y] = Blocks.AIR.getDefaultState(); // Air everywhere else
            }
        }

        return new VerticalBlockSample(world.getBottomY(), states);
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
        text.add("Flat world generator (Level 188)");
    }

    public void generate(StructureWorldAccess world, Chunk chunk) {}

    private void generateLevel188Terrain(Chunk chunk) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockState groundBlock = ModBlocks.CONCRETE_BLOCK_1.getDefaultState();

        // Generate flat ground at Y=0
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                mutable.set(x, 0, z);
                chunk.setBlockState(mutable, groundBlock, false);
            }
        }

        // Generate single structure at 0, 60, 0 if this chunk contains that position
        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;

        // Check if structure coordinates fall within this chunk
        if (STRUCTURE_X >= chunkX * 16 && STRUCTURE_X < (chunkX + 1) * 16 &&
            STRUCTURE_Z >= chunkZ * 16 && STRUCTURE_Z < (chunkZ + 1) * 16) {

            // Convert world coordinates to chunk-local coordinates
            int localX = STRUCTURE_X - (chunkX * 16);
            int localZ = STRUCTURE_Z - (chunkZ * 16);

            generateStructure(chunk, localX, localZ);
        }
    }

    private void generateStructure(Chunk chunk, int localX, int localZ) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockState structureBlock = ModBlocks.CONCRETE_BLOCK_1.getDefaultState();

        // Generate a simple tower structure at the specified location
        for (int y = STRUCTURE_Y; y < STRUCTURE_Y + 10; y++) {
            // Create a 3x3 platform at the base
            if (y == STRUCTURE_Y) {
                for (int x = localX - 1; x <= localX + 1; x++) {
                    for (int z = localZ - 1; z <= localZ + 1; z++) {
                        if (x >= 0 && x < 16 && z >= 0 && z < 16) {
                            mutable.set(x, y, z);
                            chunk.setBlockState(mutable, structureBlock, false);
                        }
                    }
                }
            }
            // Create walls for the tower
            else if (y < STRUCTURE_Y + 9) {
                for (int x = localX - 1; x <= localX + 1; x++) {
                    for (int z = localZ - 1; z <= localZ + 1; z++) {
                        if (x >= 0 && x < 16 && z >= 0 && z < 16) {
                            // Only place blocks on the edges (walls)
                            if (x == localX - 1 || x == localX + 1 || z == localZ - 1 || z == localZ + 1) {
                                mutable.set(x, y, z);
                                chunk.setBlockState(mutable, structureBlock, false);
                            }
                        }
                    }
                }
            }
            // Create roof
            else if (y == STRUCTURE_Y + 9) {
                for (int x = localX - 1; x <= localX + 1; x++) {
                    for (int z = localZ - 1; z <= localZ + 1; z++) {
                        if (x >= 0 && x < 16 && z >= 0 && z < 16) {
                            mutable.set(x, y, z);
                            chunk.setBlockState(mutable, structureBlock, false);
                        }
                    }
                }
            }
        }
    }

    private StructurePlacementData defaultPlacement() {
        return new StructurePlacementData().setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
    }

    private StructurePlacementData randomRotation() {
        StructurePlacementData data = new StructurePlacementData().setMirror(BlockMirror.NONE).setIgnoreEntities(true);
        data.setRotation(Random.create().nextBetween(1, 2) == 1 ? BlockRotation.NONE : BlockRotation.CLOCKWISE_90);
        return data;
    }
}
