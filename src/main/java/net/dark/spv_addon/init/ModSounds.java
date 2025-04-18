package net.dark.spv_addon.init;

import com.sp.SPBRevamped;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.dark.spv_addon.Spv_addon;

public class ModSounds {
    public static final SoundEvent BELLWALKER_BELL = register("entity.bellwalker.bell");
    public static final SoundEvent BELLWALKER_CARP = register("entity.bellwalker.carp");
    public static final SoundEvent DONG = register("entity.spv.belldong");
    public static final SoundEvent ELEV = register("stuff.spv.elevator");

    private static SoundEvent register(String name) {
        Identifier id = new Identifier(Spv_addon.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void registerSounds() {
        SPBRevamped.LOGGER.info("Registering Sounds for" + SPBRevamped.MOD_ID);
    }

}
