package net.dark.spv_addon.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.entities.custom.KittyEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class InitializeComponents implements EntityComponentInitializer {

    public static final ComponentKey<BellWalkerComponent> BELL_WALKER =
            ComponentRegistry.getOrCreate(new Identifier(Spv_addon.MOD_ID, "bell_walker"), BellWalkerComponent.class);
    public static final ComponentKey<KittyComponent> KITTY =
            ComponentRegistry.getOrCreate(new Identifier(Spv_addon.MOD_ID, "kitty"), KittyComponent.class);
    public static final ComponentKey<ThirstComponent> THIRST =
            ComponentRegistry.getOrCreate(new Identifier(Spv_addon.MOD_ID, "thirst"), ThirstComponent.class);
    public static final ComponentKey<SanityComponent> SANITY =
            ComponentRegistry.getOrCreate(new Identifier("spv_addon", "sanity"), SanityComponent.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {

        registry.registerFor(BellWalkerEntity.class, BELL_WALKER, BellWalkerComponent::new);
        registry.registerFor(KittyEntity.class, KITTY, KittyComponent::new);
        registry.registerFor(PlayerEntity.class, THIRST, ThirstComponent::new);
        registry.registerFor(PlayerEntity.class, SANITY, SanityComponent::new);


    }

}
