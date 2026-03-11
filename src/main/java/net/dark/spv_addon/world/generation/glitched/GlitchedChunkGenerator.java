package net.dark.spv_addon.world.generation.glitched;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dark.spv_addon.world.generation.framework.TemplateBackroomsChunkGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class GlitchedChunkGenerator extends TemplateBackroomsChunkGenerator {
    public static final Codec<GlitchedChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(gen -> gen.settings)
            ).apply(instance, GlitchedChunkGenerator::new)
    );

    private static final BlockState[] GLITCH_PALETTE = new BlockState[]{
            Blocks.WHITE_CONCRETE.getDefaultState(),
            Blocks.CYAN_CONCRETE.getDefaultState(),
            Blocks.MAGENTA_CONCRETE.getDefaultState(),
            Blocks.BLACKSTONE.getDefaultState(),
            Blocks.PURPLE_CONCRETE.getDefaultState(),
            Blocks.TINTED_GLASS.getDefaultState(),
            Blocks.GRAY_CONCRETE.getDefaultState()
    };

    public GlitchedChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource, settings);
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;
        int baseX = chunk.getPos().getStartX();
        int baseZ = chunk.getPos().getStartZ();
        int roomProfile = Math.floorMod(hash(chunkX, chunkZ, 17), 4);
        boolean hubChunk = Math.floorMod(hash(chunkX, chunkZ, 91), 7) == 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        for (int lx = 0; lx < 16; lx++) {
            for (int lz = 0; lz < 16; lz++) {
                int x = baseX + lx;
                int z = baseZ + lz;
                float corridorNoise = corridorNoise(x, z);
                float chamberNoise = chamberNoise(x, z);
                int floorY = 28 + (hubChunk && Math.abs(lx - 8) < 3 && Math.abs(lz - 8) < 3 ? 1 : 0);
                int ceilingY = 36 + Math.floorMod(hash(x, z, 401), 2);
                boolean corridor = isCorridor(lx, lz, corridorNoise, chamberNoise, roomProfile, hubChunk);
                boolean pillar = !corridor && isPillar(lx, lz, roomProfile);
                boolean glitchWindow = corridor && Math.floorMod(hash(x, z, 57), 11) == 0;

                BlockState floorState = pickPalette(x, z, floorY);
                BlockState ceilingState = pickPalette(x, z, ceilingY);

                mutable.set(x, floorY, z);
                world.setBlockState(mutable, floorState, 3);
                mutable.set(x, ceilingY, z);
                world.setBlockState(mutable, ceilingState, 3);

                for (int y = floorY + 1; y < ceilingY; y++) {
                    mutable.set(x, y, z);
                    if (corridor) {
                        if (glitchWindow && y > floorY + 1 && y < ceilingY - 1) {
                            world.setBlockState(mutable, Blocks.TINTED_GLASS.getDefaultState(), 3);
                        } else {
                            world.setBlockState(mutable, Blocks.AIR.getDefaultState(), 3);
                        }
                    } else if (pillar && y < ceilingY - 1) {
                        world.setBlockState(mutable, Blocks.BLACKSTONE.getDefaultState(), 3);
                    } else {
                        world.setBlockState(mutable, pickPalette(x, z, y), 3);
                    }
                }

                if (corridor && hash(x, z, 91) % 17 == 0) {
                    mutable.set(x, floorY + 1, z);
                    world.setBlockState(mutable, Blocks.REDSTONE_LAMP.getDefaultState(), 3);
                }
            }
        }
    }

    private static float corridorNoise(int x, int z) {
        double wave = Math.sin(x * 0.12) + Math.cos(z * 0.10);
        double drift = Math.sin((x + z) * 0.035);
        return (float) (wave * 0.45 + drift * 0.25);
    }

    private static float chamberNoise(int x, int z) {
        double wave = Math.cos((x - z) * 0.05) + Math.sin((x + z) * 0.025);
        return (float) (wave * 0.5);
    }

    private static boolean isCorridor(int lx, int lz, float corridorNoise, float chamberNoise, int roomProfile, boolean hubChunk) {
        int dx = Math.abs(lx - 8);
        int dz = Math.abs(lz - 8);
        boolean axisHall = dx <= 2 || dz <= 2;
        boolean diagonalBreak = Math.abs(dx - dz) <= 1 && roomProfile >= 2;
        boolean offsetPocket = chamberNoise > 0.18f && dx + dz < 9;

        if (hubChunk) {
            return dx + dz < 8 || axisHall;
        }

        return switch (roomProfile) {
            case 0 -> axisHall && corridorNoise > -0.35f;
            case 1 -> (dx <= 1 || dz <= 3) && corridorNoise < 0.42f;
            case 2 -> diagonalBreak || offsetPocket;
            default -> axisHall || (corridorNoise > -0.08f && corridorNoise < 0.26f);
        };
    }

    private static boolean isPillar(int lx, int lz, int roomProfile) {
        int dx = Math.abs(lx - 8);
        int dz = Math.abs(lz - 8);
        return switch (roomProfile) {
            case 0 -> dx == 5 && dz == 5;
            case 1 -> dx == 4 && dz <= 1;
            case 2 -> dx == dz && dx >= 4;
            default -> (dx == 5 && dz == 2) || (dx == 2 && dz == 5);
        };
    }

    private static BlockState pickPalette(int x, int z, int y) {
        int idx = Math.floorMod(hash(x, z, y), GLITCH_PALETTE.length);
        return GLITCH_PALETTE[idx];
    }

    private static int hash(int x, int z, int y) {
        int h = x * 73428767 ^ z * 912931 ^ y * 19349663;
        h ^= (h >>> 13);
        h *= 1274126177;
        return h;
    }

    @Override
    protected int surfaceHeight() {
        return 37;
    }
}
