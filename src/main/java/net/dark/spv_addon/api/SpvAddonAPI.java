package net.dark.spv_addon.api;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.*;
import java.util.function.*;

/**
 * SPV-Addon all-in-one modding API — a complete toolkit for modders and power users.
 */
public class SpvAddonAPI {

    // ======== HUD & UI ========

    /** Register custom HUD overlays (icons, progress bars, etc). */
    public static void registerHudOverlay(HudRenderCallback callback) {
        HudRenderCallback.EVENT.register(callback);
    }

    /** Register custom in-game screen or overlay. */
    public static void registerScreen(Identifier id, Supplier<Screen> factory) {
        // Add to your screen registry
    }

    /** Register a progress bar or fill bar for player stats. */
    public static void registerProgressBar(String statId, HudBarSupplier supplier, HudBarStyle style) {
        // E.g. health, stamina, custom bars
    }

    // ======== Player Data/Components ========

    /** Register a new CCA/player data key. */
    public static <T> void registerPlayerComponent(String key, Class<T> type, Supplier<T> factory) {
        // Hook into Cardinal Components API
    }

    /** Read custom player component safely. */
    public static <T> Optional<T> getPlayerComponent(PlayerEntity player, String key, Class<T> type) {
        // Safe getter for CCA/your data
        return Optional.empty();
    }

    /** Sync a custom component. */
    public static <T> void syncPlayerComponent(PlayerEntity player, String key) {
        // Sync logic for CCA or your data system
    }

    // ======== World/Level/Room Generation ========


    /** Register a new structure or room type. */
    public static void registerRoom(String roomId, RoomTemplate template) {
        // Room/structure registry
    }

    /** Register a custom chunk generator for a level. */
    public static void registerChunkGenerator(Identifier id, ChunkGenerator generator) {
        // Add to level/dimension manager
    }

    // ======== Entities, Bosses, Events ========

    /** Register a custom entity or mob. */
    public static void registerEntity(String id, EntityType<?> type, EntityInitHandler handler) {
        // Register entity & initializer
    }

    /** Register a boss fight, event, or unique encounter. */
    public static void registerBossFight(String bossId, BossFightHandler handler) {
        // Registry for unique bosses/events
    }

    /** Register a random in-level event (ambience, static, etc). */
    public static void registerLevelEvent(String levelId, LevelEventHandler handler) {
        // Example: flickering lights, sanity loss, etc.
    }

    // ======== Player Actions & Hooks ========

    /** On noclip event (teleport, static, effect). */
    public static void onPlayerNoclip(BiConsumer<ServerPlayerEntity, World> handler) {
        // Listeners for noclip
    }

    /** On level entry or exit (for cutscenes, stats, etc). */
    public static void onLevelEnter(BiConsumer<ServerPlayerEntity, String> handler) {}
    public static void onLevelExit(BiConsumer<ServerPlayerEntity, String> handler) {}

    /** On suffocation/triggered events (custom backrooms logic). */
    public static void onCustomSuffocate(BiConsumer<PlayerEntity, BlockPos> handler) {}

    // ======== Sound, Music, FX ========

    /** Play a custom sound at player or in world. */
    public static void playSound(World world, BlockPos pos, SoundEvent sound, float volume, float pitch) {}

    /** Register new sound events for use in cutscenes or events. */
    public static void registerSoundEvent(String name, SoundEvent sound) {}

    /** Play a music track or ambiance for a level/event. */
    public static void playMusic(PlayerEntity player, Identifier musicId, int fadeTimeTicks) {}

    // ======== Cutscenes, FX, Shader ========

    /** Register a cutscene (camera, blackout, custom FX, shader pipeline). */
    public static void registerCutscene(String id, CutsceneHandler handler) {}

    /** Play a cutscene for player. */
    public static void playCutscene(PlayerEntity player, String cutsceneId) {}

    /** Register a Veil/GLSL shader effect and trigger. */
    public static void registerShaderEffect(String id, ShaderEffectHandler handler) {}

    /** Trigger a shader for player/camera. */
    public static void triggerShader(PlayerEntity player, String shaderId, float intensity) {}

    // ======== Config, Data, Resource ========

