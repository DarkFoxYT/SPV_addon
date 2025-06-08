package net.dark.spv_addon.world.levels.custom;

import com.sp.cca_stuff.PlayerComponent;
import com.sp.world.events.AbstractEvent;
import com.sp.world.levels.BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.init.ModBlocks;
import net.dark.spv_addon.world.generation.ikea.LevelIKEAChunkGenerator;
import net.dark.spv_addon.world.levels.custom.events.HaHvavCustomEvent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
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
        events.add(HaHvavCustomEvent::new);

    }

    @Override
    public AbstractEvent getRandomEvent(World world) {
        // Ikea peut avoir ses propres events (“Staff appears”, “Lights Out”, etc)
        return null;
    }
    /**
     * Indicates if the flashlight (torch) is allowed in this level.
     * @return BoolTextPair containing the permission and a message.
     */
    @Override
    public BoolTextPair allowsTorch() {
        return new BoolTextPair(true, Text.translatable("Flashlight on."));
    }

    @Override
    public int nextEventDelay() {
        return 999999999; // Change si tu veux des events
    }

    @Override
    public void writeToNbt(NbtCompound nbt) { }

    @Override
    public void readFromNbt(NbtCompound nbt) { }

    @Override
    public boolean transitionOut(CrossDimensionTeleport teleport) {
        // Exits seulement sur points spéciaux ou si player trouve la sortie
        // Ici, simple : il doit être sur une “sortie” définie par bloc, ou par pos
        return teleport.playerComponent().player.getBlockStateAtPos().isOf(ModBlocks.EXIT_SIGN);
    }

    @Override
    public void transitionIn(CrossDimensionTeleport teleport) {

    }

    @Override
    public int getTransitionDuration() {
        return 30;
    }
}
