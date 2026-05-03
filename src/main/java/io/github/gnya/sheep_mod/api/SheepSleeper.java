package io.github.gnya.sheep_mod.api;

import net.minecraft.world.entity.animal.sheep.Sheep;

public interface SheepSleeper {
  boolean isSleepInSheep();

  void startSleeping(final Sheep sheep);

  // mixinではsuperが使えないので継承先ごとにメソッドを用意する
  void LivingEntity$startSleeping(final Sheep sheep);

  // mixinではsuperが使えないので継承先ごとにメソッドを用意する
  void ServerPlayer$startSleeping(final Sheep sheep);
}
