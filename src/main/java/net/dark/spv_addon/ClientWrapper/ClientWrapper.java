package net.dark.spv_addon.ClientWrapper;

import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import net.dark.spv_addon.init.ModSounds;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;

public class ClientWrapper {
    public static void bellWalkerPlayStepSound(ServerLimb limb) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            MinecraftClient client = MinecraftClient.getInstance();
            client.getSoundManager().play(new PositionedSoundInstance(ModSounds.BELLWALKER_CARP, SoundCategory.HOSTILE, 5.0f, 1.0f, limb.random, limb.pos.x, limb.pos.y, limb.pos.z));
            limb.playedStepSound = true;
        }
    }
}
