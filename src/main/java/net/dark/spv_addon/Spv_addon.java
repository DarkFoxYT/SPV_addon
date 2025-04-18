package net.dark.spv_addon;

import com.mojang.serialization.Codec;
import com.sp.SPBRevamped;
import com.sp.entity.ik.model.GeckoLib.MowzieModelFactory;
import com.sp.world.generation.Level0ChunkGenerator;
import net.dark.spv_addon.commands.Level5Command;
import net.dark.spv_addon.init.ModBlocks;
import net.dark.spv_addon.init.ModChunkGenerators;
import net.dark.spv_addon.init.ModSounds;
import net.dark.spv_addon.items.ModItemGroups;
import net.dark.spv_addon.items.ModItems;
import net.dark.spv_addon.util.FlashlightBatteryEvents;
import net.dark.spv_addon.commands.FlashlightBatteryCommand;
import net.dark.spv_addon.voicechat.SpvAddonVoicechatPlugin;
import net.dark.spv_addon.world.generation.Level5ChunkGenerator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Spv_addon implements ModInitializer {
    public static final String MOD_ID = "spv_addon";
    public static final Logger LOGGER = LoggerFactory.getLogger("spv_addon");
    public static final int finalMazeSize = 5;



    @Override
    public void onInitialize() {
        ModItems.registerItems();
        FlashlightBatteryEvents.register();
        ModBlocks.registerModBlocks();
        ModChunkGenerators.register();
        ModItemGroups.registerItemGroups();
        ModSounds.registerSounds();


        GeckoLibUtil.addCustomBakedModelFactory(MOD_ID, new MowzieModelFactory());
        GeckoLib.initialize();



        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            FlashlightBatteryCommand.register(dispatcher);
            Level5Command.register(dispatcher);
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            SpvAddonVoicechatPlugin.justSpoke.clear();
        });
    }
}
