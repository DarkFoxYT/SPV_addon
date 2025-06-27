package net.dark.spv_addon.init;

import net.dark.spv_addon.blocks.entities.KittyLampEntity;
import net.dark.spv_addon.blocks.entities.KittyLightBlockEntity;
import net.dark.spv_addon.blocks.entities.TapeRecorderBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<TapeRecorderBlockEntity> TAPE_RECORDER;
    public static BlockEntityType<KittyLampEntity> KITTY_LAMP;
    public static BlockEntityType<KittyLightBlockEntity> KITTY_LIGHT_BLOCK_ENTITY;

    public static void register() {
        TAPE_RECORDER = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("spv_addon", "tape_recorder"),
                BlockEntityType.Builder.create(TapeRecorderBlockEntity::new, ModBlocks.TAPE_RECORDER).build(null)
        );
        KITTY_LAMP = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("spv_addon", "kitty_lamp"),
                BlockEntityType.Builder.create(KittyLampEntity::new, ModBlocks.KITTY_LAMP).build(null)
        );
        KITTY_LIGHT_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier("spv_addon", "kitty_light"),
                BlockEntityType.Builder.create(KittyLightBlockEntity::new, ModBlocks.KITTY_LIGHT).build(null)
        );
    }
}