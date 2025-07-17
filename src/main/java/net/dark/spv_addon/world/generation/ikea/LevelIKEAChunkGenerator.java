package net.dark.spv_addon.world.generation.ikea;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.sp.SPBRevampedClient;
import com.sp.world.generation.chunk_generator.BackroomsChunkGenerator;
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

public final class LevelIKEAChunkGenerator extends BackroomsChunkGenerator {
    public static final Codec<LevelIKEAChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(gen -> gen.settings)
            ).apply(instance, LevelIKEAChunkGenerator::new)
    );


    private final RegistryEntry<ChunkGeneratorSettings> settings;
    private final Random random = Random.create();

    public LevelIKEAChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource);
        this.settings = settings;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void generate(StructureWorldAccess world, Chunk chunk) {
        this.generateFeatures(world, chunk, null);
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        int cx = chunk.getPos().x;
        int cz = chunk.getPos().z;

        MinecraftServer server = world.getServer();
        if (server == null) return;
        StructureTemplateManager mgr = server.getStructureTemplateManager();

        int gridSize = 32;

        for (int gx = 0; gx < 128; gx++) {
            for (int gz = 0; gz < 128; gz++) {
                int px = gx * 32;
                int pz = gz * 32;
                int chunkX = px / 16;
                int chunkZ = pz / 16;
                if (chunkX == cx && chunkZ == cz) {
                    String pathName = "ikea/pathway_room" + (((gx + gz) % 4) + 1);
                    Identifier pathId = new Identifier(Spv_addon.MOD_ID, pathName);
                    Optional<StructureTemplate> optPath = mgr.getTemplate(pathId);
                    if (optPath.isPresent()) {
                        BlockPos pathPos = new BlockPos(px, 0, pz);
                        StructurePlacementData pathData = new StructurePlacementData()
                                .setMirror(BlockMirror.NONE)
                                .setIgnoreEntities(true);
                        optPath.get().place(world, pathPos, pathPos, pathData, random, 2);
                    }
                }
                int rightPx = px + 32;
                int rightChunkX = rightPx / 16;
                int rightChunkZ = pz / 16;
                if (rightChunkX == cx && rightChunkZ == cz) {
                    String miscName = "ikea/misc_room" + (((gx + gz) % 4) + 1);
                    Identifier miscId = new Identifier(Spv_addon.MOD_ID, miscName);
                    Optional<StructureTemplate> optMisc = mgr.getTemplate(miscId);
                    if (optMisc.isPresent()) {
                        BlockPos miscPos = new BlockPos(rightPx, 0, pz);
                        StructurePlacementData miscData = new StructurePlacementData()
                                .setMirror(BlockMirror.NONE)
                                .setRotation(BlockRotation.values()[random.nextInt(BlockRotation.values().length)])
                                .setIgnoreEntities(true);
                        optMisc.get().place(world, miscPos, miscPos, miscData, random, 2);
                    }
                }
                // Génération des salles à gauche
                int leftPx = px - 32;
                int leftChunkX = leftPx / 16;
                int leftChunkZ = pz / 16;
                if (leftChunkX == cx && leftChunkZ == cz) {
                    String miscName = "ikea/misc_room" + (((gx + gz + 2) % 4) + 1);
                    Identifier miscId = new Identifier(Spv_addon.MOD_ID, miscName);
                    Optional<StructureTemplate> optMisc = mgr.getTemplate(miscId);
                    if (optMisc.isPresent()) {
                        BlockPos miscPos = new BlockPos(leftPx, 0, pz);
                        StructurePlacementData miscData = new StructurePlacementData()
                                .setMirror(BlockMirror.NONE)
                                .setRotation(BlockRotation.values()[random.nextInt(BlockRotation.values().length)])
                                .setIgnoreEntities(true);
                        optMisc.get().place(world, miscPos, miscPos, miscData, random, 2);
                    }
                }
            }
        }
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                String roofName = random.nextBoolean() ? "ikea/roof1" : "ikea/roof2";
                Identifier roofId = new Identifier(Spv_addon.MOD_ID, roofName);
                Optional<StructureTemplate> optRoof = mgr.getTemplate(roofId);
                if (optRoof.isEmpty()) continue;

                int bx = chunk.getPos().getStartX();
                int bz = chunk.getPos().getStartZ();
                int px = bx + 8 * i;
                int pz = bz + 8 * j;

                BlockPos roofPos = new BlockPos(px, 10, pz);

                StructurePlacementData roofData = new StructurePlacementData()
                        .setMirror(BlockMirror.NONE)
                        .setRotation(BlockRotation.NONE)
                        .setIgnoreEntities(true);

                optRoof.get().place(world, roofPos, roofPos, roofData, random, 16);
            }
        }

    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender,
                                                  NoiseConfig noiseConfig,
                                                  StructureAccessor structureAccessor,
                                                  Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getWorldHeight() {
        return 256;
    }

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
            states[i] = Blocks.AIR.getDefaultState();
        }
        return new VerticalBlockSample(0, states);
    }

    @Override
    public void carve(ChunkRegion region, long seed,
                      NoiseConfig noiseConfig, net.minecraft.world.biome.source.BiomeAccess biomeAccess,
                      StructureAccessor structAcc, Chunk chunk,
                      GenerationStep.Carver carverStep) {
    }

    @Override
    public void buildSurface(ChunkRegion region,
                             StructureAccessor structAcc,
                             NoiseConfig noiseConfig,
                             Chunk chunk) {
    }

    @Override
    public void populateEntities(ChunkRegion region) {
    }

    @Override
    public void getDebugHudText(java.util.List<String> text,
                                NoiseConfig noiseConfig,
                                BlockPos pos) {
    }
}
