package net.dark.spv_addon.init;

import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.world.generation.glitched.GlitchedChunkGenerator;
import net.dark.spv_addon.world.generation.level188.Level188ChunkGenerator;
import net.dark.spv_addon.world.generation.ikea.LevelIKEAChunkGenerator;
import net.dark.spv_addon.world.generation.kitty.KittyChunkGenerator;
import net.dark.spv_addon.world.generation.level207.Level207ChunkGenerator;
import net.dark.spv_addon.world.generation.run.RunChunkGenerator;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModChunkGenerators {
    public static void register() {
        // Exemple pour Fabric
        Registry.register(Registries.CHUNK_GENERATOR, new Identifier(Spv_addon.MOD_ID, "level188_chunk_generator"), Level188ChunkGenerator.CODEC);
        Registry.register(Registries.CHUNK_GENERATOR, new Identifier(Spv_addon.MOD_ID, "level207_chunk_generator"), Level207ChunkGenerator.CODEC);
        Registry.register(Registries.CHUNK_GENERATOR, new Identifier(Spv_addon.MOD_ID, "level_run_chunk_generator"), RunChunkGenerator.CODEC);
        Registry.register(Registries.CHUNK_GENERATOR, new Identifier(Spv_addon.MOD_ID, "level_ikea"), LevelIKEAChunkGenerator.CODEC);
        Registry.register(Registries.CHUNK_GENERATOR, new Identifier(Spv_addon.MOD_ID, "level_kitty"), KittyChunkGenerator.CODEC);
        Registry.register(Registries.CHUNK_GENERATOR, new Identifier(Spv_addon.MOD_ID, "glitched_chunk_generator"), GlitchedChunkGenerator.CODEC);
    }
}
