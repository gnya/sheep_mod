package io.github.gnya.sheep_mod.mixins.sleeper;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import io.github.gnya.sheep_mod.api.IMixinSheep;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.stream.Stream;

@Mixin(VillagerGoalPackages.class)
public class VillagerGoalPackagesMixin {
    @ModifyArg(
            method = "getPlayPackage", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/behavior/RunOne;<init>(Ljava/util/Map;Ljava/util/List;)V"
    ), index = 1
    )
    private static List<Pair<? extends BehaviorControl<? extends LivingEntity>, Integer>> modifyGetPlayPackage(
            List<Pair<? extends BehaviorControl<? extends LivingEntity>, Integer>> weightedBehaviors,
            @Local(argsOnly = true) float speedModifier
    ) {
        // 村人の子どもがHappyな羊を追いかけるようにする
        return Stream.concat(weightedBehaviors.stream(), Stream.of(Pair.of(
                InteractWith.of(
                        EntityType.SHEEP, 32, _ -> true, mob -> ((IMixinSheep) mob).isHappy(),
                        MemoryModuleType.INTERACTION_TARGET,
                        speedModifier, 2
                ), 16
        ))).collect(ImmutableList.toImmutableList());
    }
}
