package net.dark.spv_addon.world.events.level207;

import com.sp.world.events.AbstractEvent;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.dark.spv_addon.init.ModEntities;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class Level207BellWalkerEvent extends AbstractEvent {
    private static final int EVENT_DELAY_TICKS = 20 * 60 * 3; // 3 minutes
    private final Random random = Random.create();
    private int ticks = 0;
    private ServerWorld world;
    private Vec3d spawnPos;

    public Level207BellWalkerEvent() {
        // Constructeur par défaut
    }

    // Appelée automatiquement par le système d'event après l'ajout
    public void init(World world) {
        if (world instanceof ServerWorld serverWorld) {
            this.world = serverWorld;
            // Choisissez une position de spawn adaptée à votre niveau
            this.spawnPos = new Vec3d(7, 66, 7); // exemple : spawn du niveau 207
        }
    }

    @Override
    public int duration() {
        return 1;
    }

    public void tick() {
        if (world == null || spawnPos == null) return;
        ticks++;
        if (ticks >= EVENT_DELAY_TICKS) {
            ticks = 0;
            spawnBellWalkers();
        }
    }

    private void spawnBellWalkers() {
        int count = 2 + random.nextInt(3); // 2 à 4
        for (int i = 0; i < count; i++) {
            double dx = spawnPos.x + random.nextBetween(-10, 10);
            double dz = spawnPos.z + random.nextBetween(-10, 10);
            double dy = spawnPos.y;
            BellWalkerEntity bellWalker = new BellWalkerEntity(ModEntities.SIX_LEG_ENTITY, world);
            bellWalker.refreshPositionAndAngles(dx, dy, dz, 0, 0);
            world.spawnEntity(bellWalker);
        }
    }
}