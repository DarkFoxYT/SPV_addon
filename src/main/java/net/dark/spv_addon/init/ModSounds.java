package net.dark.spv_addon.init;

import net.dark.spv_addon.Spv_addon;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent BELLWALKER_BELL = register("entity.bellwalker.bell");
    public static final SoundEvent BONK = register("stuff.bonk");
    public static final SoundEvent LEVEL_207_AMBIANCE = register("stuff.207_ambiance");
    public static final SoundEvent BELLWALKER_CARP = register("entity.bellwalker.carp");
    public static final SoundEvent DONG = register("stuff.belldong");
    public static final SoundEvent ELEV = register("stuff.elevator");
    public static final SoundEvent IC1 = register("stuff.intercom_custom1");
    public static final SoundEvent IC2 = register("stuff.intercom_custom2");


    public static final SoundEvent TAPE1 = register("tapes.tape1");
    public static final SoundEvent TAPE2 = register("tapes.tape2");

    private static SoundEvent register(String name) {
        Identifier id = new Identifier(Spv_addon.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        Spv_addon.LOGGER.info("Registering Sounds for" + Spv_addon.MOD_ID);
    }

}
