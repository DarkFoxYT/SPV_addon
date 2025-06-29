package net.dark.spv_addon.client;

import com.sp.init.BackroomsLevels;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

public class ShaderPuddleHook {

    private static final Identifier EVERYTHING_SHADER = new Identifier("spb", "everything");

    public static void registerShaderHook() {
        VeilEventPlatform.INSTANCE.preVeilPostProcessing((name, pipeline, context) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            ClientWorld world = client.world;

            if (player == null || world == null) return;

            ShaderProgram shaderProgram = context.getShader(EVERYTHING_SHADER);
            if (shaderProgram != null) {
                boolean inLevelWithPuddles = isPuddleWorld(world);
                shaderProgram.setInt("TogglePuddles", inLevelWithPuddles ? 1 : 0);
            }
        });
    }

    private static boolean isPuddleWorld(ClientWorld world) {
        // Assure que Level 1 et Level 207 activent bien les flaques
        return world.getRegistryKey() == BackroomsLevels.LEVEL1_WORLD_KEY
                || world.getRegistryKey() == net.dark.spv_addon.init.BackroomsLevels.LEVEL207_WORLD_KEY;
    }
}
