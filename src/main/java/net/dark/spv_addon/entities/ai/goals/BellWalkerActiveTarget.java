package net.dark.spv_addon.entities.ai.goals;

import com.sp.cca_stuff.InitializeComponents;
import com.sp.cca_stuff.SkinWalkerComponent;
import com.sp.entity.custom.SkinWalkerEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.player.PlayerEntity;

public class BellWalkerActiveTarget extends ActiveTargetGoal<PlayerEntity> {
    private final SkinWalkerComponent component;

    public BellWalkerActiveTarget(SkinWalkerEntity entity) {
        super(entity, PlayerEntity.class, false);
        this.component = InitializeComponents.SKIN_WALKER.get(entity);
    }

    @Override
    public boolean canStart() {
        if(!this.component.isInTrueForm() && !this.component.shouldBeginReveal()) {
            return super.canStart();
        }

        return false;
    }
}
