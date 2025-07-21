package net.dark.spv_addon.init;

import net.dark.spv_addon.Spv_addon;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent BELLWALKER_BELL = register("entity.bellwalker.bell");
    public static final SoundEvent DEATH_SOUND = register("death_sound");
    public static final SoundEvent BONK = register("stuff.bonk");
    public static final SoundEvent LEVEL_207_AMBIANCE = register("stuff.207_ambiance");
    public static final SoundEvent BELLWALKER_CARP = register("entity.bellwalker.carp");
    public static final SoundEvent DONG = register("stuff.belldong");
    public static final SoundEvent ELEV = register("stuff.elevator");
    public static final SoundEvent IC1 = register("stuff.intercom_custom1");
    public static final SoundEvent IC2 = register("stuff.intercom_custom2");


    public static final SoundEvent TAPE1 = register("tapes.tape1");
    public static final SoundEvent TAPE2 = register("tapes.tape2");

    // Sanity Sound Events
    public static final SoundEvent SANITY_WHISPER_1 = register("sanity.whisper_1");
    public static final SoundEvent SANITY_WHISPER_2 = register("sanity.whisper_2");
    public static final SoundEvent SANITY_WHISPER_3 = register("sanity.whisper_3");
    public static final SoundEvent SANITY_AMBIENT_LOW = register("sanity.ambient_low");
    public static final SoundEvent SANITY_AMBIENT_CRITICAL = register("sanity.ambient_critical");
    public static final SoundEvent SANITY_AMBIENT_NIGHTMARE = register("sanity.ambient_nightmare");
    public static final SoundEvent SANITY_HEARTBEAT = register("sanity.heartbeat");
    public static final SoundEvent SANITY_BREATHING = register("sanity.breathing");
    public static final SoundEvent SANITY_STATIC = register("sanity.static");
    public static final SoundEvent SANITY_FOOTSTEPS_PHANTOM = register("sanity.footsteps_phantom");
    public static final SoundEvent SANITY_TRANSITION_CRITICAL = register("sanity.transition_critical");
    public static final SoundEvent SANITY_TRANSITION_NIGHTMARE = register("sanity.transition_nightmare");

    private static SoundEvent register(String name) {
        Identifier id = new Identifier(Spv_addon.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        Spv_addon.LOGGER.info("Registering Sounds for" + Spv_addon.MOD_ID);
    }

}
