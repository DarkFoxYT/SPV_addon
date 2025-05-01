package net.dark.spv_addon.compat.modmenu;

import java.util.Map;
import java.util.function.Supplier;

/**
 * A central registry of all client‐side config flags (boolean entries) in ConfigStuff.
 * Keyed by UPPER_SNAKE_CASE strings for easy lookup.
 */
public class ConfigDefinitions {
    public static final Map<String, Supplier<Boolean>> definitions = Map.of(
            // HUD toggles
            "BATTERY_HUD",     ConfigDefinitions::isBatteryHudEnabled,
            "THIRST_HUD",      ConfigDefinitions::isThirstHudEnabled,
            "SANITY_HUD",      ConfigDefinitions::isSanityHudEnabled,

            // System toggles
            "THIRST_SYSTEM",   ConfigDefinitions::isThirstEnabled,
            "SANITY_SYSTEM",   ConfigDefinitions::isSanityEnabled,

            // Feature toggles
            "THIRST_EFFECTS",  ConfigDefinitions::isThirstEffectsEnabled,
            "SANITY_REGEN",    ConfigDefinitions::isSanityRegenEnabled,

            // Level/world toggles
            "USE_SKYLIGHT",    ConfigDefinitions::isUseSkylightForDarkness
    );

    // HUD
    public static boolean isBatteryHudEnabled()    { return ConfigStuff.batteryHudEnabled; }
    public static boolean isThirstHudEnabled()     { return ConfigStuff.thirstHudEnabled; }
    public static boolean isSanityHudEnabled()     { return ConfigStuff.sanityHudEnabled; }

    // Core systems
    public static boolean isThirstEnabled()        { return ConfigStuff.thirstEnabled; }
    public static boolean isSanityEnabled()        { return ConfigStuff.sanityEnabled; }

    // Thirst extras
    public static boolean isThirstEffectsEnabled() { return ConfigStuff.thirstEffectsEnabled; }

    // Sanity extras
    public static boolean isSanityRegenEnabled()   { return ConfigStuff.sanityRegenEnabled; }

    // Level/world
    public static boolean isUseSkylightForDarkness() { return ConfigStuff.useSkylightForDarkness; }
}
