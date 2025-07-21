package net.dark.spv_addon;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.entity.ik.model.GeckoLib.MowzieModelFactory;
import net.dark.spv_addon.Additions.battery.FlashlightBatteryEvents;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.dark.spv_addon.commands.SpvCommands;
import net.dark.spv_addon.init.config.SpvAddonConfig;
import net.dark.spv_addon.world.events.level207.Level207AmbianceHandler;
import net.dark.spv_addon.init.gamerules.SpvGameRules;
import net.dark.spv_addon.init.*;
import net.dark.spv_addon.init.voicechat.SpvAddonVoicechatPlugin;
import net.dark.spv_addon.world.events.misc.LevelRunGlobalTicker;
import net.dark.spv_addon.world.events.misc.RedWoolTeleporter;
import net.dark.spv_addon.world.events.misc.WoolTeleporter207;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
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

    @Override
    public void onInitialize() {
        SpvAddonConfig.init("spv_addon", SpvAddonConfig.class);

        SpvGameRules.initialize();

        ModBlockEntities.register();
        ThirstManager.register();
        FlashlightBatteryEvents.register();
        Level207AmbianceHandler.register();
        ModChunkGenerators.register();
        BackroomsLevels.init();
        ModItems.registerItems();
        ModBlocks.registerModBlocks();
        ModItemGroups.registerItemGroups();
        ModSounds.registerSounds();
        LevelRunGlobalTicker.init();
        GeckoLibUtil.addCustomBakedModelFactory(MOD_ID, new MowzieModelFactory());
        GeckoLib.initialize();

        net.dark.spv_addon.init.crawl.CrawlSystem.initialize();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> SpvCommands.register(dispatcher));

        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                RedWoolTeleporter.tickPlayer(player);
                WoolTeleporter207.tickPlayer(player);
            }
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            SpvAddonVoicechatPlugin.justMadeNoise.clear();
            if (SpvAddonVoicechatPlugin.voicechatApi != null) {
                SpvAddonVoicechatPlugin.voicechatApi.getBroadcastRange();
            }
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (!com.sp.init.BackroomsLevels.isInBackrooms(oldPlayer.getWorld().getRegistryKey())) {
                return;
            }
            try {
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(newPlayer);

                // Stop any existing blackscreen and sounds
                com.sp.SPBRevamped.sendBlackScreenPacket(newPlayer, 0, false, false);


                // Reset player state
                playerComponent.setShouldRender(true);
                playerComponent.setShouldDoStatic(false);
                playerComponent.sync();

                // Brief respawn effect
                com.sp.SPBRevamped.sendBlackScreenPacket(newPlayer, 60, true, false);
                boolean backupInvulnerable = newPlayer.getAbilities().invulnerable;
                newPlayer.getAbilities().invulnerable = true;

                // Schedule restoration after brief blackscreen
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(() -> {
                    try {
                        playerComponent.setShouldRender(true);
                        playerComponent.setShouldDoStatic(true);
                        playerComponent.sync();
                        newPlayer.getAbilities().invulnerable = backupInvulnerable;

                        // Stop static effect after a short time
                        executorService.schedule(() -> {
                            try {
                                playerComponent.setShouldDoStatic(false);
                                playerComponent.sync();
                            } catch (Exception e) {
                                LOGGER.error("Error stopping static effect: ", e);
                            } finally {
                                executorService.shutdown();
                            }
                        }, 4, TimeUnit.SECONDS);

                    } catch (Exception e) {
                        LOGGER.error("Error in respawn restoration: ", e);
                        executorService.shutdown();
                    }
                }, 3, TimeUnit.SECONDS);

            } catch (Exception e) {
                LOGGER.error("Error in AFTER_RESPAWN event: ", e);
            }
        });
    }
}
