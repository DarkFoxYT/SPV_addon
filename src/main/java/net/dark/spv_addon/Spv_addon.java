package net.dark.spv_addon;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.entity.ik.model.GeckoLib.MowzieModelFactory;
import com.sp.networking.InitializePackets;
import eu.midnightdust.lib.config.MidnightConfig;
import net.dark.spv_addon.commands.Level5Command;
import net.dark.spv_addon.commands.ThirstCommand;
import net.dark.spv_addon.compat.modmenu.ConfigStuff;
import net.dark.spv_addon.init.*;
import net.dark.spv_addon.init.ModItemGroups;
import net.dark.spv_addon.init.ModItems;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.dark.spv_addon.Additions.battery.FlashlightBatteryEvents;
import net.dark.spv_addon.commands.FlashlightBatteryCommand;
import net.dark.spv_addon.voicechat.SpvAddonVoicechatPlugin;
import net.dark.spv_addon.world.generation.run.RunChunkGenerator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sp.SPBRevamped.sendBlackScreenPacket;

public class Spv_addon implements ModInitializer {
    public static final String MOD_ID = "spv_addon";
    public static final Logger LOGGER = LoggerFactory.getLogger("spv_addon");
    public static final int finalMazeSize = 5;



    @Override
    public void onInitialize() {
        ModKeybinds.registerKeybinds();


        ThirstManager.register();
        net.dark.spv_addon.sanity.SanityManager.register();
        ThirstManager.register();

        ModItems.registerItems();
        FlashlightBatteryEvents.register();
        ModBlocks.registerModBlocks();
        ModChunkGenerators.register();
        ModItemGroups.registerItemGroups();
        ModSounds.registerSounds();
        ThirstManager.register();
        MidnightConfig.init(MOD_ID, ConfigStuff.class);

        GeckoLibUtil.addCustomBakedModelFactory(MOD_ID, new MowzieModelFactory());
        GeckoLib.initialize();



        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            FlashlightBatteryCommand.register(dispatcher);
            Level5Command.register(dispatcher);
            ThirstCommand.register(dispatcher);
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            SpvAddonVoicechatPlugin.justSpoke.clear();
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(((player, origin, destination) -> {
            PacketByteBuf buffer = PacketByteBufs.create();
            ServerPlayNetworking.send(player, InitializePackets.RELOAD_LIGHTS, buffer);
        }));

        ServerPlayerEvents.AFTER_RESPAWN.register(((oldPlayer, newPlayer, alive) -> {
            if(!com.sp.init.BackroomsLevels.isInBackrooms(oldPlayer.getWorld().getRegistryKey())) {
                return;
            }

            boolean backupInvulnerable;
            try {
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(newPlayer);

                sendBlackScreenPacket(newPlayer, 120, false, false);
                backupInvulnerable = newPlayer.getAbilities().invulnerable;
                newPlayer.getAbilities().invulnerable = true;
                playerComponent.setShouldRender(false);
                playerComponent.sync();
                newPlayer.networkHandler.sendPacket(new PlaySoundS2CPacket(RegistryEntry.of(ModSounds.DONG), SoundCategory.AMBIENT, newPlayer.getPos().getX(), newPlayer.getPos().getY(), newPlayer.getPos().getZ(), 100.0f, 1.0f, newPlayer.getRandom().nextLong()));

                //After YOU CAN'T ESCAPE is over
                executorService.schedule(() -> {
                    playerComponent.setShouldRender(true);
                    playerComponent.setShouldDoStatic(true);
                    playerComponent.sync();
                    newPlayer.getAbilities().invulnerable = backupInvulnerable;
                    executorService.shutdown();
                }, 6000, TimeUnit.MILLISECONDS);

                executorService.schedule(() -> {
                    playerComponent.setShouldDoStatic(false);
                    playerComponent.sync();
                    executorService.shutdown();
                }, 8000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                LOGGER.error("Error in AFTER_RESPAWN event: {}", String.valueOf(e));
            }
        }));



        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerWorld world : server.getWorlds()) {
                // only run in your “run” dimension
                if (world.getRegistryKey() != BackroomsLevels.LEVELRUN_WORLD_KEY) continue;

                // grab the chunk generator and cast
                if (!(world.getChunkManager().getChunkGenerator() instanceof RunChunkGenerator runGen)) continue;
                int exitChunkX = runGen.getExitChunkIndex();

                for (ServerPlayerEntity player : world.getPlayers()) {
                    ChunkPos cpos = player.getChunkPos();
                    // exit room is always at cz == 0, cx == exitChunkX
                    if (cpos.x != exitChunkX || cpos.z != 0) continue;

                    // how far into the chunk?
                    double localX = player.getX() - (cpos.x * 16.0);
                    // once they pass halfway (8 blocks)…
                    if (localX > 0) {
                        // teleport them to your next level
                        var destKey = BackroomsLevels.LEVEL5_WORLD_KEY; // change to whatever
                        ServerWorld target = server.getWorld(destKey);
                        if (target != null) {
                            BlockPos spawn = target.getSpawnPos();
                            player.teleport(target, spawn.getX(), spawn.getY(), spawn.getZ(), player.getYaw(), player.getPitch());
                        }
                    }
                }
            }
        });
    }
}
