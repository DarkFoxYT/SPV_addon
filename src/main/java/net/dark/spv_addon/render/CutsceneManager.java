package net.dark.spv_addon.render;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.PlayerComponent;
import com.sp.init.ModSounds;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;



public class CutsceneManager {
    private static CutsceneManager INSTANCE;
    private boolean active;
    private long    startTime;
    private ArmorStandEntity camera;
    private Vec3d   velocity;

    // Duration of the death cutscene in milliseconds
    private static final long   DURATION_MS = 5000;
    private static final double INIT_SPEED   = -0.2;
    private static final double GRAVITY      = -0.02;
    private static final double TERMINAL     = -1.0;

    private CutsceneManager() {}

    public static void init() {
        INSTANCE = new CutsceneManager();
        ClientTickEvents.END_CLIENT_TICK.register(client -> INSTANCE.tick(client));
    }

    public static CutsceneManager getInstance() {
        return INSTANCE;
    }

    public void startDeathCutscene() {
        if (active) return;


        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        World world = client.world;
        if (player == null || world == null) return;
        client.options.hudHidden = true;


        active    = true;
        startTime = System.currentTimeMillis();
        velocity  = new Vec3d(0, INIT_SPEED, 0);

        player.playSound(net.dark.spv_addon.init.ModSounds.DONG, 1.0f, 1.0f);
        PlayerComponent comp = InitializeComponents.PLAYER.get(player);
        if (comp.shouldGlitch()) {
            player.playSound(ModSounds.GLITCH, 1.0f, 1.0f);
        }

        camera = new ArmorStandEntity(world, player.getX(), player.getY(), player.getZ());
        camera.setInvisible(true);
        camera.setNoGravity(true);
        world.spawnEntity(camera);

        client.setCameraEntity(camera);
        client.options.hudHidden = true;
    }

    private void tick(MinecraftClient client) {
        if (!active) return;
        if (client.player == null || client.world == null) return;

        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed >= DURATION_MS) {
            active                  = false;
            client.options.hudHidden = false;
            client.setCameraEntity(client.player);
            if (camera != null) {
                camera.discard();
                camera = null;
            }

            if (client.player != null) {
                client.player.requestRespawn();
            }
            return;
        }

        double vy = Math.max(velocity.y + GRAVITY, TERMINAL);
        velocity = new Vec3d(0, vy, 0);

        double newY = camera.getY() + vy;

        BlockHitResult hit = client.world.raycast(new RaycastContext(
                new Vec3d(camera.getX(), camera.getY(), camera.getZ()),
                new Vec3d(camera.getX(), camera.getY() - 2.0, camera.getZ()),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                camera
        ));
        if (hit.getType() == BlockHitResult.Type.BLOCK) {
            newY = Math.max(newY, hit.getPos().y + 0.1);
        }

        Box nextBox = camera.getBoundingBox().offset(0, vy, 0);
        if (!client.world.isSpaceEmpty(nextBox)) {
            newY = camera.getY();
        }

        camera.updatePosition(camera.getX(), newY, camera.getZ());
    }

    public boolean isActive() {
        return active;
    }
}