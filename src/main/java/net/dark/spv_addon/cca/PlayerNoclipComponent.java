package net.dark.spv_addon.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.Random;

public class PlayerNoclipComponent implements AutoSyncedComponent {
    private int distanceWalked = 0;
    private int noclipThreshold = 100;
    private boolean shouldNoclip = false;
    private final PlayerEntity player;

    // IMPORTANT : constructeur qui prend l'entité cible !
    public PlayerNoclipComponent(PlayerEntity player) {
        this.player = player;
        randomizeThreshold();
    }

    public void addDistance(int blocks) {
        distanceWalked += blocks;
        if (distanceWalked >= noclipThreshold) {
            shouldNoclip = true;
        }
    }

    public boolean shouldNoclip() {
        return shouldNoclip;
    }

    public void reset() {
        distanceWalked = 0;
        shouldNoclip = false;
        randomizeThreshold();
    }

    private void randomizeThreshold() {
        noclipThreshold = 50 + new Random().nextInt(151); // 50-200
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        distanceWalked = tag.getInt("DistanceWalked");
        noclipThreshold = tag.getInt("NoclipThreshold");
        shouldNoclip = tag.getBoolean("ShouldNoclip");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("DistanceWalked", distanceWalked);
        tag.putInt("NoclipThreshold", noclipThreshold);
        tag.putBoolean("ShouldNoclip", shouldNoclip);
    }
}
