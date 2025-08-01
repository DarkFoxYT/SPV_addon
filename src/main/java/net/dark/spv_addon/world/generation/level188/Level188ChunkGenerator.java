package net.dark.spv_addon.world.generation.level188;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sp.init.ModBlocks;
import com.sp.world.generation.chunk_generator.BackroomsChunkGenerator;
import net.dark.spv_addon.Spv_addon;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
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
import java.util.Optional;
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
    private final Random random = Random.create();

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
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;

        // Only spawn the structure at chunk coordinates (0,0) which contains world coordinates (0,0,0)
        if (chunkX == 0 && chunkZ == 0) {
            MinecraftServer server = world.getServer();
            if (server == null) return;

            StructureTemplateManager mgr = server.getStructureTemplateManager();
            Identifier roomId = new Identifier(Spv_addon.MOD_ID, "level105/room1");
            Optional<StructureTemplate> optTpl = mgr.getTemplate(roomId);

            if (optTpl.isPresent()) {
                BlockPos basePos = new BlockPos(0, 100, 0);

                StructurePlacementData placeData = new StructurePlacementData()
                        .setMirror(BlockMirror.NONE)
                        .setRotation(BlockRotation.NONE)
                        .setIgnoreEntities(true);

                optTpl.get().place(world, basePos, basePos, placeData, random, 2);
            }
        }
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
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        // Check if we're within the 64x64 structure bounds at 0,0,0
        if (x >= 0 && x < 64 && z >= 0 && z < 64) {
            return 100; // Structure height (64x100x64)
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
            } else {
                states[y] = Blocks.AIR.getDefaultState(); // Air everywhere else (structure will be placed by template)
            }
        }

        return new VerticalBlockSample(world.getBottomY(), states);
    }

    public void generate(StructureWorldAccess world, Chunk chunk) {}
}
