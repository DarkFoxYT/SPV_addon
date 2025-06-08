package net.dark.spv_addon.items.custom;

import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;

public class TapeItem extends Item {
    private final SoundEvent sound;

    public TapeItem(Settings settings, SoundEvent sound) {
        super(settings);
        this.sound = sound;
    }

    public SoundEvent getSound() {
        return sound;
    }
}