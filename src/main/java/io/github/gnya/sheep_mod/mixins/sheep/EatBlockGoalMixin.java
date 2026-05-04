package io.github.gnya.sheep_mod.mixins.sheep;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.gnya.sheep_mod.api.IMixinSheep;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EatBlockGoal.class)
public abstract class EatBlockGoalMixin extends Goal {
  @Shadow @Final private Mob mob;

  @Shadow @Final private Level level;

  @Unique
  private boolean private$redirectTick(
      Predicate<BlockState> check, BlockPos pos, Consumer<BlockPos> edit) {
    boolean isMobAte = check.test(this.level.getBlockState(pos));

    if (this.mob instanceof Sheep && ((IMixinSheep) this.mob).isHappy()) {
      // Happyな羊は最大5ブロック分の草を食べる
      for (var randomPos : BlockPos.randomInCube(this.level.getRandom(), 4, pos, 1)) {
        if (check.test(this.level.getBlockState(randomPos))) {
          var serverLevel = (ServerLevel) this.level;
          var event = new EntityMobGriefingEvent(this.mob);
          var eventResult = EntityMobGriefingEvent.BUS.fire(event).getResult();

          if (eventResult == Result.DEFAULT
              ? serverLevel.getGameRules().get(GameRules.MOB_GRIEFING)
              : eventResult == Result.ALLOW) {
            edit.accept(randomPos);
          }

          isMobAte = true;
        }
      }
    }

    return isMobAte;
  }

  @Redirect(
      method = "tick",
      at =
          @At(value = "INVOKE", target = "Ljava/util/function/Predicate;test(Ljava/lang/Object;)Z"))
  public boolean redirectTick(
      Predicate<BlockState> isEdible, Object blockState, @Local(name = "pos") BlockPos pos) {
    return this.private$redirectTick(isEdible, pos, p -> this.level.destroyBlock(p, false));
  }

  @Redirect(
      method = "tick",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"))
  public boolean redirectTick(
      BlockState blockState, Object block, @Local(name = "below") BlockPos below) {
    return this.private$redirectTick(
        state -> state.is((Block) block),
        below,
        p -> {
          this.level.levelEvent(2001, p, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
          this.level.setBlock(p, Blocks.DIRT.defaultBlockState(), 2);
        });
  }
}
