package net.dark.spv_addon;

import com.mojang.serialization.Codec;
import net.dark.spv_addon.init.ModBlocks;
import net.dark.spv_addon.init.ModChunkGenerators;
import net.dark.spv_addon.items.ModItems;
import net.dark.spv_addon.util.FlashlightBatteryEvents;
import net.dark.spv_addon.commands.FlashlightBatteryCommand;
import net.dark.spv_addon.world.generation.Level5ChunkGenerator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class Spv_addon implements ModInitializer {
    @Override
    public void onInitialize() {
        ModItems.registerItems();
        FlashlightBatteryEvents.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            FlashlightBatteryCommand.register(dispatcher);
        });
        ModChunkGenerators.register();
        ModBlocks.registerModBlocks();

    }
}
