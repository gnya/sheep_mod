package io.github.gnya.sheep_mod.api;

import net.minecraft.world.entity.animal.sheep.Sheep;

public interface SheepSleeper {
    boolean isSleepInSheep();

    void startSleeping(final Sheep sheep);

    void LivingEntity$startSleeping(final Sheep sheep);
}
