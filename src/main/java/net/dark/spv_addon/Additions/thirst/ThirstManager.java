package net.dark.spv_addon.Additions.thirst;

import net.dark.spv_addon.cca.InitializeComponents;
import net.dark.spv_addon.cca.SanityComponent;
import net.dark.spv_addon.cca.ThirstComponent;
import net.dark.spv_addon.init.config.ServerConfig;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.init.CustomDamageSources;
import net.dark.spv_addon.init.ModSounds;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enhanced Thirst System - Comprehensive survival mechanics
 * Features environmental effects, progressive symptoms, and dynamic gameplay
 */
public class ThirstManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("ThirstManager");

    // Enhanced timing system - slower drain as requested
    private static final int INTERVAL_TICKS = 20 * 10; // 10 seconds (slower drain)
    private static final int FAST_INTERVAL_TICKS = 20 * 5; // 5 seconds for critical states

    public static boolean enabled = true;
    private static int tickCounter = 0;
    private static final Random random = Random.create();

    // Thirst level thresholds
    public static final int THIRST_PERFECT = 100;
    public static final int THIRST_GOOD = 80;
    public static final int THIRST_MODERATE = 60;
    public static final int THIRST_LOW = 40;
    public static final int THIRST_CRITICAL = 20;
    public static final int THIRST_DANGEROUS = 10;
    public static final int THIRST_DYING = 0;

    // Environmental multipliers
    private static final float HOT_BIOME_MULTIPLIER = 2.0f;
    private static final float COLD_BIOME_MULTIPLIER = 0.7f;
    private static final float POOLROOMS_MULTIPLIER = 1.5f; // Humid but dehydrating
    private static final float LEVEL188_MULTIPLIER = 0.8f; // Cooler environment

    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(ThirstManager::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        // Check if thirst system is enabled via server config/gamerules
        if (!enabled || !ServerConfig.isThirstSystemEnabled(server)) return;

        tickCounter++;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ThirstComponent comp = InitializeComponents.THIRST.get(player);
            int thirst = comp.getThirst();

            // Use faster ticking for critical thirst levels
            int requiredTicks = (thirst <= THIRST_CRITICAL) ? FAST_INTERVAL_TICKS : INTERVAL_TICKS;

            if (tickCounter >= requiredTicks) {
                tickPlayer(player, server);
            }
        }

        // Reset counter when reaching the standard interval
        if (tickCounter >= INTERVAL_TICKS) {
            tickCounter = 0;
        }
    }

    /**
     * Enhanced player thirst processing with environmental effects and progressive symptoms
     */
    private static void tickPlayer(ServerPlayerEntity player, MinecraftServer server) {
        try {
            ThirstComponent comp = InitializeComponents.THIRST.get(player);
            SanityComponent sanityComp = InitializeComponents.SANITY.get(player);
            int currentThirst = comp.getThirst();

            // Calculate thirst drain based on multiple factors
            float drainAmount = calculateThirstDrain(player) * ServerConfig.getThirstDrainRate(server);

            // Apply environmental multipliers (using default 1.5f multiplier)
            drainAmount *= getEnvironmentalMultiplier(player) * 1.5f;

            // Apply the drain
            int newThirst = MathHelper.clamp(currentThirst - Math.round(drainAmount), 0, 100);
            comp.setThirst(newThirst);

            // Apply progressive effects based on thirst level
            applyThirstEffects(player, newThirst, currentThirst, server);

            // Handle thirst level changes
            if (newThirst != currentThirst) {
                onThirstLevelChanged(player, currentThirst, newThirst);
            }

        } catch (Exception e) {
            LOGGER.warn("Error processing thirst for player {}: {}", player.getName().getString(), e.getMessage());
        }
    }

    /**
     * Calculate base thirst drain based on player activity - only drains when moving
     */
    private static float calculateThirstDrain(ServerPlayerEntity player) {
        // Check if player is moving
        Vec3d velocity = player.getVelocity();
        boolean isMoving = velocity.lengthSquared() > 0.01; // Small threshold to account for minor movements

        // No drain if not moving (standing still)
        if (!isMoving) {
            return 0.0f;
        }

        float baseDrain = 0.5f; // Reduced base drain

        // Activity-based drain - only when moving
        if (player.isSprinting()) {
            baseDrain += 1.0f; // Reduced from 2.0f
        } else if (player.isSwimming()) {
            baseDrain += 0.8f; // Reduced from 1.5f
        } else if (player.isSneaking()) {
            baseDrain += 0.3f; // Reduced from 0.5f
        } else {
            baseDrain += 0.2f; // Small drain for walking
        }

        // Health-based drain (reduced)
        if (player.getHealth() < player.getMaxHealth() * 0.5f) {
            baseDrain += 0.3f; // Reduced from 1.0f
        }

        // Status effect modifiers (reduced)
        if (player.hasStatusEffect(StatusEffects.POISON)) {
            baseDrain += 0.5f; // Reduced from 2.0f
        }
        if (player.hasStatusEffect(StatusEffects.HUNGER)) {
            baseDrain += 0.3f; // Reduced from 1.0f
        }
        if (player.hasStatusEffect(StatusEffects.REGENERATION)) {
            baseDrain += 0.2f; // Reduced from 0.5f
        }

        return baseDrain;
    }

    /**
     * Get environmental multiplier based on dimension and biome
     */
    private static float getEnvironmentalMultiplier(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        // Dimension-based multipliers
        if (world.getRegistryKey().getValue().equals(com.sp.init.BackroomsLevels.POOLROOMS_WORLD_KEY.getValue())) {
            return POOLROOMS_MULTIPLIER; // Humid but still dehydrating
        } else if (world.getRegistryKey().getValue().equals(BackroomsLevels.LEVEL188_WORLD_KEY.getValue())) {
            return LEVEL188_MULTIPLIER; // Cooler environment
        }

        // Biome-based multipliers for overworld
        BlockPos pos = player.getBlockPos();
        Biome biome = world.getBiome(pos).value();

        if (biome.getTemperature() > 1.5f) {
            return HOT_BIOME_MULTIPLIER; // Hot biomes (desert, nether-like)
        } else if (biome.getTemperature() < 0.2f) {
            return COLD_BIOME_MULTIPLIER; // Cold biomes (tundra, ice)
        }

        return 1.0f; // Normal multiplier
    }

    /**
     * Apply progressive thirst effects based on current thirst level
     */
    private static void applyThirstEffects(ServerPlayerEntity player, int thirst, int previousThirst, MinecraftServer server) {
        SanityComponent sanityComp = InitializeComponents.SANITY.get(player);

        // Clear previous thirst effects first
        clearThirstEffects(player);

        if (thirst <= THIRST_DYING) {
            // 0% - Dying of thirst
            applyDyingEffects(player, sanityComp, server);
        } else if (thirst <= THIRST_DANGEROUS) {
            // 1-10% - Dangerous dehydration
            applyDangerousEffects(player, sanityComp, server);
        } else if (thirst <= THIRST_CRITICAL) {
            // 11-20% - Critical dehydration
            applyCriticalEffects(player, sanityComp);
        } else if (thirst <= THIRST_LOW) {
            // 21-40% - Low hydration
            applyLowEffects(player, sanityComp);
        } else if (thirst <= THIRST_MODERATE) {
            // 41-60% - Moderate thirst
            applyModerateEffects(player, sanityComp);
        }
        // Above 60% - No negative effects
    }

    /**
     * Apply dying of thirst effects (0% thirst)
     */
    private static void applyDyingEffects(ServerPlayerEntity player, SanityComponent sanityComp, MinecraftServer server) {
        // Severe status effects
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, FAST_INTERVAL_TICKS + 20, 3, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, FAST_INTERVAL_TICKS + 20, 2, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, FAST_INTERVAL_TICKS + 20, 2, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, FAST_INTERVAL_TICKS + 20, 1, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0, true, false)); // Brief blindness

        // Severe sanity loss
        if (random.nextFloat() < 0.3f) {
            sanityComp.decreaseSanity(8);
        }

        // Damage from dehydration
        RegistryEntry<DamageType> entry = player.getWorld()
                .getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .entryOf(CustomDamageSources.THIRST_DAMAGE_ID);
        player.damage(new DamageSource(entry), 6.0f);

        // Visual and audio effects
        spawnDehydrationParticles(player, 15);
        playDehydrationSound(player, "critical");

        // Warning message
        if (random.nextFloat() < 0.1f) {
            player.sendMessage(Text.literal("You are dying of thirst!").formatted(Formatting.DARK_RED), true);
        }
    }

    /**
     * Apply dangerous dehydration effects (1-10% thirst)
     */
    private static void applyDangerousEffects(ServerPlayerEntity player, SanityComponent sanityComp, MinecraftServer server) {
        // Strong status effects
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, FAST_INTERVAL_TICKS + 20, 2, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, FAST_INTERVAL_TICKS + 20, 1, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, FAST_INTERVAL_TICKS + 20, 1, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 0, true, false));

        // Sanity loss
        if (random.nextFloat() < 0.2f) {
            sanityComp.decreaseSanity(5);
        }

        // Configurable damage
        if (ServerConfig.isThirstDamageEnabled(server) && random.nextFloat() < 0.2f) {
            RegistryEntry<DamageType> entry = player.getWorld()
                    .getRegistryManager()
                    .get(RegistryKeys.DAMAGE_TYPE)
                    .entryOf(CustomDamageSources.THIRST_DAMAGE_ID);
            player.damage(new DamageSource(entry), 1.5f); // Default damage amount
        }

        // Visual effects
        spawnDehydrationParticles(player, 8);
        playDehydrationSound(player, "dangerous");

        // Warning message
        if (random.nextFloat() < 0.05f) {
            player.sendMessage(Text.literal("Severe dehydration!").formatted(Formatting.RED), true);
        }
    }

    /**
     * Apply critical dehydration effects (11-20% thirst) - More progressive around 20%
     */
    private static void applyCriticalEffects(ServerPlayerEntity player, SanityComponent sanityComp) {
        int currentThirst = InitializeComponents.THIRST.get(player).getThirst();

        // Progressive effects based on how close to 20% we are
        float severity = (20 - currentThirst) / 9.0f; // 0.0 at 20%, 1.0 at 11%

        // Moderate status effects - intensity based on severity
        if (severity > 0.3f) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, INTERVAL_TICKS + 20, 0, true, false));
        }
        if (severity > 0.5f) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, INTERVAL_TICKS + 20, 0, true, false));
        }
        if (severity > 0.7f) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, INTERVAL_TICKS + 20, 0, true, false));
        }

        // Occasional nausea - only at higher severity
        if (severity > 0.6f && random.nextFloat() < 0.2f) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 60, 0, true, false));
        }

        // Reduced sanity loss
        if (random.nextFloat() < 0.1f * severity) {
            sanityComp.decreaseSanity(2);
        }

        // Visual effects based on severity
        spawnDehydrationParticles(player, Math.round(3 + severity * 2));
        if (severity > 0.5f) {
            playDehydrationSound(player, "critical");
        }

        // Warning message - less frequent
        if (random.nextFloat() < 0.02f) {
            player.sendMessage(Text.literal("You are getting dehydrated").formatted(Formatting.YELLOW), true);
        }
    }

    /**
     * Apply low hydration effects (21-40% thirst)
     */
    private static void applyLowEffects(ServerPlayerEntity player, SanityComponent sanityComp) {
        // Mild status effects
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, INTERVAL_TICKS + 20, 0, true, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, INTERVAL_TICKS + 20, 0, true, false));

        // Occasional sanity loss
        if (random.nextFloat() < 0.1f) {
            sanityComp.decreaseSanity(2);
        }

        // Subtle visual effects
        spawnDehydrationParticles(player, 3);
        playDehydrationSound(player, "low");

        // Subtle warning
        if (random.nextFloat() < 0.02f) {
            player.sendMessage(Text.literal("You feel thirsty").formatted(Formatting.YELLOW), true);
        }
    }

    /**
     * Apply moderate thirst effects (41-60% thirst)
     */
    private static void applyModerateEffects(ServerPlayerEntity player, SanityComponent sanityComp) {
        // Very mild effects
        if (random.nextFloat() < 0.05f) {
            sanityComp.decreaseSanity(1);
        }

        // Minimal visual effects
        if (random.nextFloat() < 0.3f) {
            spawnDehydrationParticles(player, 1);
        }

        // Subtle hint
        if (random.nextFloat() < 0.01f) {
            player.sendMessage(Text.literal("You could use some water").formatted(Formatting.GRAY), true);
        }
    }

    /**
     * Clear all thirst-related status effects
     */
    private static void clearThirstEffects(ServerPlayerEntity player) {
        // Remove thirst-related effects (but not others)
        // This is a simplified approach - in a full implementation you'd track which effects are from thirst
    }

    /**
     * Spawn dehydration particles around the player
     */
    private static void spawnDehydrationParticles(ServerPlayerEntity player, int count) {
        ServerWorld world = player.getServerWorld();

        for (int i = 0; i < count; i++) {
            double x = player.getX() + (random.nextDouble() - 0.5) * 2.0;
            double y = player.getY() + random.nextDouble() * 2.0;
            double z = player.getZ() + (random.nextDouble() - 0.5) * 2.0;

            world.spawnParticles(ParticleTypes.FALLING_WATER, x, y, z, 1, 0.1, 0.1, 0.1, 0.0);
        }
    }

    /**
     * Play dehydration sound effects
     */
    private static void playDehydrationSound(ServerPlayerEntity player, String severity) {
        try {
            switch (severity) {
                case "critical":
                case "dangerous":
                    player.getServerWorld().playSound(null, player.getBlockPos(),
                        ModSounds.SANITY_BREATHING, SoundCategory.PLAYERS, 0.5f, 0.8f);
                    break;
                case "low":
                    if (random.nextFloat() < 0.3f) {
                        player.getServerWorld().playSound(null, player.getBlockPos(),
                            ModSounds.SANITY_BREATHING, SoundCategory.PLAYERS, 0.3f, 1.0f);
                    }
                    break;
            }
        } catch (Exception e) {
            // Silently handle sound errors
        }
    }

    /**
     * Handle thirst level changes for special effects
     */
    private static void onThirstLevelChanged(ServerPlayerEntity player, int oldThirst, int newThirst) {
        // Trigger special effects when crossing thresholds
        if (newThirst <= THIRST_DYING && oldThirst > THIRST_DYING) {
            // Entering dying state
            player.sendMessage(Text.literal("You are dying of thirst!").formatted(Formatting.DARK_RED), false);
        } else if (newThirst <= THIRST_DANGEROUS && oldThirst > THIRST_DANGEROUS) {
            // Entering dangerous state
            player.sendMessage(Text.literal("Severe dehydration!").formatted(Formatting.RED), false);
        } else if (newThirst <= THIRST_CRITICAL && oldThirst > THIRST_CRITICAL) {
            // Entering critical state
            player.sendMessage(Text.literal("Critical dehydration").formatted(Formatting.GOLD), false);
        }
    }

    /**
     * Get thirst status text for UI
     */
    public static String getThirstStatusText(int thirst) {
        if (thirst <= THIRST_DYING) {
            return "DYING";
        } else if (thirst <= THIRST_DANGEROUS) {
            return "SEVERE";
        } else if (thirst <= THIRST_CRITICAL) {
            return "CRITICAL";
        } else if (thirst <= THIRST_LOW) {
            return "LOW";
        } else if (thirst <= THIRST_MODERATE) {
            return "THIRSTY";
        } else {
            return "HYDRATED";
        }
    }

    /**
     * Get thirst color for UI based on level
     */
    public static int getThirstColor(int thirst) {
        if (thirst <= THIRST_DYING) {
            return 0x8B0000; // Dark red
        } else if (thirst <= THIRST_DANGEROUS) {
            return 0xFF0000; // Red
        } else if (thirst <= THIRST_CRITICAL) {
            return 0xFF4500; // Orange red
        } else if (thirst <= THIRST_LOW) {
            return 0xFFA500; // Orange
        } else if (thirst <= THIRST_MODERATE) {
            return 0xFFD700; // Gold
        } else {
            return 0x00BFFF; // Deep sky blue
        }
    }

    /**
     * Enhanced thirst restoration with overflow protection
     */
    public static void restoreThirst(ServerPlayerEntity player, int amount, boolean showMessage) {
        ThirstComponent comp = InitializeComponents.THIRST.get(player);
        int currentThirst = comp.getThirst();
        int newThirst = MathHelper.clamp(currentThirst + amount, 0, 100);
        comp.setThirst(newThirst);

        if (showMessage && amount > 0) {
            String message = amount >= 50 ? "Fully hydrated!" :
                           amount >= 25 ? "Much better!" :
                           amount >= 10 ? "Refreshing!" : "A little better";
            player.sendMessage(Text.literal(message).formatted(Formatting.AQUA), true);
        }

        // Remove negative effects if thirst is restored significantly
        if (newThirst >= THIRST_MODERATE && currentThirst < THIRST_LOW) {
            player.removeStatusEffect(StatusEffects.SLOWNESS);
            player.removeStatusEffect(StatusEffects.WEAKNESS);
            player.removeStatusEffect(StatusEffects.MINING_FATIGUE);
            player.removeStatusEffect(StatusEffects.NAUSEA);
        }
    }

    public static void setThirst(ServerPlayerEntity player, int value) {
        InitializeComponents.THIRST.get(player).setThirst(MathHelper.clamp(value, 0, 100));
    }

    public static void increaseThirst(PlayerEntity player, int value) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            restoreThirst(serverPlayer, value, true);
        } else {
            InitializeComponents.THIRST.get(player).addThirst(value);
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        ThirstManager.enabled = enabled;
    }
}
