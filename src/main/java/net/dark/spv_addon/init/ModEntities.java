package net.dark.spv_addon.init;

import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.entities.custom.IkeaWalkerEntity;
import net.dark.spv_addon.entities.custom.KittyEntity;
import net.dark.spv_addon.entities.custom.StalkerEntity;
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
                            .dimensions(EntityDimensions.fixed(0.5f, 0.6f))
                            .build()
            );
    public static final EntityType<KittyEntity> KITTY =
            Registry.register(Registries.ENTITY_TYPE,
                    new Identifier(Spv_addon.MOD_ID, "kitty"),
                    FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, KittyEntity::new)
                            .dimensions(EntityDimensions.fixed(0.5f, 2.5f))
                            .build()
            );
    public static final EntityType<IkeaWalkerEntity> IKEA_WALKER =
            Registry.register(Registries.ENTITY_TYPE,
                    new Identifier(Spv_addon.MOD_ID, "ikea_walker"),
                    FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, IkeaWalkerEntity::new)
                            .dimensions(EntityDimensions.fixed(0.7f, 1.95f))
                            .build()
            );
    public static final EntityType<StalkerEntity> STALKER_ENTITY =
            Registry.register(Registries.ENTITY_TYPE,
                    new Identifier(Spv_addon.MOD_ID, "stalker_entity"),
                    FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, StalkerEntity::new)
                            .dimensions(EntityDimensions.fixed(0.7f, 1.95f))
                            .build()
            );


}

