package net.dark.spv_addon.world.levels.custom;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.mixininterfaces.NewServerProperties;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.Level2BackroomsLevel;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.world.generation.ikea.LevelIKEAChunkGenerator;
import net.dark.spv_addon.world.levels.custom.events.HaHvavCustomEvent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class LevelIKEA extends BackroomsLevel {
    public LevelIKEA() {
        super("level_ikea", LevelIKEAChunkGenerator.CODEC, new Vec3d(16, 2, 16), BackroomsLevels.LEVEL_IKEA_WORLD_KEY, "spv_addon");

        this.registerTransition((world, playerComponent, from) -> {
            List<BackroomsLevel.LevelTransition> playerList = new ArrayList();
            int exitRadius = ConfigStuff.exitSpawnRadius;
            if (world.getServer() != null && world.getServer().isDedicated()) {
                exitRadius = ((NewServerProperties)((MinecraftDedicatedServer)world.getServer()).getProperties()).getExitSpawnRadius();
            }

            if (from instanceof LevelIKEA && Math.abs(playerComponent.player.getPos().getZ()) >= (double)exitRadius) {
                playerList.add(this.getPoolRoomsTransition(playerComponent));
            }

            return playerList;
        }, "level_ikea -> poolrooms");
    }

    private BackroomsLevel.LevelTransition getPoolRoomsTransition(PlayerComponent playerComponent) {
        return new BackroomsLevel.LevelTransition(110, (teleport, tick) -> {
            World world = teleport.playerComponent().player.getWorld();
            if (!world.isClient()) {
                if (tick == 20) {
                    teleport.playerComponent().setShouldNoClip(true);
                    teleport.playerComponent().sync();
                }

                if (tick == 14) {
                    SPBRevamped.sendBlackScreenPacket((ServerPlayerEntity)teleport.playerComponent().player, 20, true, false);
                }

                if (tick == 1) {
                    teleport.playerComponent().setShouldNoClip(false);
                    teleport.playerComponent().sync();
                }

            }
        }, new BackroomsLevel.CrossDimensionTeleport(playerComponent, com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL.getSpawnPos(), this, com.sp.init.BackroomsLevels.POOLROOMS_BACKROOMS_LEVEL), (teleport, tick) -> {
            teleport.playerComponent().setShouldNoClip(false);
            teleport.playerComponent().sync();
        });
    }

    @Override
    public void register() {

        this.registerEvents("empty_ikea", HaHvavCustomEvent::new);

    }


    @Override
    public int nextEventDelay() {
        return 100;
    }

    @Override
    public BoolTextPair allowsTorch() {
        return new BoolTextPair(true, Text.translatable("Flash.on"));
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
    }

    @Override
    public void transitionOut(CrossDimensionTeleport teleport) {
    }

    @Override
    public void transitionIn(CrossDimensionTeleport teleport) {

    }

}
