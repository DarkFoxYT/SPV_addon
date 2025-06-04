package net.dark.spv_addon.world.levels.custom.events;

import com.sp.world.events.AbstractEvent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class KittyMeowEvent extends AbstractEvent {
    /**
     * Déclenche un événement de miaulement de chat.
     * Joue des sons de miaulement autour du joueur et a une chance d'invoquer un "Kitty Plush".
     *
     * @param world Le monde dans lequel l'événement se produit.
     * @param player Le joueur qui déclenche l'événement.
     */
    public void trigger(World world, ServerPlayerEntity player) {
        Random rand = new Random();
        // Joue 3 à 6 miaulements autour du joueur
        int meows = 3 + rand.nextInt(4);
        for (int i = 0; i < meows; i++) {
            double dx = player.getX() + rand.nextGaussian() * 5;
            double dy = player.getY();
            double dz = player.getZ() + rand.nextGaussian() * 5;
            world.playSound(null, dx, dy, dz, SoundEvents.ENTITY_CAT_AMBIENT, player.getSoundCategory(), 1.0F, 0.8F + rand.nextFloat() * 0.4F);
        }

        // 30% de chance de faire apparaître un "Kitty Plush" (à remplacer par ton mob/item)
        if (rand.nextFloat() < 0.3F) {
            BlockPos plushPos = player.getBlockPos().add(rand.nextInt(5) - 2, 0, rand.nextInt(5) - 2);
            // Remplace ceci par le code d’apparition de ton mob ou item
            // world.spawnEntity(new EntityKittyPlush(world, plushPos));
        }

        // Message immersif
        player.sendMessage(Text.translatable("Des miaulements résonnent... Quelque chose te regarde."), false);
    }

    public String getName() {
        return "Kitty Meow Event";
    }

    @Override
    public void init(World world) {

    }

    @Override
    public int duration() {
        return 0;
    }
}