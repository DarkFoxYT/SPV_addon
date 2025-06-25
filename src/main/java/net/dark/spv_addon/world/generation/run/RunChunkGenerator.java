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

public final class RunChunkGenerator extends ChunkGenerator {

    public static final Codec<RunChunkGenerator> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(g -> g.biomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(g -> g.settings)
            ).apply(inst, inst.stable(RunChunkGenerator::new))
    );

    private final RegistryEntry<ChunkGeneratorSettings> settings;
    private final Random random = Random.create();
    private final int corridorLength;


    public RunChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource);
        SPBRevampedClient.setInBackrooms(true);
        this.settings = settings;
        this.corridorLength = random.nextBetween(100, 500);
    }


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

        int roofY = 5;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int px = bx + 8 * i;
                int pz = bz + 8 * j;
                BlockPos roofPos = new BlockPos(px, roofY, pz);
                    roofTpl.get().place(world, roofPos, roofPos, roofData, random, 16);
                }
            }
        }


    @Override protected Codec<? extends ChunkGenerator> getCodec() { return CODEC; }


    @Override public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender,
                                                            NoiseConfig noiseConfig,
                                                            StructureAccessor structureAccessor,
                                                            Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }


    @Override public int getSeaLevel() { return 0; }


    @Override public int getMinimumY() { return 0; }


    @Override public int getWorldHeight() { return 256; }


    @Override
    public int getHeight(int x, int z, net.minecraft.world.Heightmap.Type type,
                         net.minecraft.world.HeightLimitView view,
                         NoiseConfig noiseConfig) {
        return getWorldHeight();
    }


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

    @Override public void carve(ChunkRegion region, long seed,
                                NoiseConfig noiseConfig, net.minecraft.world.biome.source.BiomeAccess biomeAccess,
                                StructureAccessor structAcc, Chunk chunk,
                                GenerationStep.Carver carverStep) {}

    @Override public void buildSurface(ChunkRegion region,
                                       StructureAccessor structAcc,
                                       NoiseConfig noiseConfig,
                                       Chunk chunk) {}


    @Override public void populateEntities(ChunkRegion region) {}


    @Override public void getDebugHudText(java.util.List<String> text,
                                          NoiseConfig noiseConfig,
                                          BlockPos pos) {}
}