package io.github.gnya.sheep_mod.api;

import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import org.jspecify.annotations.Nullable;

public interface SheepSleeper {
  SleepType getSleepInSheepType();

  boolean isSleepInSheep();

  void startSleeping(final Sheep sheep);

  @Nullable Sheep getBedSheep();

  // mixinではsuperが使えないので継承先ごとにメソッドを用意する
  void LivingEntity$startSleeping(final Sheep sheep);

  // mixinではsuperが使えないので継承先ごとにメソッドを用意する
  void ServerPlayer$startSleeping(final Sheep sheep);

  @AutoRegisterCapability
  enum SleepType {
    NONE,
    FACE_UP,
    FACE_DOWN
  }
}
