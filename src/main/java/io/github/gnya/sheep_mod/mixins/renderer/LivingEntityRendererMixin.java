package io.github.gnya.sheep_mod.mixins.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.gnya.sheep_mod.api.IMixinLivingEntityRenderState;
import io.github.gnya.sheep_mod.api.SheepSleeper;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.phys.Vec3;
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
    Sheep sheep = ((SheepSleeper) entity).getBedSheep();

    if (sheep == null) {
      return;
    }

    var type = ((SheepSleeper) entity).getSleepInSheepType();
    float angle = sheep.getPreciseBodyRotation(partialTicks);

    ((IMixinLivingEntityRenderState) state).setSleepInSheepType(type);
    ((IMixinLivingEntityRenderState) state).setBedSheepYRot(angle);

    Vec3 offset =
        Vec3.Z_AXIS
            .scale(-entity.getType().getDimensions().eyeHeight())
            .yRot((float) Math.toRadians(-angle));

    state.x += offset.x;
    state.y += offset.y;
    state.z += offset.z;

    // 寝ているときの姿勢にする
    // TODO これってここでする必要ある？
    state.xRot = 0.0F;
    state.yRot = 0.0F;
    state.bodyRot = 0.0F;
  }

  @ModifyVariable(method = "setupRotations", at = @At("LOAD"), name = "angle")
  protected float modifySetupRotations(
      float angle, LivingEntityRenderState state, PoseStack poseStack) {
    SheepSleeper.SleepType type = ((IMixinLivingEntityRenderState) state).getSleepInSheepType();

    // 羊の上で寝ているときは身体の回転を羊に合わせる
    if (type == SheepSleeper.SleepType.FACE_UP) {
      // 顔を上にして寝る場合
      return 90.0F - ((IMixinLivingEntityRenderState) state).getBedSheepYRot();
    } else if (type == SheepSleeper.SleepType.FACE_DOWN) {
      // 顔を下にして寝る場合
      poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

      return 90.0F + ((IMixinLivingEntityRenderState) state).getBedSheepYRot();
    } else {
      return angle;
    }
  }
}
