package io.github.gnya.sheep_mod.mixins.renderer;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.gnya.sheep_mod.api.IMixinLivingEntityRenderState;
import io.github.gnya.sheep_mod.api.SheepSleeper;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
  @Inject(
      method =
          "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
      at = @At("TAIL"))
  public void extractRenderState(
      final LivingEntity entity,
      final LivingEntityRenderState state,
      final float partialTicks,
      CallbackInfo ci) {
    if (!((SheepSleeper) entity).isSleepInSheep()) {
      return;
    }

    // TODO getBedSheep()を追加する
    if (!(entity.getVehicle() instanceof Sheep sheep)) {
      return;
    }

    float angle = sheep.getPreciseBodyRotation(partialTicks);

    ((IMixinLivingEntityRenderState) state).setSleepInSheep(true);
    ((IMixinLivingEntityRenderState) state).setBedSheepYRot(angle);

    // 身体の向きを動かさない
    state.bodyRot = 0.0F;
    // TODO 頭の向きをカメラの方向に向ける
    state.yRot = 0.0F;
  }

  @ModifyVariable(method = "setupRotations", at = @At("LOAD"), name = "angle")
  protected float modifySetupRotations(
      float angle, @Local(argsOnly = true) LivingEntityRenderState state) {
    if (((IMixinLivingEntityRenderState) state).isSleepInSheep()) {
      // 羊の上で寝ているときは身体の回転を羊に合わせる
      return 90.0F - ((IMixinLivingEntityRenderState) state).getBedSheepYRot();
    } else {
      return angle;
    }
  }
}
