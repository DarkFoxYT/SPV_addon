package net.dark.spv_addon.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lightweight server-side delayed task scheduler driven by the server tick loop.
 * This replaces blocking Thread.sleep patterns in gameplay events.
 */
public final class ServerTickScheduler {
    private static final Map<Long, List<Runnable>> TASKS = new ConcurrentHashMap<>();
    private static volatile long serverTick = 0L;
    private static boolean registered = false;

    private ServerTickScheduler() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        ServerTickEvents.END_SERVER_TICK.register(ServerTickScheduler::tick);
    }

    public static long getServerTick() {
        return serverTick;
    }

    public static void schedule(int delayTicks, Runnable task) {
        long targetTick = Math.max(0, delayTicks) + serverTick;
        TASKS.computeIfAbsent(targetTick, ignored -> new ArrayList<>()).add(task);
    }

    private static void tick(MinecraftServer server) {
        serverTick++;
        List<Runnable> due = TASKS.remove(serverTick);
        if (due == null || due.isEmpty()) {
            return;
        }

        for (Runnable task : due) {
            try {
                task.run();
            } catch (Exception ignored) {
            }
        }
    }
}

