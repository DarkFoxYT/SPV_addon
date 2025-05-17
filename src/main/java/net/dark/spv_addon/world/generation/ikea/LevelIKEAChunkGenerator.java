package net.dark.spv_addon.world.generation.ikea;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dark.spv_addon.Spv_addon;
import net.minecraft.block.BlockState;
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
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.noise.NoiseConfig;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class LevelIKEAChunkGenerator extends ChunkGenerator {
    public static final Codec<LevelIKEAChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(generator -> generator.settings)
            ).apply(instance, instance.stable(LevelIKEAChunkGenerator::new))
    );

    private final RegistryEntry<ChunkGeneratorSettings> settings;

    public LevelIKEAChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource);
        this.settings = settings;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        // Rien à faire ici, le terrain reste plat/air
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getSeaLevel() { return 0; }

    @Override
    public int getMinimumY() { return 0; }

    @Override
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) { return 48; }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        // Fix: utiliser BlockState[] du bon type (évite intrusive holder crash)
        BlockState[] states = new BlockState[world.getHeight()];
        Arrays.fill(states, Blocks.VOID_AIR.getDefaultState());
        return new VerticalBlockSample(0, states);
    }

    @Override
    public void getDebugHudText(java.util.List<String> text, NoiseConfig noiseConfig, BlockPos pos) {}

    @Override
    public int getWorldHeight() { return 128; }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) { }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) { }

    @Override
    public void populateEntities(ChunkRegion region) { }

    /**
     * Cette méthode doit être appelée dans l'événement de génération, ex :
     * via un mixin ou event chunk, pour placer les salles/structures de l'IKEA.
     */
    public void generateFeatures(StructureWorldAccess world, Chunk chunk) {
        int x = chunk.getPos().getStartX();
        int z = chunk.getPos().getStartZ();

        MinecraftServer server = world.getServer();
        if (server == null) return;
        StructureTemplateManager manager = server.getStructureTemplateManager();
        BlockPos.Mutable pos = new BlockPos.Mutable();

        // Salle d'entrée centrale au spawn
        if (chunk.getPos().x == 0 && chunk.getPos().z == 0) {
            Identifier entryRoom = new Identifier(Spv_addon.MOD_ID, "ikea/entrance");
            Optional<StructureTemplate> template = manager.getTemplate(entryRoom);
            template.ifPresent(struct -> struct.place(world, pos.set(x, 20, z), pos, defaultPlacement(), world.getRandom(), 2));
        }

        // Rooms spéciales random
        Random random = Random.create();
        if (random.nextFloat() < 0.12f) { // 12% de rooms spéciales par chunk, change le taux si tu veux
            Identifier roomId = getRandomIkeaRoom(random);
            Optional<StructureTemplate> template = manager.getTemplate(roomId);
            template.ifPresent(struct -> struct.place(world, pos.set(x, 20, z), pos, randomRotation(random), random, 2));
        } else {
            // Open space IKEA de base
            Identifier openSpace = new Identifier(Spv_addon.MOD_ID, "ikea/open_space" + (random.nextBetween(1, 3)));
            Optional<StructureTemplate> template = manager.getTemplate(openSpace);
            template.ifPresent(struct -> struct.place(world, pos.set(x, 20, z), pos, defaultPlacement(), random, 2));
        }

        // Placer un exit rare (1 sur 100 chunks)
        if (random.nextBetween(1, 100) == 1) {
            Identifier exitRoom = new Identifier(Spv_addon.MOD_ID, "ikea/exit");
            Optional<StructureTemplate> template = manager.getTemplate(exitRoom);
            template.ifPresent(struct -> struct.place(world, pos.set(x, 20, z), pos, defaultPlacement(), random, 2));
        }
    }

    private Identifier getRandomIkeaRoom(Random random) {
        // Ajoute tes rooms ici, ex: ikea/room1.nbt, ikea/room2.nbt, etc.
        int room = random.nextBetween(1, 6); // Modifie le nombre max selon ce que t’as
        return new Identifier(Spv_addon.MOD_ID, "ikea/room" + room);
    }

    private StructurePlacementData defaultPlacement() {
        return new StructurePlacementData().setMirror(BlockMirror.NONE).setRotation(BlockRotation.NONE).setIgnoreEntities(true);
    }
    private StructurePlacementData randomRotation(Random random) {
        StructurePlacementData data = new StructurePlacementData().setMirror(BlockMirror.NONE).setIgnoreEntities(true);
        BlockRotation[] rotations = {BlockRotation.NONE, BlockRotation.CLOCKWISE_90, BlockRotation.CLOCKWISE_180, BlockRotation.COUNTERCLOCKWISE_90};
        data.setRotation(rotations[random.nextBetween(0, rotations.length - 1)]);
        return data;
    }
}
