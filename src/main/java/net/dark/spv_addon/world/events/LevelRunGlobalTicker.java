package net.dark.spv_addon.world.events;

import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static com.sp.init.BackroomsLevels.LEVEL0_WORLD_KEY;
import static com.sp.init.BackroomsLevels.LEVEL1_WORLD_KEY;
import static net.dark.spv_addon.init.BackroomsLevels.LEVELRUN_WORLD_KEY;

public class LevelRunGlobalTicker {
    private static final boolean IS_DEV = java.lang.management.ManagementFactory.getRuntimeMXBean()
            .getInputArguments()
            .toString()
            .contains("jdwp");
    private static final int TICKS_PER_MIN = 20 * 60;
    private static final Set<ServerPlayerEntity> alreadyTeleported = new HashSet<>();
    private static int globalTimerTicks = -1;
    private static boolean alreadyActivated = false;

    public static void init() {
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(LevelRunGlobalTicker::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        boolean anyPlayerInBackrooms = false;
        Set<ServerPlayerEntity> playersInBackrooms = new HashSet<>();
        ServerPlayerEntity plushieHolder = null;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            RegistryKey<World> key = player.getWorld().getRegistryKey();
            if (key.equals(LEVEL0_WORLD_KEY) || key.equals(LEVEL1_WORLD_KEY)) {
                anyPlayerInBackrooms = true;
                playersInBackrooms.add(player);

                if (plushieHolder == null) {
                    for (int i = 0; i < player.getInventory().size(); i++) {
                        if (player.getInventory().getStack(i).isOf(net.dark.spv_addon.init.ModBlocks.KITTY_PLUSHIE.asItem())) {
                            plushieHolder = player;
                            break;
                        }
                    }
                }
            }
        }

        if (!alreadyActivated && anyPlayerInBackrooms) {
            int delay = IS_DEV ? TICKS_PER_MIN : (5 * TICKS_PER_MIN) + new Random().nextInt(6 * TICKS_PER_MIN);
            globalTimerTicks = delay;
            alreadyActivated = true;
            System.out.println("[SPV_ADDON] Global LevelRun timer started for " + (delay / 20) + "s.");
        }

        if (globalTimerTicks > 0) {
            globalTimerTicks--;
            if (globalTimerTicks == 0) {
                System.out.println("[SPV_ADDON] Global LevelRun timer expired! Noclipping...");
                boolean goKitty = plushieHolder != null;

                // Remove plushie from holder (1 item only)
                if (goKitty) {
                    for (int i = 0; i < plushieHolder.getInventory().size(); i++) {
                        if (plushieHolder.getInventory().getStack(i).isOf(net.dark.spv_addon.init.ModBlocks.KITTY_PLUSHIE1.asItem())) {
                            plushieHolder.getInventory().removeStack(i, 1);
                            break;
                        }
                    }
                }

                for (ServerPlayerEntity player : playersInBackrooms) {
                    if (goKitty) {
                        noclipPlayerToKitty(player);
                        System.out.println("[SPV_ADDON] " + player.getEntityName() + " was sent to LEVEL_KITTY.");
                    } else {
                        noclipPlayerToLevelRun(player);
                        System.out.println("[SPV_ADDON] " + player.getEntityName() + " was sent to LEVEL_RUN.");
                    }
                }

                globalTimerTicks = -1;
                alreadyActivated = false;
            }
        }

        if (alreadyActivated && !anyPlayerInBackrooms) {
            alreadyActivated = false;
            globalTimerTicks = -1;
        }
    }


    private static void noclipPlayerToLevelRun(ServerPlayerEntity player) {
        com.sp.SPBRevamped.sendBlackScreenPacket(player, 60, true, false);
        player.getServer().execute(() -> {
            ServerWorld runWorld = player.getServer().getWorld(LEVELRUN_WORLD_KEY);
            if (runWorld != null) {
                player.teleport(runWorld, 7.5, 1, 7.5, player.getYaw(), player.getPitch());
            }
        });
    }

    private static void noclipPlayerToKitty(ServerPlayerEntity player) {
        com.sp.SPBRevamped.sendBlackScreenPacket(player, 60, true, false);
        player.getServer().execute(() -> {
            ServerWorld kittyWorld = player.getServer().getWorld(BackroomsLevels.LEVEL_KITTY_WORLD_KEY);
            if (kittyWorld != null) {
                player.teleport(kittyWorld, 21, 2, 13, 0, -90);
            }
        });
    }
}