package net.dark.spv_addon.world.events;

import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

import static com.sp.init.BackroomsLevels.*;
import static net.dark.spv_addon.init.BackroomsLevels.LEVELRUN_WORLD_KEY;

public class LevelRunGlobalTicker {
    private static final boolean IS_DEV = true; // Passe à true pour le mode dev
    private static int globalTimerTicks = -1;
    private static boolean alreadyActivated = false;
    private static final int TICKS_PER_MIN = 20 * 60;
    private static final Set<ServerPlayerEntity> alreadyTeleported = new HashSet<>();
    private static final TagKey<Item> CUTE_TAG = TagKey.of(Registries.ITEM.getKey(), new Identifier("spv_addon", "cute_item"));

    public static void init() {
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(LevelRunGlobalTicker::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        boolean anyPlayerInBackrooms = false;
        Set<ServerPlayerEntity> playersInBackrooms = new HashSet<>();
        Set<ServerPlayerEntity> playersWithCute = new HashSet<>();

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            RegistryKey<World> key = player.getWorld().getRegistryKey();
            if (key.equals(LEVEL0_WORLD_KEY) || key.equals(LEVEL1_WORLD_KEY) || key.equals(LEVEL2_WORLD_KEY) || key.equals(LEVEL324_WORLD_KEY) || key.equals(POOLROOMS_WORLD_KEY) || key.equals(LEVEL324_WORLD_KEY) ) {
                anyPlayerInBackrooms = true;
                playersInBackrooms.add(player);
                if (hasCuteItem(player)) {
                    playersWithCute.add(player);
                }
            }
        }

        if (!alreadyActivated && anyPlayerInBackrooms) {
            int delay;
            if (IS_DEV) {
                delay = TICKS_PER_MIN; // 1 minute en dev
            } else {
                delay = (10 * TICKS_PER_MIN) + new Random().nextInt(11 * TICKS_PER_MIN); // 10-20 minutes
            }
            globalTimerTicks = delay;
            alreadyActivated = true;
            System.out.println("[SPV_ADDON] Global LevelRun timer started for " + (delay / 20) + "s.");
        }

        if (globalTimerTicks > 0) {
            globalTimerTicks--;
            if (globalTimerTicks == 0) {
                System.out.println("[SPV_ADDON] Global LevelRun timer expired! Noclipping...");
                for (ServerPlayerEntity player : playersInBackrooms) {
                    if (playersWithCute.contains(player)) {
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

        // Reset activation if plus aucun joueur dans les backrooms
        if (alreadyActivated && !anyPlayerInBackrooms) {
            alreadyActivated = false;
                globalTimerTicks = -1;
            }
        }

    private static boolean isInAnyBackroomsLevel(ServerPlayerEntity player) {
        // Adapter selon la logique de tes world keys
        RegistryKey<World> key = player.getWorld().getRegistryKey();
        return com.sp.init.BackroomsLevels.BACKROOMS_LEVELS.contains(key)
                && !key.equals(LEVELRUN_WORLD_KEY)
                && !key.equals(BackroomsLevels.LEVEL_KITTY_WORLD_KEY)
                && !key.equals(BackroomsLevels.LEVEL_IKEA_WORLD_KEY);
    }

    private static boolean hasCuteItem(ServerPlayerEntity player) {
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty() && stack.isIn(CUTE_TAG)) {
                return true;
            }
        }
        return false;
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
                player.teleport(kittyWorld, 20, 1, 15, player.getYaw(), player.getPitch());
            }
        });
    }
}