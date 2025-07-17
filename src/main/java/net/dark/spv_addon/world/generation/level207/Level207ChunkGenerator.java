package net.dark.spv_addon.world.generation.level207;

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

public final class Level207ChunkGenerator extends ChunkGenerator {
    public static final Codec<Level207ChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(gen -> gen.settings)
            ).apply(instance, Level207ChunkGenerator::new)
    );

    private final RegistryEntry<ChunkGeneratorSettings> settings;
    private final Random random = Random.create();

    public Level207ChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource);
        this.settings = settings;
        SPBRevampedClient.setInBackrooms(true);
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        int cx = chunk.getPos().x;
        int cz = chunk.getPos().z;

        if (cx % 2 == 0 && cz % 2 == 0) {
            int rx = cx / 2;
            int rz = cz / 2;

            Identifier roomId;
            if (rx == 0 && rz == 0) {
                roomId = new Identifier(Spv_addon.MOD_ID, "level207/entrance");
            } else {
                int variant = 1 + random.nextInt(3); // 1-3 inclus
                roomId = new Identifier(Spv_addon.MOD_ID, "level207/grave" + variant);
            }

            MinecraftServer server = world.getServer();
            if (server == null) return;
            StructureTemplateManager mgr = server.getStructureTemplateManager();
            Optional<StructureTemplate> optTpl = mgr.getTemplate(roomId);
            if (optTpl.isEmpty()) return;

            StructureTemplate template = optTpl.get();

            int limeYOffset = 0;
            for (StructureTemplate.StructureBlockInfo info : template.getInfosForBlock(BlockPos.ORIGIN, new StructurePlacementData(), Blocks.LIME_WOOL)) {
                limeYOffset = info.pos().getY();
                break;
            }

            int bx = chunk.getPos().getStartX();
            int bz = chunk.getPos().getStartZ();
            int baseY = 20 - limeYOffset;

            BlockPos.Mutable basePos = new BlockPos.Mutable(bx, baseY, bz);
            StructurePlacementData placeData = new StructurePlacementData()
                    .setMirror(BlockMirror.NONE)
                    .setRotation(BlockRotation.NONE)
                    .setIgnoreEntities(true);

            template.place(world, basePos, basePos, placeData, random, 2);
        }
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender,
                                                  NoiseConfig noiseConfig,
                                                  StructureAccessor structureAccessor,
                                                  Chunk chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override public int getSeaLevel() { return 0; }
    @Override public int getMinimumY() { return 0; }
    @Override public int getWorldHeight() { return 256; }
    @Override public int getHeight(int x, int z, net.minecraft.world.Heightmap.Type type, net.minecraft.world.HeightLimitView view, NoiseConfig noiseConfig) { return getWorldHeight(); }
    @Override public VerticalBlockSample getColumnSample(int x, int z, net.minecraft.world.HeightLimitView view, NoiseConfig noiseConfig) {
        var states = new net.minecraft.block.BlockState[getWorldHeight()];
        for (int i = 0; i < states.length; i++) states[i] = Blocks.AIR.getDefaultState();
        return new VerticalBlockSample(0, states);
    }

    @Override
    public void carve(ChunkRegion region, long seed, NoiseConfig noiseConfig, net.minecraft.world.biome.source.BiomeAccess biomeAccess, StructureAccessor structAcc, Chunk chunk, GenerationStep.Carver carverStep) {}

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
