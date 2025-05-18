// net.dark.spv_addon.init.ModChunkGenerators.java

package net.dark.spv_addon.init;

import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.world.generation.Level5ChunkGenerator;
import net.dark.spv_addon.world.generation.ikea.LevelIKEAChunkGenerator;
import net.dark.spv_addon.world.generation.run.RunChunkGenerator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ModChunkGenerators {
    public static void register() {
        Registry.register(
                Registries.CHUNK_GENERATOR,
                new Identifier(Spv_addon.MOD_ID, "level5_chunk_generator"),  // all lowercase
                Level5ChunkGenerator.CODEC
        );
        Registry.register(
                Registries.CHUNK_GENERATOR,
                new Identifier(Spv_addon.MOD_ID, "levelrun_chunk_generator"),  // all lowercase, no caps
                RunChunkGenerator.CODEC
        );
        Registry.register(
                Registries.CHUNK_GENERATOR,
                new Identifier(Spv_addon.MOD_ID, "level_ikea"), // all lowercase
                LevelIKEAChunkGenerator.CODEC
        );
    }
}
