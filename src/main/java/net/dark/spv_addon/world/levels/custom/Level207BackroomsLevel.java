package net.dark.spv_addon.world.levels.custom;

import com.sp.SPBRevamped;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.compat.modmenu.ConfigStuff;
import com.sp.mixininterfaces.NewServerProperties;
import com.sp.world.events.level0.Level0Blackout;
import com.sp.world.levels.BackroomsLevel;
import com.sp.world.levels.custom.Level0BackroomsLevel;
import com.sp.world.levels.custom.Level1BackroomsLevel;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.deferred.light.DirectionalLight;
import net.dark.spv_addon.Spv_addon;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.init.BackroomsLevels;
import net.dark.spv_addon.init.ModEntities;
import net.dark.spv_addon.init.ModSounds;
import net.dark.spv_addon.world.events.level207.Level207AmbienceEvent;
import net.dark.spv_addon.world.events.level207.Level207BellWalkerEvent;
import net.dark.spv_addon.world.generation.level207.Level207ChunkGenerator;
import net.dark.spv_addon.world.levels.custom.events.HaHvavCustomEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.*;

public class Level207BackroomsLevel extends BackroomsLevel {
    private final Random random = Random.create();
    DirectionalLight light;
    float brightness;

    public Level207BackroomsLevel() {
        super("level207", Level207ChunkGenerator.CODEC, new Vec3d(7, 66, 7), BackroomsLevels.LEVEL207_WORLD_KEY, "spv_addon");
        this.registerTransition((world, playerComponent, from) -> {
            List<BackroomsLevel.LevelTransition> playerList = new ArrayList();
            int exitRadius = ConfigStuff.exitSpawnRadius;
            var server = world.getServer();
            if (server != null && server.isDedicated()) {
                exitRadius = ((NewServerProperties)((MinecraftDedicatedServer)server).getProperties()).getExitSpawnRadius();
            }

            if (from instanceof LevelIKEA && Math.abs(playerComponent.player.getPos().getZ()) >= (double)exitRadius) {
                playerList.add(this.getPoolRoomsTransition(playerComponent));
            }

            return playerList;
        }, "level207 -> poolrooms");
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
        this.registerEvents("empty", HaHvavCustomEvent::new);

    }

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) {
            if (this.light == null) {
                this.brightness = 1F;
                this.light = new DirectionalLight();
                VeilRenderSystem.renderer().getDeferredRenderer().getLightRenderer().addLight(this.light.setBrightness(this.brightness).setColor(0.28F, 0.28F, 0.28F));
            }
        }
    }


    @Override
    public int nextEventDelay() {
        return 100;
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
    }


    @Override
    public void readFromNbt(NbtCompound nbt) {
    }


    public void transitionOut(BackroomsLevel.CrossDimensionTeleport crossDimensionTeleport) {

    }

    @Override
    public void transitionIn(BackroomsLevel.CrossDimensionTeleport crossDimensionTeleport) {

    }

    @Override
    public int getTransitionDuration() {
        return 30;
    }
}
