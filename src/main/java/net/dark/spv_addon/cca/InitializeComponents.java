package net.dark.spv_addon.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class InitializeComponents implements EntityComponentInitializer {

    public static final ComponentKey<BellWalkerComponent> BELL_WALKER =
            ComponentRegistry.getOrCreate(new Identifier(Spv_addon.MOD_ID, "bell_walker"), BellWalkerComponent.class);
    public static final ComponentKey<FlashlightBatteryComponent> FLASHLIGHT_BATTERY =
            ComponentRegistry.getOrCreate(new Identifier(Spv_addon.MOD_ID, "flashlight_battery"), FlashlightBatteryComponent.class);
    public static final ComponentKey<ThirstComponent> THIRST =
            ComponentRegistry.getOrCreate(new Identifier(Spv_addon.MOD_ID, "thirst"), ThirstComponent.class);
    public static final ComponentKey<SanityComponent> SANITY =
            ComponentRegistry.getOrCreate(new Identifier("spv_addon", "sanity"), SanityComponent.class);
    public static final ComponentKey<RunTimerComponent> RUN_TIMER =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(Spv_addon.MOD_ID, "run_timer"), RunTimerComponent.class);

    public static final ComponentKey<LevelRunComponent> LEVEL_RUN =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(Spv_addon.MOD_ID, "level_run"), LevelRunComponent.class);

    public static final ComponentKey<net.dark.spv_addon.cca.DeathTeleportComponent> DEATH_TELEPORT =
            ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier(Spv_addon.MOD_ID, "death_teleport"), net.dark.spv_addon.cca.DeathTeleportComponent.class);


    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {

        registry.registerFor(BellWalkerEntity.class, BELL_WALKER, BellWalkerComponent::new);
        registry.registerFor(ServerPlayerEntity.class, DEATH_TELEPORT, DeathTeleportComponent::new);
        registry.registerForPlayers(FLASHLIGHT_BATTERY, FlashlightBatteryComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerFor(PlayerEntity.class, THIRST, ThirstComponent::new);
        registry.registerFor(PlayerEntity.class, SANITY, SanityComponent::new);
        registry.registerForPlayers(RUN_TIMER, player -> new RunTimerComponent(), RespawnCopyStrategy.ALWAYS_COPY);
        registry.registerForPlayers(LEVEL_RUN, player -> new LevelRunComponent(player), RespawnCopyStrategy.NEVER_COPY);



    }

}
