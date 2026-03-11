package net.dark.spv_addon.world.generation.framework;

import com.sp.world.generation.chunk_generator.BackroomsChunkGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.entry.RegistryEntry;
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
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Shared no-noise Backrooms generator foundation for template-driven levels.
 */
public abstract class TemplateBackroomsChunkGenerator extends BackroomsChunkGenerator {
    protected final RegistryEntry<ChunkGeneratorSettings> settings;

    protected TemplateBackroomsChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource);
        this.settings = settings;
    }

    @Override
    public void generate(StructureWorldAccess world, Chunk chunk) {
        this.generateFeatures(world, chunk, null);
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
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
    public int getWorldHeight() {
        return 256;
    }

    protected int surfaceHeight() {
        return getWorldHeight();
    }

    protected BlockState columnState(int y) {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type type, HeightLimitView view, NoiseConfig noiseConfig) {
        return surfaceHeight();
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView view, NoiseConfig noiseConfig) {
        int bottom = getMinimumY();
        int height = getWorldHeight();
        BlockState[] states = new BlockState[height];
        for (int i = 0; i < height; i++) {
            states[i] = columnState(bottom + i);
        }
        return new VerticalBlockSample(bottom, states);
    }

    @Override
    public void carve(ChunkRegion region, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structAcc, NoiseConfig noiseConfig, Chunk chunk) {
    }

    @Override
    public void populateEntities(ChunkRegion region) {
    }

    @Override
    public void getDebugHudText(java.util.List<String> text, NoiseConfig noiseConfig, BlockPos pos) {
    }
}
