package net.dark.spv_addon.world.levels.custom;

import com.sp.cca_stuff.PlayerComponent;
import com.sp.world.events.AbstractEvent;
import com.sp.world.levels.BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.Level5ChunkGenerator;
import net.dark.spv_addon.world.generation.ikea.LevelIKEAChunkGenerator;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class LevelIKEA extends BackroomsLevel {
    public LevelIKEA() {

        super("level_ikea", LevelIKEAChunkGenerator.CODEC, new Vec3d(0, 20.0, 0), BackroomsLevels.LEVEL_IKEA_WORLD_KEY, "spv_addon");
    }

    @Override
    public void register() {
        // Ajoute des events custom ici si besoin
    }

    @Override
    public AbstractEvent getRandomEvent(World world) {
        // Ikea peut avoir ses propres events (“Staff appears”, “Lights Out”, etc)
        return null;
    }

    @Override
    public int nextEventDelay() {
        return 1200; // Change si tu veux des events
    }

    @Override
    public void writeToNbt(NbtCompound nbt) { }

    @Override
    public void readFromNbt(NbtCompound nbt) { }

    @Override
    public boolean transitionOut(CrossDimensionTeleport teleport) {
        // Exits seulement sur points spéciaux ou si player trouve la sortie
        // Ici, simple : il doit être sur une “sortie” définie par bloc, ou par pos
        return teleport.playerComponent().player.getBlockStateAtPos().isOf(net.dark.spv_addon.init.ModBlocks.IKEA_EXIT);
    }

    @Override
    public void transitionIn(CrossDimensionTeleport teleport) { }

    @Override
    public int getTransitionDuration() {
        return 30;
    }
}
