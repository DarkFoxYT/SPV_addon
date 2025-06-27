package net.dark.spv_addon.world.generation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Level5ChunkGenerator extends ChunkGenerator {
    public static final Codec<Level5ChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(gen -> gen.settings)
            ).apply(instance, Level5ChunkGenerator::new)
    );

    private final RegistryEntry<ChunkGeneratorSettings> settings;
    private final BiomeSource biomeSource;

    public Level5ChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource);
        this.biomeSource = biomeSource;
        this.settings = settings;
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void carve(ChunkRegion chunkRegion, long seed, NoiseConfig noiseConfig, BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, GenerationStep.Carver carverStep) {

    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, NoiseConfig noiseConfig, Chunk chunk) {

    }

    @Override
    public void populateEntities(ChunkRegion region) {

    }

    @Override
    public int getWorldHeight() {
        return 0;
    }

    @Override
    public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender, NoiseConfig noiseConfig, StructureAccessor structureAccessor, Chunk chunk) {
        return null;
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
    public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world, NoiseConfig noiseConfig) {
        return 0;
    }

    @Override
    public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world, NoiseConfig noiseConfig) {
        return null;
    }

    @Override
    public void getDebugHudText(List<String> text, NoiseConfig noiseConfig, BlockPos pos) {

    }

    public void generate(StructureWorldAccess world, Chunk chunk) {
        int baseX = chunk.getPos().getStartX();
        int baseZ = chunk.getPos().getStartZ();
        Random random = Random.create();
        MinecraftServer server = world.getServer();
        if (server == null) return;
        StructureTemplateManager templateManager = server.getStructureTemplateManager();

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        int gridSize = 3;
        int spacing = 20;
        int[][] occupied = new int[64][64];

        for (int gx = 0; gx < gridSize; gx++) {
            for (int gz = 0; gz < gridSize; gz++) {
                Level5RoomRegistry.RoomType type = switch (random.nextInt(5)) {
                    case 0 -> Level5RoomRegistry.RoomType.GUESTROOM;
                    case 1 -> Level5RoomRegistry.RoomType.MEGAROOM;
                    case 2 -> Level5RoomRegistry.RoomType.HALLWAY;
                    case 3 -> Level5RoomRegistry.RoomType.LOBBY;
                    default -> Level5RoomRegistry.RoomType.JUNCTION;
                };

                var room = Level5RoomRegistry.getRandomRoom(type, random);
                if (room == null) continue;

                int px = baseX + gx * spacing;
                int pz = baseZ + gz * spacing;

                boolean overlap = false;
                for (int x = px; x < px + room.sizeX(); x++)
                    for (int z = pz; z < pz + room.sizeZ(); z++)
                        if (occupied[x % 64][z % 64] == 1) {
                            overlap = true;
                            break;
                        }
                if (overlap) continue;

                // Marque comme "occupé"
                for (int x = px; x < px + room.sizeX(); x++)
                    for (int z = pz; z < pz + room.sizeZ(); z++)
                        occupied[x % 64][z % 64] = 1;

                var opt = templateManager.getTemplate(room.id());
                if (opt.isPresent()) {
                    opt.get().place(world, mutable.set(px, 18, pz), mutable.set(px, 18, pz), randomRotation(), random, 2);
                }
            }
        }

        // Place quelques toits random par-dessus (tu peux améliorer la logique)
        for (int rx = 0; rx < gridSize; rx++) {
            for (int rz = 0; rz < gridSize; rz++) {
                var roof = Level5RoomRegistry.getRandomRoom(Level5RoomRegistry.RoomType.ROOF, random);
                if (roof == null) continue;
                int px = baseX + rx * spacing;
                int pz = baseZ + rz * spacing;
                var opt = templateManager.getTemplate(roof.id());
                if (opt.isPresent()) {
                    opt.get().place(world, mutable.set(px, 32, pz), mutable.set(px, 32, pz), randomRotation(), random, 16);
                }
            }
        }


    }


    private void placeRoofs(StructureWorldAccess world, int x, int z, Random random, StructureTemplateManager manager, BlockPos.Mutable pos) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                Identifier id = new Identifier(Spv_addon.MOD_ID, "level5/roof1");
                StructurePlacementData data = randomRotation();
                Optional<StructureTemplate> template = manager.getTemplate(id);
                int px = x + 8 * i;
                int pz = z + 8 * j;

                if (world.getBlockState(pos.set(px, 32, pz)) == Blocks.AIR.getDefaultState()) {
                    template.ifPresent(struct -> struct.place(world, new BlockPos(px, 32, pz), pos, data, random, 16));
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

    public class Level5RoomRegistry {
        private static final List<RoomEntry> registeredRooms = new ArrayList<>();

        public static void register(RoomType type, Identifier id, int sizeX, int sizeZ) {
            registeredRooms.add(new RoomEntry(type, id, sizeX, sizeZ));
        }

        public static List<RoomEntry> getRooms(RoomType type) {
            return registeredRooms.stream().filter(e -> e.type == type).toList();
        }

        public static RoomEntry getRandomRoom(RoomType type, Random random) {
            List<RoomEntry> list = getRooms(type);
            return list.isEmpty() ? null : list.get(random.nextInt(list.size()));
        }

        public enum RoomType {GUESTROOM, HALLWAY, JUNCTION, LOBBY, MEGAROOM, ROOF, STAIRS, STORAGE, TRAP}

        public record RoomEntry(RoomType type, Identifier id, int sizeX, int sizeZ) {
        }
    }
}
