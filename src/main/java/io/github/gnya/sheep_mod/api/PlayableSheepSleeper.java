package io.github.gnya.sheep_mod.api;

import com.mojang.datafixers.util.Either;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;

public interface PlayableSheepSleeper extends SheepSleeper {
    Either<Player.BedSleepingProblem, Unit> startSleepInBed(final Sheep sheep);

    Either<Player.BedSleepingProblem, Unit> Player$startSleepInBed(final Sheep sheep);
}
