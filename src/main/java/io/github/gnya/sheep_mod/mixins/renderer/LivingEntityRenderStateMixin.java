package io.github.gnya.sheep_mod.mixins.renderer;

import io.github.gnya.sheep_mod.api.IMixinLivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
@Implements(@Interface(iface = IMixinLivingEntityRenderState.class, prefix = "sheep_mod$"))
public abstract class LivingEntityRenderStateMixin {
  @Unique private boolean sheep_mod$isSleepInSheep;

  @Unique private float sheep_mod$vehicleSheepYRot;

  public boolean sheep_mod$isSleepInSheep() {
    return this.sheep_mod$isSleepInSheep;
  }

  public void sheep_mod$setSleepInSheep(final boolean value) {
    this.sheep_mod$isSleepInSheep = value;
  }

  public float sheep_mod$getBedSheepYRot() {
    return this.sheep_mod$vehicleSheepYRot;
  }

  public void sheep_mod$setBedSheepYRot(final float value) {
    this.sheep_mod$vehicleSheepYRot = value;
  }
}
