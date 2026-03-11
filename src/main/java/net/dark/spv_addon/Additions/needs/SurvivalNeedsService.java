package net.dark.spv_addon.Additions.needs;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import net.dark.spv_addon.Additions.battery.BatteryManager;
import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.cca.ThirstComponent;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.init.CustomDamageSources;
import net.dark.spv_addon.init.config.ServerConfig;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Centralized survival needs runtime:
 * - Sanity
 * - Thirst
 * - Battery
 */
public final class SurvivalNeedsService {
    private static final TagKey<Block> SANITY_LIGHT_TAG =
            TagKey.of(Registries.BLOCK.getKey(), new Identifier("spv_addon", "sanity_lights"));
    private static final BooleanProperty RED_LIGHT = BooleanProperty.of("red_light");
    private static final BooleanProperty STOPPED = BooleanProperty.of("stopped");

    private static final int THIRST_INTERVAL = 100;
    private static final int SANITY_INTERVAL = 120;
    private static final int BATTERY_INTERVAL = 100;

    private static final List<BlockPos> SANITY_LIGHT_OFFSETS = buildOffsets(10, 4, 10);
    private static volatile boolean registered = false;

    private SurvivalNeedsService() {
    }

    public static boolean isCentralizedMode() {
        return true;
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        ServerTickEvents.END_SERVER_TICK.register(SurvivalNeedsService::tick);
    }

