package net.dark.spv_addon;

import com.sp.SPBRevampedClient;
import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.entity.ik.model.GeckoLib.MowzieModelFactory;
import com.sp.networking.InitializePackets;
import com.sp.render.pbr.PbrRegistry;
import eu.midnightdust.lib.config.MidnightConfig;
import net.dark.spv_addon.commands.Level5Command;
import net.dark.spv_addon.commands.SanityCommand;
import net.dark.spv_addon.commands.ThirstCommand;
import net.dark.spv_addon.compat.modmenu.ConfigStuff;
import net.dark.spv_addon.init.*;
import net.dark.spv_addon.init.ModItemGroups;
import net.dark.spv_addon.init.ModItems;
import net.dark.spv_addon.Additions.thirst.ThirstManager;
import net.dark.spv_addon.Additions.battery.FlashlightBatteryEvents;
import net.dark.spv_addon.commands.FlashlightBatteryCommand;
import net.dark.spv_addon.voicechat.SpvAddonVoicechatPlugin;
import net.dark.spv_addon.world.events.LevelRunTicker;
import net.dark.spv_addon.world.generation.run.RunChunkGenerator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sp.SPBRevamped.sendBlackScreenPacket;

public class Spv_addon implements ModInitializer {
    public static final String MOD_ID = "spv_addon";
    public static final Logger LOGGER = LoggerFactory.getLogger("spv_addon");

    public static final DefaultParticleType RAIN_PARTICLE = FabricParticleTypes.simple();


    @Override
    public void onInitialize() {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID, "rain_particle"), RAIN_PARTICLE);


        ThirstManager.register();
        FlashlightBatteryEvents.register();

        ModChunkGenerators.register();
        BackroomsLevels.init();

        ModItems.registerItems();
        ModBlocks.registerModBlocks();
        ModItemGroups.registerItemGroups();
        ModSounds.registerSounds();

        MidnightConfig.init(MOD_ID, ConfigStuff.class);

        GeckoLibUtil.addCustomBakedModelFactory(MOD_ID, new MowzieModelFactory());
        GeckoLib.initialize();


        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            FlashlightBatteryCommand.register(dispatcher);
            SanityCommand.register(dispatcher);
            Level5Command.register(dispatcher);
            ThirstCommand.register(dispatcher);
        });


        ServerTickEvents.START_SERVER_TICK.register(server -> {
            SpvAddonVoicechatPlugin.justSpoke.clear();
        });

        ServerPlayerEvents.AFTER_RESPAWN.register(((oldPlayer, newPlayer, alive) -> {
            if(!com.sp.init.BackroomsLevels.isInBackrooms(oldPlayer.getWorld().getRegistryKey())) {
                return;
            }

            boolean backupInvulnerable;
            try {
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                PlayerComponent playerComponent = InitializeComponents.PLAYER.get(newPlayer);

                sendBlackScreenPacket(newPlayer, 120, false, false);
                backupInvulnerable = newPlayer.getAbilities().invulnerable;
                newPlayer.getAbilities().invulnerable = true;
                playerComponent.setShouldRender(false);
                playerComponent.sync();

                //After YOU CAN'T ESCAPE is over
                executorService.schedule(() -> {
                    playerComponent.setShouldRender(true);
                    playerComponent.setShouldDoStatic(true);
                    playerComponent.sync();
                    newPlayer.getAbilities().invulnerable = backupInvulnerable;
                    executorService.shutdown();
                }, 6000, TimeUnit.MILLISECONDS);

                executorService.schedule(() -> {
                    playerComponent.setShouldDoStatic(false);
                    playerComponent.sync();
                    executorService.shutdown();
                }, 8000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                LOGGER.error("Error in AFTER_RESPAWN event: {}", String.valueOf(e));
            }
        }));


    }
}
