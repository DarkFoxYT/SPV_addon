package net.dark.spv_addon.world.events;

import com.sp.SPBRevamped;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.run.RunChunkGenerator;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class LevelRunGlobalTicker {

    // Timer global pour tous les joueurs
    private static int globalTimerTicks = -1; // -1 = inactif
    private static boolean alreadyActivated = false;
    private static final int TICKS_PER_MIN = 20 * 60;

    // Pour suivre les joueurs déjà transférés (empêcher re-tp)
    private static final Set<ServerPlayerEntity> alreadyTeleported = new HashSet<>();

    // Tag Fabric
    private static final TagKey<Item> CUTE_TAG = TagKey.of(Registries.ITEM.getKey(), new Identifier("spv_addon", "cute_item"));

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(LevelRunGlobalTicker::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        boolean anyPlayerInBackrooms = false;

        // On scanne tous les joueurs
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (isInBackrooms(player)) {
                anyPlayerInBackrooms = true;
                break;
            }
        }

        // Si jamais activé, ne jamais réarmer
        if (!alreadyActivated && anyPlayerInBackrooms) {
            // 10 à 20 min en ticks
            int delay = (10 * TICKS_PER_MIN) + new Random().nextInt(11 * TICKS_PER_MIN);
            globalTimerTicks = delay;
            alreadyActivated = true;
            System.out.println("[SPV_ADDON] Global LevelRun timer started for " + (delay / 20) + "s.");
        }

        // Timer jamais relancé, une seule fois
        if (globalTimerTicks > 0) {
            globalTimerTicks--;
            if (globalTimerTicks == 0) {
                System.out.println("[SPV_ADDON] Global LevelRun timer expired! Noclipping...");
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    // Do not TP if not in backrooms, or already teleported (should only ever run once, but safety check)
                    if (!isInBackrooms(player) || alreadyTeleported.contains(player)) continue;

                    if (hasCuteItem(player)) {
                        noclipPlayerToKitty(player);
                        System.out.println("[SPV_ADDON] " + player.getEntityName() + " was sent to LEVEL_KITTY.");
                    } else {
                        noclipPlayerToLevelRun(player);
                        System.out.println("[SPV_ADDON] " + player.getEntityName() + " was sent to LEVEL_RUN.");
                    }
                    alreadyTeleported.add(player);
                }
                // Ne jamais réarmer, même si on reload le serveur. Pour relancer, il faut /reload ou redémarrer la JVM.
                globalTimerTicks = -1;
            }
        }

        // Level Run Exit Logic (comme avant)
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (isInLevelRun(player) && RunChunkGenerator.isPlayerInExit(player)) {
                exitPlayerFromLevelRun(player);
            }
        }
    }

    private static boolean isInBackrooms(ServerPlayerEntity player) {
        // On veut tout, sauf Level Run, Level Kitty, Level IKEA, etc. Donc à adapter à ta logique.
        return player.getWorld().getRegistryKey().equals(com.sp.init.BackroomsLevels.BACKROOMS_LEVELS);
    }

    private static boolean isInLevelRun(ServerPlayerEntity player) {
        return player.getWorld().getRegistryKey().equals(BackroomsLevels.LEVELRUN_WORLD_KEY);
    }

    private static boolean isInKitty(ServerPlayerEntity player) {
        return player.getWorld().getRegistryKey().equals(BackroomsLevels.LEVEL_KITTY_WORLD_KEY);
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
        SPBRevamped.sendBlackScreenPacket(player, 60, true, false);
        player.getServer().execute(() -> {
            MinecraftServer server = player.getServer();
            ServerWorld runWorld = server.getWorld(BackroomsLevels.LEVELRUN_WORLD_KEY);
            if (runWorld != null) {
                player.teleport(runWorld, 7.5, 1, 7.5, player.getYaw(), player.getPitch());
            }
        });
    }

    private static void noclipPlayerToKitty(ServerPlayerEntity player) {
        SPBRevamped.sendBlackScreenPacket(player, 60, true, false);
        player.getServer().execute(() -> {
            MinecraftServer server = player.getServer();
            ServerWorld kittyWorld = server.getWorld(BackroomsLevels.LEVEL_KITTY_WORLD_KEY);
            if (kittyWorld != null) {
                player.teleport(kittyWorld, 7.5, 1, 7.5, player.getYaw(), player.getPitch());
            }
        });
    }

    private static void exitPlayerFromLevelRun(ServerPlayerEntity player) {
        SPBRevamped.sendBlackScreenPacket(player, 60, true, false);
        player.getServer().execute(() -> {
            MinecraftServer server = player.getServer();
            ServerWorld exitWorld = server.getWorld(BackroomsLevels.LEVEL_IKEA_WORLD_KEY);
            if (exitWorld != null) {
                player.teleport(exitWorld, 7.5, 1, 7.5, player.getYaw(), player.getPitch());
            }
        });
    }
}
