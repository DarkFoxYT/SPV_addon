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
    private static final int STRUCTURE_X = 0;
    private static final int STRUCTURE_Y = 0;
    private static final int STRUCTURE_Z = 0;
    private static final int STRUCTURE_WIDTH = 64;
    private static final int STRUCTURE_HEIGHT = 100;
    private static final int STRUCTURE_DEPTH = 64;

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
        int chunkStartX = chunkX * 16;
        int chunkStartZ = chunkZ * 16;
        int chunkEndX = chunkStartX + 16;
        int chunkEndZ = chunkStartZ + 16;

        // Check if this chunk intersects with the structure bounds
        if (chunkStartX < STRUCTURE_X + STRUCTURE_WIDTH && chunkEndX > STRUCTURE_X &&
            chunkStartZ < STRUCTURE_Z + STRUCTURE_DEPTH && chunkEndZ > STRUCTURE_Z) {

            generateLargeStructureFromTemplate(world, chunk, chunkStartX, chunkStartZ);
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
        // Check if we're within the structure bounds
        if (x >= STRUCTURE_X && x < STRUCTURE_X + STRUCTURE_WIDTH &&
            z >= STRUCTURE_Z && z < STRUCTURE_Z + STRUCTURE_DEPTH) {
            return STRUCTURE_Y + STRUCTURE_HEIGHT; // Structure height
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
            } else if (x >= STRUCTURE_X && x < STRUCTURE_X + STRUCTURE_WIDTH &&
                       z >= STRUCTURE_Z && z < STRUCTURE_Z + STRUCTURE_DEPTH &&
                       worldY >= STRUCTURE_Y && worldY < STRUCTURE_Y + STRUCTURE_HEIGHT) {
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

    private void generateLargeStructureFromTemplate(StructureWorldAccess world, Chunk chunk, int chunkStartX, int chunkStartZ) {
        MinecraftServer server = world.getServer();
        if (server == null) return;

        StructureTemplateManager mgr = server.getStructureTemplateManager();

        // Calculate which chunk we're in relative to the structure origin
        int relativeChunkX = chunkStartX / 16;
        int relativeChunkZ = chunkStartZ / 16;

        // Only place structures if we're within the 4x4 chunk area (64x64 blocks)
        if (relativeChunkX >= 0 && relativeChunkX < 4 && relativeChunkZ >= 0 && relativeChunkZ < 4) {

            // Determine which structure template to use based on position
            Identifier structureId;
            if (relativeChunkX == 0 && relativeChunkZ == 0) {
                // Corner piece - entrance or special room
                structureId = new Identifier(Spv_addon.MOD_ID, "level188/entrance");
            } else if (relativeChunkX == 3 && relativeChunkZ == 3) {
                // Opposite corner - exit or special room
                structureId = new Identifier(Spv_addon.MOD_ID, "level188/exit");
            } else {
                // Regular room pieces - use different variants
                int variant = ((relativeChunkX + relativeChunkZ) % 4) + 1;
                structureId = new Identifier(Spv_addon.MOD_ID, "level188/room" + variant);
            }

            Optional<StructureTemplate> optTemplate = mgr.getTemplate(structureId);
            if (optTemplate.isEmpty()) {
                // Fallback: generate a simple structure if template doesn't exist
                generateFallbackStructure(world, chunk, chunkStartX, chunkStartZ);
                return;
            }

            StructureTemplate template = optTemplate.get();

            // Calculate the exact position for this chunk's structure
            BlockPos basePos = new BlockPos(chunkStartX, STRUCTURE_Y, chunkStartZ);

            StructurePlacementData placementData = new StructurePlacementData()
                    .setMirror(BlockMirror.NONE)
                    .setRotation(BlockRotation.NONE)
                    .setIgnoreEntities(true);

            // Place the template for this chunk
            template.place(world, basePos, basePos, placementData, random, 2);
        }
    }

    private void generateFallbackStructure(StructureWorldAccess world, Chunk chunk, int chunkStartX, int chunkStartZ) {
        // Fallback method that generates a simple 64x100x64 structure when template is not found
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockState structureBlock = ModBlocks.CONCRETE_BLOCK_1.getDefaultState();

        // Calculate the intersection of this chunk with the structure bounds
        int structureStartX = Math.max(STRUCTURE_X, chunkStartX);
        int structureEndX = Math.min(STRUCTURE_X + STRUCTURE_WIDTH, chunkStartX + 16);
        int structureStartZ = Math.max(STRUCTURE_Z, chunkStartZ);
        int structureEndZ = Math.min(STRUCTURE_Z + STRUCTURE_DEPTH, chunkStartZ + 16);

        // Generate the structure part that falls within this chunk
        for (int worldX = structureStartX; worldX < structureEndX; worldX++) {
            for (int worldZ = structureStartZ; worldZ < structureEndZ; worldZ++) {
                // Convert world coordinates to chunk-local coordinates
                int localX = worldX - chunkStartX;
                int localZ = worldZ - chunkStartZ;

                // Calculate relative position within the structure
                int relativeX = worldX - STRUCTURE_X;
                int relativeZ = worldZ - STRUCTURE_Z;

                // Generate structure blocks from Y=0 to Y=99 (100 blocks high)
                for (int y = STRUCTURE_Y; y < STRUCTURE_Y + STRUCTURE_HEIGHT; y++) {
                    // Create hollow structure with walls only on the edges
                    boolean isEdge = (relativeX == 0 || relativeX == STRUCTURE_WIDTH - 1 ||
                                     relativeZ == 0 || relativeZ == STRUCTURE_DEPTH - 1 ||
                                     y == STRUCTURE_Y || y == STRUCTURE_Y + STRUCTURE_HEIGHT - 1);

                    if (isEdge && localX >= 0 && localX < 16 && localZ >= 0 && localZ < 16) {
                        BlockPos worldPos = new BlockPos(worldX, y, worldZ);
                        world.setBlockState(worldPos, structureBlock, 3);
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
