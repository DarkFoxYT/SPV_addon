package net.dark.spv_addon.Additions.thirst;

import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.ThirstComponent;
import net.dark.spv_addon.init.CustomDamageSources;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

public class ThirstManager {
    public static boolean enabled = true;
    private static final int INTERVAL_TICKS = 20 * 10;
    private static int tickCounter = 0;

    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(ThirstManager::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        if (!enabled || ++tickCounter < INTERVAL_TICKS) return;
        tickCounter = 0;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            tickPlayer(player);
        }
    }

    private static void tickPlayer(ServerPlayerEntity player) {
        ThirstComponent comp = InitializeComponents.THIRST.get(player);
        int thirst = comp.getThirst();

        int drain = player.isSprinting() ? 2 : player.isSprinting() ? 1 : 0;
        thirst = MathHelper.clamp(thirst - drain, 0, 100);
        comp.setThirst(thirst);

        if (thirst <= 25) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, INTERVAL_TICKS, 1, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, INTERVAL_TICKS, 0, true, false));
            if (tickCounter % (20 * 5) == 0) InitializeComponents.SANITY.get(player).decreaseSanity(1);
        }

        if (thirst == 0) {
            RegistryEntry<DamageType> entry = player.getWorld()
                    .getRegistryManager()
                    .get(RegistryKeys.DAMAGE_TYPE)
                    .entryOf(CustomDamageSources.THIRST_DAMAGE_ID);

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, INTERVAL_TICKS, 1, true, false));

            if (tickCounter % (20 * 5) == 0) InitializeComponents.SANITY.get(player).decreaseSanity(5);
            player.damage(new DamageSource(entry), 4.0f);
        }

    }
    public static void setThirst(ServerPlayerEntity player, int value) {
        InitializeComponents.THIRST.get(player).setThirst(value);
    }

    public static void increaseThirst(PlayerEntity player, int value) {
        InitializeComponents.THIRST.get(player).addThirst(value);
    }
}
