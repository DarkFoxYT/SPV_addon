package net.dark.spv_addon.cca;

import com.sp.entity.ik.components.IKLegComponent;
import com.sp.entity.ik.parts.Segment;
import com.sp.entity.ik.parts.ik_chains.TargetReachingIKChain;
import com.sp.entity.ik.parts.sever_limbs.ServerLimb;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.dark.spv_addon.entities.custom.BellWalkerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class BellWalkerComponent implements AutoSyncedComponent {
    private final BellWalkerEntity entity;

    // C = TargetReachingIKChain, E = BellWalkerEntity
    private final IKLegComponent<TargetReachingIKChain, BellWalkerEntity> IKComponent;

    public BellWalkerComponent(BellWalkerEntity entity) {
        this.entity = entity;

        // 1) Liste des points de base pour les 6 pattes
        List<ServerLimb> endpoints = List.of(
                new ServerLimb( 0.5, 0.0,  0.8),
                new ServerLimb(-0.5, 0.0,  0.8),
                new ServerLimb( 0.8, 0.0,  0.2),
                new ServerLimb(-0.8, 0.0,  0.2),
                new ServerLimb( 0.5, 0.0, -0.6),
                new ServerLimb(-0.5, 0.0, -0.6)
        );

        // 2) Un LegSetting identique pour chaque patte (tu peux en passer plusieurs si besoin)
        IKLegComponent.LegSetting setting = new IKLegComponent.LegSetting.Builder()
                .fluid(RaycastContext.FluidHandling.NONE)
                .maxStandingStillDistance(0.1)
                .maxDistance(1.5)
                .stepInFront(1)
                .movementSpeed(0.7)
                .standStillCounter(20)
                .build();
        List<IKLegComponent.LegSetting> settings = endpoints.stream()
                .map(e -> setting)
                .collect(Collectors.toList());

        // 3) Les 6 chaînes IK, chacune construite avec tes 4 segments
        TargetReachingIKChain chainTemplate = new TargetReachingIKChain(
                new Segment.Builder().length(0.65).build(),
                new Segment.Builder().length(1.00).build(),
                new Segment.Builder().length(1.30).build(),
                new Segment.Builder().length(0.85).build()
        );

        this.IKComponent = new IKLegComponent<>(
                settings,
                endpoints,
                chainTemplate, chainTemplate, chainTemplate,
                chainTemplate, chainTemplate, chainTemplate
        );
    }

    public IKLegComponent<TargetReachingIKChain, BellWalkerEntity> getIKComponent() {
        return IKComponent;
    }

    @Override
    public void readFromNbt(NbtCompound tag) { /* rien à persister */ }

    @Override
    public void writeToNbt(NbtCompound tag) { /* rien à persister */ }

    public void sync() {
        InitializeComponents.BELL_WALKER.sync(entity);
    }
}