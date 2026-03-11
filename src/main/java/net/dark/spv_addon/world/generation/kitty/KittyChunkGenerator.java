package net.dark.spv_addon.world.generation.kitty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.world.generation.framework.StructurePlacementHelper;
import net.dark.spv_addon.world.generation.framework.TemplateBackroomsChunkGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
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

public final class KittyChunkGenerator extends TemplateBackroomsChunkGenerator {
    public static final Codec<KittyChunkGenerator> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
                    ChunkGeneratorSettings.REGISTRY_CODEC.fieldOf("settings").forGetter(gen -> gen.settings)
            ).apply(instance, KittyChunkGenerator::new)
    );

    public KittyChunkGenerator(BiomeSource biomeSource, RegistryEntry<ChunkGeneratorSettings> settings) {
        super(biomeSource, settings);
    }

    private static void applyLootTablesToCrates(StructureWorldAccess world, BlockPos start, BlockPos end, Identifier lootTable) {
        BlockPos.iterate(start, end).forEach(pos -> {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof LootableContainerBlockEntity crate) {
                crate.setLootTable(lootTable, world.getRandom().nextLong());
                crate.markDirty();
            }
        });
    }

    @Override
    protected Codec<? extends ChunkGenerator> getCodec() {
        return CODEC;
    }

    @Override
    public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor) {
        int cx = chunk.getPos().x;
        int cz = chunk.getPos().z;
        Random random = StructurePlacementHelper.chunkRandom(cx, cz, 0x4B_49_54_54_59L);

        if (cx % 2 == 0 && cz % 2 == 0) {
            int rx = cx / 2;
            int rz = cz / 2;

            Identifier roomId;
            if (rx == 0 && rz == 0) {
                roomId = new Identifier(Spv_addon.MOD_ID, "kitty/entrance");
            } else {
                int minRoom = 1, maxRoom = 27;
                int bound = maxRoom - minRoom + 1;
                int variant = minRoom;
                variant = minRoom + random.nextInt(bound);
                roomId = new Identifier(Spv_addon.MOD_ID, "kitty/room" + variant);
            }

            Optional<StructureTemplate> optTpl = StructurePlacementHelper.template(world, roomId);
            if (optTpl.isEmpty()) return;

            int bx = chunk.getPos().getStartX();
            int bz = chunk.getPos().getStartZ();
            BlockPos.Mutable basePos = new BlockPos.Mutable(bx, 0, bz);

            StructurePlacementData placeData = new StructurePlacementData()
                    .setMirror(BlockMirror.NONE)
                    .setRotation(BlockRotation.NONE)
                    .setIgnoreEntities(true);

            optTpl.get().place(world, basePos, basePos, placeData, random, 2);

            applyLootTablesToCrates(world, basePos, basePos.add(15, 15, 15), new Identifier("spb-revamped", "wooden_crate"));
        }

        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                String roofName = random.nextBoolean() ? "kitty/roof1" : "kitty/roof2";
                Identifier roofId = new Identifier(Spv_addon.MOD_ID, roofName);
                Optional<StructureTemplate> optRoof = StructurePlacementHelper.template(world, roofId);
                if (optRoof.isEmpty()) continue;

                int bx = chunk.getPos().getStartX();
                int bz = chunk.getPos().getStartZ();
                int px = bx + 8 * i;
                int pz = bz + 8 * j;

                BlockPos roofPos = new BlockPos(px, 5, pz);

                StructurePlacementData roofData = new StructurePlacementData()
                        .setMirror(BlockMirror.NONE)
                        .setRotation(BlockRotation.NONE)
                        .setIgnoreEntities(true);

                optRoof.get().place(world, roofPos, roofPos, roofData, random, 16);
            }
        }
    }

}
