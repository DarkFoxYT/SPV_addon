package net.dark.spv_addon.cca;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.cca_stuff.WorldEvents;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import com.sp.entity.custom.SkinWalkerEntity;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.cca.BellWalkerComponent;        // <= ton component
import net.dark.spv_addon.entities.custom.BellWalkerEntity; // <= ton entity
import net.minecraft.util.Identifier;

public class InitializeComponents implements EntityComponentInitializer {

    public static final ComponentKey<BellWalkerComponent> BELL_WALKER =
            ComponentRegistry.getOrCreate(Identifier.of(Spv_addon.MOD_ID, "bell_walker"), BellWalkerComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {

        registry.registerFor(BellWalkerEntity.class, BELL_WALKER, BellWalkerComponent::new);
    }

}