    /** Register a runtime config value. */
    public static <T> void registerConfig(String key, T defaultValue, ConfigChangeHandler<T> onChange) {}

    /** Register a resource or datapack-driven event/room (data-driven modding). */
    public static void registerDataDrivenRoom(String roomId, RoomDataSupplier supplier) {}

    /** Load a loot table or item pool for custom entities/events. */
    public static void registerLootTable(String id, Identifier lootTable) {}

    // ======== Utilities ========

    /** Fade a value (HUD, FX) smoothly. */
    public static float smoothFade(float current, float target, float speed) {
        return current + (target - current) * speed;
    }

    /** Teleport a player to a level, room, or custom pos. */
    public static void teleportPlayer(ServerPlayerEntity player, String levelId, BlockPos pos) {}

    /** Send blackscreen, freeze, or static overlay to player (classic backrooms). */
    public static void showBlackScreen(ServerPlayerEntity player, int durationTicks, boolean noEscape) {}

    /** Show static/TV effect. */
    public static void showStatic(PlayerEntity player, float intensity, int durationTicks) {}

    /** Set custom player state: sanity, thirst, battery, etc. */
    public static void setPlayerStat(PlayerEntity player, String stat, int value) {}

    /** Add/modify player inventory for a scenario/event. */
    public static void giveItem(PlayerEntity player, ItemStack stack) {}

    // ======== Logging, Debug, Network ========

    /** Log to your mod's debug system. */
    public static void log(String message) {
        System.out.println("[SPV-ADDON-API] " + message);
    }

    /** Register a packet listener for addon-to-addon comms. */
    public static void registerPacket(Identifier id, Consumer<PacketByteBuf> handler) {}

    // ======== Interfaces for Extension ========

    @FunctionalInterface public interface HudBarSupplier { float get(PlayerEntity player); }
    @FunctionalInterface public interface HudBarStyle { void render(DrawContext ctx, int x, int y, float value); }
    @FunctionalInterface public interface EntityInitHandler { void setup(Entity entity); }
    @FunctionalInterface public interface BossFightHandler { void start(ServerPlayerEntity player, World world); }
    @FunctionalInterface public interface LevelEventHandler { void trigger(World world, Random random, BlockPos pos); }
    @FunctionalInterface public interface CutsceneHandler { void play(PlayerEntity player); }
    @FunctionalInterface public interface ShaderEffectHandler { void apply(PlayerEntity player, float intensity); }
    @FunctionalInterface public interface ConfigChangeHandler<T> { void onChange(T newValue); }
    @FunctionalInterface public interface RoomTemplate { void build(World world, BlockPos pos, Random random); }
    @FunctionalInterface public interface RoomDataSupplier { RoomTemplate get(World world, BlockPos pos, Random random); }

    // ======== Backrooms/FF Specific Features ========

    /** Register a 'Reality Event' (random static, time loop, hallucination, etc). */
    public static void registerRealityEvent(String id, Consumer<World> event) {}

    /** Toggle or check "no escape" mode for a player (e.g. disables /kill, respawn, escape keys). */
    public static void setNoEscape(ServerPlayerEntity player, boolean active) {}

    /** Register a custom player death handler (e.g. 'You Died in the Backrooms', custom screens). */
    public static void registerPlayerDeathHandler(BiConsumer<PlayerEntity, World> handler) {}

    /** Register a 'level transfer' effect (teleport, fade, custom visuals, etc). */
    public static void registerLevelTransfer(String fromLevel, String toLevel, BiConsumer<ServerPlayerEntity, World> effect) {}

    // ======== Example: Data-Driven Stat Registration ========

    public static void registerDynamicStat(String statId, Function<PlayerEntity, Integer> getter, BiConsumer<PlayerEntity, Integer> setter) {}

    // ======== Extra Utilities ========

    /** Helper: Clamp a value between min and max. */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Helper: Get nearest player to position (for events/entities).
     */
    public static Optional<? extends PlayerEntity> getNearestPlayer(World world, BlockPos pos, double maxDist) {
        return world.getPlayers().stream()
                .filter(p -> p.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < maxDist * maxDist)
                .findFirst();
    }

    /** Helper: Run something on next server/client tick. */
    public static void runNextTick(Runnable r) {
        // Use Fabric's or your mod's scheduler
    }

}