    private static void tick(MinecraftServer server) {
        long serverTick = server.getOverworld().getTime();
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (shouldRun(serverTick, player, THIRST_INTERVAL)) {
                tickThirst(player, server);
            }
            if (shouldRun(serverTick, player, SANITY_INTERVAL)) {
                tickSanity(player, server);
            }
            if (shouldRun(serverTick, player, BATTERY_INTERVAL)) {
                tickBattery(player, server);
            }
        }
    }

    private static boolean shouldRun(long serverTick, ServerPlayerEntity player, int interval) {
        int phase = Math.floorMod(player.getUuid().hashCode(), interval);
        return serverTick % interval == phase;
    }

    private static void tickBattery(ServerPlayerEntity player, MinecraftServer server) {
        if (!ServerConfig.isBatterySystemEnabled(server)) {
            return;
        }
        PlayerComponent component = InitializeComponents.PLAYER.get(player);
        if (component != null && component.isFlashLightOn()) {
            BatteryManager.drainBattery(player, 1, server);
        }
    }

    private static void tickThirst(ServerPlayerEntity player, MinecraftServer server) {
        if (!ServerConfig.isThirstSystemEnabled(server)) {
            return;
        }

        ThirstComponent thirstComponent = net.dark.spv_addon.cca.InitializeComponents.THIRST.get(player);
        SanityComponent sanityComponent = net.dark.spv_addon.cca.InitializeComponents.SANITY.get(player);

        float drain = 0.20f;
        Vec3d velocity = player.getVelocity();
        boolean moving = velocity.horizontalLengthSquared() > 0.01;
        if (!moving) {
            if (beforeCriticalThirst(player) && ServerConfig.isThirstDamageEnabled(server) && player.getRandom().nextFloat() < 0.12f) {
                RegistryEntry<DamageType> entry = player.getWorld()
                        .getRegistryManager()
                        .get(RegistryKeys.DAMAGE_TYPE)
                        .entryOf(CustomDamageSources.THIRST_DAMAGE_ID);
                player.damage(new DamageSource(entry), 1.0f);
            }
            return;
        }

        if (player.isSprinting()) drain += 0.35f;
        else if (player.isSwimming()) drain += 0.22f;
        else drain += 0.12f;
        if (player.hasStatusEffect(StatusEffects.HUNGER)) drain += 0.10f;
        if (player.hasStatusEffect(StatusEffects.POISON)) drain += 0.16f;
        if (player.getHealth() < player.getMaxHealth() * 0.45f) drain += 0.10f;

        drain *= ServerConfig.getThirstDrainRate(server);
        if (player.getServerWorld().getRegistryKey().equals(BackroomsLevels.LEVEL188_WORLD_KEY)) {
            drain *= 0.85f;
        } else if (player.getServerWorld().getRegistryKey().equals(com.sp.init.BackroomsLevels.POOLROOMS_WORLD_KEY)) {
            drain *= 1.25f;
        }

        int before = thirstComponent.getThirst();
        int after = MathHelper.clamp(before - Math.round(drain), 0, 100);
        thirstComponent.setThirst(after);

        if (after <= 25) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, THIRST_INTERVAL + 10, 0, true, false));
            if (after <= 12) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, THIRST_INTERVAL + 10, 0, true, false));
            }
            if (after <= 6 && ServerConfig.isThirstDamageEnabled(server)) {
                RegistryEntry<DamageType> entry = player.getWorld()
                        .getRegistryManager()
                        .get(RegistryKeys.DAMAGE_TYPE)
                        .entryOf(CustomDamageSources.THIRST_DAMAGE_ID);
                player.damage(new DamageSource(entry), 1.5f);
            }
        }

        // Cross-system pressure: dehydration accelerates sanity collapse.
        if (after <= 35 && player.getRandom().nextFloat() < 0.35f) {
            sanityComponent.decreaseSanity(1);
        }
    }

    private static void tickSanity(ServerPlayerEntity player, MinecraftServer server) {
        if (!ServerConfig.isSanitySystemEnabled(server)) {
            return;
        }
        if (player.getServerWorld().getRegistryKey().equals(com.sp.init.BackroomsLevels.POOLROOMS_WORLD_KEY)
                || player.getServerWorld().getRegistryKey().equals(BackroomsLevels.LEVEL207_WORLD_KEY)
                || player.getServerWorld().getRegistryKey().equals(BackroomsLevels.LEVEL_KITTY_WORLD_KEY)
                || player.getServerWorld().getRegistryKey().equals(com.sp.init.BackroomsLevels.INFINITE_FIELD_WORLD_KEY)) {
            return;
        }

        SanityComponent sanityComponent = net.dark.spv_addon.cca.InitializeComponents.SANITY.get(player);
        boolean nearSafeLight = isNearActiveSanityLight(player, 10);
        float drain = ServerConfig.getSanityDrainRate(server);

        if (nearSafeLight) {
            if (player.age % 240 == 0) {
                sanityComponent.increaseSanity(1);
            }
            return;
        }

        sanityComponent.decreaseSanity(Math.max(1, Math.round(drain)));
    }

    private static boolean isNearActiveSanityLight(ServerPlayerEntity player, int range) {
        BlockPos origin = player.getBlockPos();
        for (BlockPos offset : SANITY_LIGHT_OFFSETS) {
            if (offset.getSquaredDistance(BlockPos.ORIGIN) > range * range) {
                continue;
            }
            BlockPos pos = origin.add(offset);
            BlockState state = player.getServerWorld().getBlockState(pos);
            if (!state.isIn(SANITY_LIGHT_TAG)) {
                continue;
            }

            boolean active = true;
            if (state.contains(RED_LIGHT)) {
                active = state.get(RED_LIGHT);
            } else if (state.contains(STOPPED)) {
                active = state.get(STOPPED);
            }

            if (active) {
                return true;
            }
        }
        return false;
    }

    private static List<BlockPos> buildOffsets(int rx, int ry, int rz) {
        List<BlockPos> offsets = new ArrayList<>();
        for (int x = -rx; x <= rx; x++) {
            for (int y = -ry; y <= ry; y++) {
                for (int z = -rz; z <= rz; z++) {
                    offsets.add(new BlockPos(x, y, z));
                }
            }
        }
        offsets.sort((a, b) -> Double.compare(a.getSquaredDistance(BlockPos.ORIGIN), b.getSquaredDistance(BlockPos.ORIGIN)));
        return offsets;
    }

    private static boolean beforeCriticalThirst(ServerPlayerEntity player) {
        ThirstComponent thirstComponent = net.dark.spv_addon.cca.InitializeComponents.THIRST.get(player);
        return thirstComponent.getThirst() <= 6;
    }
}
