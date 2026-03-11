package net.dark.spv_addon.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class ThirstComponent implements ComponentV3, AutoSyncedComponent {
    public static final ComponentKey<ThirstComponent> KEY2 =
            ComponentRegistry.getOrCreate(new Identifier("spv_addon", "thirst"), ThirstComponent.class);


    private static final int MAX_THIRST = 100;
    private final PlayerEntity player;
    private int thirst = MAX_THIRST;

    public ThirstComponent(PlayerEntity player) {
        this.player = player;
    }

    public int getThirst() {
        return thirst;
    }

    public void setThirst(int thirst) {
        int clamped = Math.max(0, Math.min(MAX_THIRST, thirst));
        if (this.thirst != clamped) {
            this.thirst = clamped;
            InitializeComponents.THIRST.sync(player);
        }
    }

    public void addThirst(int amount) {
        setThirst(thirst + amount);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        thirst = tag.getInt("thirst");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("thirst", thirst);
    }
}
