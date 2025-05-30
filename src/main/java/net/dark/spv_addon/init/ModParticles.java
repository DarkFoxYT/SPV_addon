package net.dark.spv_addon.init;

import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.particle.WindowDripParticle;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

// Exemple d’enregistrement d’une particule custom
public class ModParticles {
    public static DefaultParticleType WINDOW_DRIP_TYPE;

    public static void register() {
        WINDOW_DRIP_TYPE = Registry.register(
                Registries.PARTICLE_TYPE,
                new Identifier("spv_addon", "window_drip"),
                FabricParticleTypes.simple()
        );
    }

    public static void registerClientParticles() {
        ParticleFactoryRegistry.getInstance().register(
                WINDOW_DRIP_TYPE,
                WindowDripParticle.Factory::new
        );
    }

}
