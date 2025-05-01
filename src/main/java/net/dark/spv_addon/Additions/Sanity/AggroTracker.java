package net.dark.spv_addon.Additions.Sanity;

import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** One-tick flag set when a mob starts targeting a player. */
public class AggroTracker {
    private static final Set<ServerPlayerEntity> flagged = ConcurrentHashMap.newKeySet();
    public static void mark(ServerPlayerEntity p)   { flagged.add(p); }
    public static boolean isAgroed(ServerPlayerEntity p) { return flagged.contains(p); }
    public static void clear(ServerPlayerEntity p)  { flagged.remove(p); }
}
