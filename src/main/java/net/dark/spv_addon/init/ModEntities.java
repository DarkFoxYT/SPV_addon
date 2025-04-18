package net.dark.spv_addon.init;

import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.entities.custom.DeathCamEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {


    public static final EntityType<BellWalkerEntity> SIX_LEG_ENTITY =
            Registry.register(Registries.ENTITY_TYPE,
                    new Identifier(Spv_addon.MOD_ID, "bellwalker_entity"),
                    FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, BellWalkerEntity::new)
                            .dimensions(EntityDimensions.fixed(1.0f, 0.8f))
                            .build()
            );
    public static final EntityType<DeathCamEntity> DEATH_CAM =
            Registry.register(
                    Registries.ENTITY_TYPE,
                    new Identifier(Spv_addon.MOD_ID, "death_cam"),
                    FabricEntityTypeBuilder.<DeathCamEntity>create(SpawnGroup.MISC, DeathCamEntity::new)
                            .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
                            .trackRangeBlocks(64)
                            .build()
            );

}

