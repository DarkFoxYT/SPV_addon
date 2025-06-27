// CustomDamageSources.java
package net.dark.spv_addon.init;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class CustomDamageSources {
    public static final RegistryKey<DamageType> THIRST_DAMAGE_ID =
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("spv_addon", "thirst"));
    public static final RegistryKey<DamageType> DISTORTION_DAMAGE_ID =
            RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("spv_addon", "distortion"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }
}
