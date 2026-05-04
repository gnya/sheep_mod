package io.github.gnya.sheep_mod.mixins.renderer;

import io.github.gnya.sheep_mod.api.IMixinLivingEntityRenderState;
import io.github.gnya.sheep_mod.api.SheepSleeper;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
@Implements(@Interface(iface = IMixinLivingEntityRenderState.class, prefix = "sheep_mod$"))
public abstract class LivingEntityRenderStateMixin {
  @Unique private SheepSleeper.SleepType sheep_mod$sleepInSheepType;

  @Unique private float sheep_mod$vehicleSheepYRot;

  public SheepSleeper.SleepType sheep_mod$getSleepInSheepType() {
    return this.sheep_mod$sleepInSheepType;
  }

  public void sheep_mod$setSleepInSheepType(final SheepSleeper.SleepType type) {
    this.sheep_mod$sleepInSheepType = type;
  }

  public boolean sheep_mod$isSleepInSheep() {
    return this.sheep_mod$sleepInSheepType != SheepSleeper.SleepType.NONE;
  }

  public float sheep_mod$getBedSheepYRot() {
    return this.sheep_mod$vehicleSheepYRot;
  }

  public void sheep_mod$setBedSheepYRot(final float value) {
    this.sheep_mod$vehicleSheepYRot = value;
  }
}
