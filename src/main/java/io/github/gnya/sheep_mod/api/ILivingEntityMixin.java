package io.github.gnya.sheep_mod.api;

import net.minecraft.world.entity.animal.sheep.Sheep;
import org.jspecify.annotations.NonNull;

public interface ILivingEntityMixin {
    boolean isSleepInSheep();

    boolean startSleeping(final @NonNull Sheep sheep);
}
