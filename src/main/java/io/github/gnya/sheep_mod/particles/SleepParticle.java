package io.github.gnya.sheep_mod.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SleepParticle extends SingleQuadParticle {
  protected SleepParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
    super(level, x, y, z, sprites.first());

    this.gravity = 0.0F;
    this.lifetime = 60;
    this.yd = 0.005F;
    this.alpha = 0.0F;

    this.scale(0.4F);
  }

  @Override
  public void tick() {
    this.alpha =
        (float)
            Mth.smoothstep(
                Math.min(
                    Math.min(10, this.age) / 10.0F,
                    Math.min(15, Math.abs(this.lifetime - this.age)) / 15.0F));
    super.tick();
  }

  @Override
  protected @NonNull Layer getLayer() {
    return Layer.TRANSLUCENT;
  }

  public static class Provider implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet sprites;

    public Provider(final SpriteSet sprites) {
      this.sprites = sprites;
    }

    @Override
    public @Nullable Particle createParticle(
        @NonNull SimpleParticleType options,
        @NonNull ClientLevel level,
        double x,
        double y,
        double z,
        double xAux,
        double yAux,
        double zAux,
        @NonNull RandomSource random) {
      return new SleepParticle(level, x, y, z, this.sprites);
    }
  }
}
