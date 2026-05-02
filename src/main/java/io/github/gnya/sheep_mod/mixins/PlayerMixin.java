package io.github.gnya.sheep_mod.mixins;

import com.mojang.datafixers.util.Either;
import io.github.gnya.sheep_mod.api.ILivingEntityMixin;
import io.github.gnya.sheep_mod.api.IPlayerMixin;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Player.class)
@Implements(@Interface(iface = IPlayerMixin.class, prefix = "sheep_mod$"))
public abstract class PlayerMixin extends Avatar {
    @Shadow
    private int sleepCounter;

    protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
        // ダミーコンストラクタ
        super(type, level);
    }

    public Either<Player.BedSleepingProblem, Unit> sheep_mod$startSleepInBed(final Sheep sheep) {
        ((ILivingEntityMixin) this).startSleeping(sheep);
        this.sleepCounter = 0;
        return Either.right(Unit.INSTANCE);
    }
}
