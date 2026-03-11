package net.dark.spv_addon.world.generation.framework;

import net.minecraft.block.Block;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shared helpers for deterministic template placement across custom level generators.
 */
public final class StructurePlacementHelper {
    private static final Map<Identifier, Optional<StructureTemplate>> TEMPLATE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Integer> MARKER_Y_CACHE = new ConcurrentHashMap<>();

    private StructurePlacementHelper() {
    }

    public static Random chunkRandom(int chunkX, int chunkZ, long salt) {
        long seed = 341873128712L * chunkX + 132897987541L * chunkZ + salt;
        return Random.create(seed);
    }

    public static Optional<StructureTemplate> template(StructureWorldAccess world, Identifier id) {
        MinecraftServer server = world.getServer();
        if (server == null) {
            return Optional.empty();
        }
        StructureTemplateManager manager = server.getStructureTemplateManager();
        return TEMPLATE_CACHE.computeIfAbsent(id, manager::getTemplate);
    }

    public static int markerYOffset(Identifier templateId, StructureTemplate template, Block marker) {
        String key = templateId + "|" + marker.getTranslationKey();
        return MARKER_Y_CACHE.computeIfAbsent(key, ignored -> {
            for (StructureTemplate.StructureBlockInfo info : template.getInfosForBlock(BlockPos.ORIGIN, new StructurePlacementData(), marker)) {
                return info.pos().getY();
            }
            return 0;
        });
    }
}

