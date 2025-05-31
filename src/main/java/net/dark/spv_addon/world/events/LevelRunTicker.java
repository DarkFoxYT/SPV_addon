package net.dark.spv_addon.world.events;

import com.sp.SPBRevamped;
import com.sp.SPBRevampedClient;
import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.run.RunChunkGenerator;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import java.util.Random;

public class LevelRunTicker {

    // Remplace par l’ID réel de l’item qui doit TP vers Kitty !
    private static final Identifier ITEM_SPV_KITTY = new Identifier("spv_addon", "kitty_plushie");

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(LevelRunTicker::onTick);
    }

    private static void onTick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            // On ignore si déjà dans level run ou kitty
            if (isInLevelRun(player) || isInKitty(player)) continue;

            // Si dans les backrooms custom
            if (isInBackrooms(player)) {
                // Tick le timer
                InitializeComponents.RUN_TIMER.get(player).tick();

                // Si timer fini...
                if (InitializeComponents.RUN_TIMER.get(player).shouldNoclip()) {
                    if (hasKittyItem(player)) {
                        noclipPlayerToKitty(player);
                    } else {
                        noclipPlayerToLevelRun(player);
                    }
                    InitializeComponents.RUN_TIMER.get(player).reset();
                }

            } else {
                // Réactive le timer dès qu’il entre dans les backrooms (1 seule fois)
                if (!InitializeComponents.RUN_TIMER.get(player).isActive()) {
                    int delay = 12000 + new Random().nextInt(12000); // 10-20min
                    InitializeComponents.RUN_TIMER.get(player).activate(delay, delay);
                }
            }
        }

        // Gestion de la sortie de Level Run (inchangée)
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (isInLevelRun(player)) {
                if (RunChunkGenerator.isPlayerInExit(player)) {
                    exitPlayerFromLevelRun(player);
                }
            }
        }
    }

    private static boolean isInBackrooms(ServerPlayerEntity player) {

        return SPBRevampedClient.isInBackrooms();
    }

    private static boolean isInLevelRun(ServerPlayerEntity player) {
        return player.getWorld().getRegistryKey().equals(BackroomsLevels.LEVELRUN_WORLD_KEY);
    }

    private static boolean isInKitty(ServerPlayerEntity player) {
        return player.getWorld().getRegistryKey().equals(BackroomsLevels.LEVEL_KITTY_WORLD_KEY);
    }

    private static boolean hasKittyItem(ServerPlayerEntity player) {
        for (ItemStack stack : player.getInventory().main) {
            if (!stack.isEmpty() && stack.getItem().getName().equals(ITEM_SPV_KITTY)) {
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
