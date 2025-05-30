package net.dark.spv_addon.particle;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;

public class WindowDripParticle extends SpriteBillboardParticle {
    public WindowDripParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0, 0, 0);
        this.setSprite(spriteProvider);
        this.velocityX = (random.nextFloat() - 0.5) * 0.003; // optional: a tiny shake left/right
        this.velocityY = -0.02;  // drips down slowly
        this.velocityZ = (random.nextFloat() - 0.5) * 0.003; // optional
        this.maxAge = 12 + random.nextInt(7); // 12–18 ticks
        this.gravityStrength = 0; // don't fall faster, simulate "stick"
        this.scale = 0.13f + random.nextFloat() * 0.05f; // a bit of random
        this.alpha = 0.8f;
    }

    @Override
    public void tick() {
        if (this.age < this.maxAge - 6) {
            this.velocityY = 0; // Stuck: stays on window
        } else {
            // Fall off the window as a drop
            this.gravityStrength = 0.14f;
            this.velocityY -= 0.01f;
        }
        super.tick();
        if (this.age > this.maxAge * 0.6) {
            this.alpha = Math.max(0, 1.0f - ((float)this.age / this.maxAge) * 1.5f);
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public boolean shouldCull() { return false; }

    // === THIS IS THE FABRIC WAY: PROVIDE A FACTORY ===
    public static class Factory implements ParticleFactory<net.minecraft.particle.DefaultParticleType> {
        private final SpriteProvider sprites;
        public Factory(SpriteProvider sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(net.minecraft.particle.DefaultParticleType type, ClientWorld world,
                                       double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            return new WindowDripParticle(world, x, y, z, sprites);
        }
    }
}
