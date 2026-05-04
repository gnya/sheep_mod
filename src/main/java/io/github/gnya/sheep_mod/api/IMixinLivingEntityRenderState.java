package io.github.gnya.sheep_mod.api;

public interface IMixinLivingEntityRenderState {
  SheepSleeper.SleepType getSleepInSheepType();

  void setSleepInSheepType(final SheepSleeper.SleepType type);

  boolean isSleepInSheep();

  float getBedSheepYRot();

  void setBedSheepYRot(final float value);
}
