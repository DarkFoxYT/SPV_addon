package net.dark.spv_addon;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.entity.ik.model.GeckoLib.MowzieModelFactory;
import net.dark.spv_addon.Additions.battery.FlashlightBatteryEvents;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.dark.spv_addon.commands.SpvCommands;
import net.dark.spv_addon.commands.TestCommand;
import net.dark.spv_addon.init.*;
import net.dark.spv_addon.voicechat.SpvAddonVoicechatPlugin;
import net.dark.spv_addon.world.events.LevelRunGlobalTicker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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



        ModBlockEntities.register();
        ThirstManager.register();
        FlashlightBatteryEvents.register();
        ModChunkGenerators.register();
        BackroomsLevels.init();
        ModItems.registerItems();
        ModBlocks.registerModBlocks();
        ModItemGroups.registerItemGroups();
        ModSounds.registerSounds();
        LevelRunGlobalTicker.init();
        TestCommand.clientTick();
        GeckoLibUtil.addCustomBakedModelFactory(MOD_ID, new MowzieModelFactory());
        GeckoLib.initialize();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            SpvCommands.register(dispatcher);
            TestCommand.register(dispatcher);

        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            SpvAddonVoicechatPlugin.justMadeNoise.clear();
            if (SpvAddonVoicechatPlugin.voicechatApi != null) {
                SpvAddonVoicechatPlugin.voicechatApi.getBroadcastRange();
            }
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if(!com.sp.init.BackroomsLevels.isInBackrooms(oldPlayer.getWorld().getRegistryKey())) {
                return;
            }
            try {
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(newPlayer);

                sendBlackScreenPacket(newPlayer, 120, false, false);
                boolean backupInvulnerable = newPlayer.getAbilities().invulnerable;
                newPlayer.getAbilities().invulnerable = true;
                playerComponent.setShouldRender(false);
                playerComponent.sync();

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
                LOGGER.error("Error in AFTER_RESPAWN event: ", e);
            }
        });
    }
}
