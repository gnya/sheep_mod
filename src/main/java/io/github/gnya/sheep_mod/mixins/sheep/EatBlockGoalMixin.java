package io.github.gnya.sheep_mod.mixins.sheep;

import io.github.gnya.sheep_mod.api.IMixinSheep;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EatBlockGoal.class)
public abstract class EatBlockGoalMixin {
    @Shadow
    @Final
    private Mob mob;

    @Redirect(
            method = "tick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"
    )
    )
    public boolean redirectTick(Level level, BlockPos pos, BlockState blockState, int updateFlags) {
        boolean res = level.setBlock(pos, blockState, updateFlags);

        if (this.mob instanceof Sheep && ((IMixinSheep) this.mob).isHappy()) {
            // Happyな羊は最大5ブロック分の草を食べる
            for (var randomPos : BlockPos.randomInCube(level.getRandom(), 4, pos, 1)) {
                if (level.getBlockState(randomPos).is(Blocks.GRASS_BLOCK)) {
                    res &= level.setBlock(randomPos, blockState, updateFlags);
                }
            }
        }

        return res;
    }
}
