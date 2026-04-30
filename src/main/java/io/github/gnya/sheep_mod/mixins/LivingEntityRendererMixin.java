package io.github.gnya.sheep_mod.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.gnya.sheep_mod.api.ILivingEntityRenderStateMixin;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.sheep.Sheep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Inject(method = "setupRotations", at = @At("TAIL"))
    protected void setupRotations(
            final LivingEntityRenderState state, final PoseStack poseStack, float bodyRot, final float entityScale,
            CallbackInfo ci
    ) {
        if (!(state instanceof ILivingEntityRenderStateMixin customState)) {
            return;
        }

        if (!customState.isSleepingOnSheep()) {
            return;
        }

        if (state.hasPose(Pose.SLEEPING)) {
            Direction direction = state.bedOrientation;
            float angle = bodyRot;

            if (direction != null) {
                angle = switch (direction) {
                    case SOUTH -> 90.0F;
                    case WEST -> 0.0F;
                    case NORTH -> 270.0F;
                    case EAST -> 180.0F;
                    default -> 0.0F;
                };
            }

            // SLEEPINGのときの姿勢を元に戻す
            poseStack.mulPose(Axis.YP.rotationDegrees(-270.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(-angle));
        }

        float sheepAngle = customState.getVehicleSheepYRot() - 90F;

        // 正しいangleを計算して再び回転させる
        poseStack.mulPose(Axis.YP.rotationDegrees(-sheepAngle));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
    }

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At("TAIL")
    )
    public void extractRenderState(
            final LivingEntity entity, final LivingEntityRenderState state, final float partialTicks,
            CallbackInfo ci
    ) {
        if (!(state instanceof ILivingEntityRenderStateMixin customState)) {
            return;
        }

        if (!entity.isPassenger()) {
            return;
        }

        if (!(entity.getVehicle() instanceof Sheep vehicle)) {
            return;
        }

        float angle = vehicle.getPreciseBodyRotation(partialTicks);

        // 現状、ひとまず羊の上に乗ってさえいればtrueにする
        // TODO 羊の上に乗った状態と羊の上に寝ている状態を区別する
        customState.setSleepingOnSheep(true);
        customState.setVehicleSheepYRot(angle);

        // 身体の向きを動かさない
        state.bodyRot = 180.0F;
        // 頭のY軸回転を無効にする
        state.yRot = 0.0F;
    }
}
