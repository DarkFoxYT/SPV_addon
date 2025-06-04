package net.dark.spv_addon.world.generation.run;



import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sp.SPBRevampedClient;
import net.dark.spv_addon.Spv_addon;
import net.minecraft.block.Blocks;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Custom chunk generator for the "Run" dimension of the SPV Addon mod.
 * Generates a long corridor with an entrance room, hallways, and an exit room.
 * Also places a roof above each generated segment.
 */
public final class RunChunkGenerator extends ChunkGenerator {
    /**
     * Codec for serialization/deserialization of RunChunkGenerator.
     */
    public static final Codec<RunChunkGenerator> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(g -> g.biomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(g -> g.settings)
            ).apply(inst, inst.stable(RunChunkGenerator::new))
    );

    private final RegistryEntry<ChunkGeneratorSettings> settings;
    private final Random random = Random.create();
    private final int corridorLength;

    /**
     * Constructor for the "Run" chunk generator.
     *
     * @param biomeSource Source of biomes used for generation.
     * @param settings Chunk generation settings.
     */
    public RunChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource);
        SPBRevampedClient.setInBackrooms(true);
        this.settings = settings;
        this.corridorLength = random.nextBetween(100, 500);
    }

    /**
     * Returns the length of the generated corridor.
     *
     * @return corridor length in blocks.
     */
    public int getCorridorLength() {
        return corridorLength;
    }

    /**
     * Returns the zero-based index of the chunk containing the exit room.
     *
     * @return exit chunk index (X axis).
     */
    public int getExitChunkIndex() {
        // (corridorLength−1)/16 is how you computed it before
        return (corridorLength - 1) / 16;
    }

    /**
     * Generates structures (rooms, hallways, roof) in the given chunk.
     *
     * @param world World access for generation.
     * @param chunk Target chunk.
     * @param structureAccessor Structure accessor.
     */
    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        int cx = chunk.getPos().x;
        int cz = chunk.getPos().z;
        if (cz != 0) {
            SPBRevampedClient.setInBackrooms(false);
            return;
        }
        int exitChunk = (corridorLength - 1) / 16;
        Identifier roomId;
        if (cx == 0) {
            roomId = new Identifier(Spv_addon.MOD_ID, "run/entrance");
        } else if (cx == exitChunk) {
            roomId = new Identifier(Spv_addon.MOD_ID, "run/exit");
        } else if (cx < exitChunk) {
            int hallwayType = random.nextBetween(1, 5);
            roomId = new Identifier(Spv_addon.MOD_ID, "run/hallway" + hallwayType);
        } else {
            return;
        }

        MinecraftServer server = world.getServer();
        if (server == null) return;
        StructureTemplateManager mgr = server.getStructureTemplateManager();
        Optional<StructureTemplate> optTpl = mgr.getTemplate(roomId);
        if (optTpl.isEmpty()) return;

        int bx = chunk.getPos().getStartX();
        int bz = chunk.getPos().getStartZ();

        int yOffset = 0;
        StructureTemplate template = optTpl.get();
        for (StructureTemplate.StructureBlockInfo blockInfo : template.getInfosForBlock(BlockPos.ORIGIN, new StructurePlacementData(), Blocks.LIME_WOOL)) {
            if (blockInfo.state().isOf(Blocks.LIME_WOOL)) {
                yOffset = blockInfo.pos().getY() + 1;
                break;
            }
        }

        BlockPos.Mutable basePos = new BlockPos.Mutable(bx, -yOffset, bz);
        StructurePlacementData placeData = new StructurePlacementData()
                .setMirror(BlockMirror.NONE)
                .setRotation(BlockRotation.CLOCKWISE_90)
                .setIgnoreEntities(true);

        if (!template.place(world, basePos, basePos, placeData, random, 2)) return;

        Identifier roofId = new Identifier(Spv_addon.MOD_ID, "run/run_roof1");
        Optional<StructureTemplate> roofTpl = mgr.getTemplate(roofId);
        if (roofTpl.isEmpty()) return;

        StructurePlacementData roofData = new StructurePlacementData()
                .setMirror(BlockMirror.NONE)
                .setRotation(BlockRotation.CLOCKWISE_90)
                .setIgnoreEntities(true);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int px = bx + 8 * i;
                int pz = bz + 8 * j;
                BlockPos roofPos = new BlockPos(px, 5 - yOffset, pz);

                if (world.getBlockState(roofPos) == Blocks.AIR.getDefaultState()) {
                    roofTpl.get().place(world, roofPos, roofPos, roofData, random, 16);
                }
            }
        }
    }

    /**
     * Returns the codec used for serialization of this generator.
     *
     * @return Codec for RunChunkGenerator.
     */
    @Override protected Codec<? extends ChunkGenerator> getCodec() { return CODEC; }

    /**
     * Does nothing, as noise population is not used here.
     *
     * @return unchanged chunk.
     */
    @Override public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender,
                                                            NoiseConfig noiseConfig,
                                                            StructureAccessor structureAccessor,
                                                            Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    /**
     * Returns the sea level (always 0 for this generator).
     *
     * @return 0
     */
    @Override public int getSeaLevel() { return 0; }

    /**
     * Returns the minimum world height (always 0).
     *
     * @return 0
     */
    @Override public int getMinimumY() { return 0; }

    /**
     * Returns the maximum world height (256).
     *
     * @return 256
     */
    @Override public int getWorldHeight() { return 256; }

    /**
     * Returns the height at the given position (always world height).
     */
    @Override
    public int getHeight(int x, int z, net.minecraft.world.Heightmap.Type type,
                         net.minecraft.world.HeightLimitView view,
                         NoiseConfig noiseConfig) {
        return getWorldHeight();
    }

    /**
     * Returns a column sample filled with air blocks.
     */
    @Override
    public VerticalBlockSample getColumnSample(int x, int z,
                                               net.minecraft.world.HeightLimitView view,
                                               NoiseConfig noiseConfig) {
        var states = new net.minecraft.block.BlockState[getWorldHeight()];
        for (int i = 0; i < states.length; i++) {
            states[i] = net.minecraft.block.Blocks.AIR.getDefaultState();
        }
        return new VerticalBlockSample(0, states);
    }

    /**
     * Does nothing, as no carving is needed.
     */
    @Override public void carve(ChunkRegion region, long seed,
                                NoiseConfig noiseConfig, net.minecraft.world.biome.source.BiomeAccess biomeAccess,
                                StructureAccessor structAcc, Chunk chunk,
                                GenerationStep.Carver carverStep) {}

    /**
     * Does nothing, as no surface is generated.
     */
    @Override public void buildSurface(ChunkRegion region,
                                       StructureAccessor structAcc,
                                       NoiseConfig noiseConfig,
                                       Chunk chunk) {}

    /**
     * Does nothing, as no entities are generated.
     */
    @Override public void populateEntities(ChunkRegion region) {}

    /**
     * Does nothing, as no debug text is added.
     */
    @Override public void getDebugHudText(java.util.List<String> text,
                                          NoiseConfig noiseConfig,
                                          BlockPos pos) {}
}