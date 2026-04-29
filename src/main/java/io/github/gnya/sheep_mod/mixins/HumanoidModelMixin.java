package io.github.gnya.sheep_mod.mixins;

import io.github.gnya.sheep_mod.api.IHumanoidRenderStateMixin;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {
    @Shadow
    @Final
    public ModelPart rightArm;
    @Shadow
    @Final
    public ModelPart leftArm;
    @Shadow
    @Final
    public ModelPart rightLeg;
    @Shadow
    @Final
    public ModelPart leftLeg;

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
    public void setupAnim(final HumanoidRenderState state, CallbackInfo info) {
        float animationPos = state.walkAnimationPos;
        float animationSpeed = state.walkAnimationSpeed;

        if (!(state instanceof IHumanoidRenderStateMixin customState)) {
            return;
        }

        if (customState.sheep_mod$isSleepingOnSheep()) {
            // 姿勢を上書きする
            this.rightArm.xRot = Mth.cos(
                    animationPos * 0.6662F + (float) Math.PI) * 2.0F * animationSpeed * 0.5F / state.speedValue;
            this.leftArm.xRot = Mth.cos(animationPos * 0.6662F) * 2.0F * animationSpeed * 0.5F / state.speedValue;

            this.rightLeg.xRot = Mth.cos(0.0F * 0.6662F) * 1.4F * 0.0F / state.speedValue;
            this.rightLeg.yRot = 0.005F;
            this.rightLeg.zRot = 0.005F;

            this.leftLeg.xRot = Mth.cos(0.0F * 0.6662F + (float) Math.PI) * 1.4F * 0.0F / state.speedValue;
            this.leftLeg.yRot = -0.005F;
            this.leftLeg.zRot = -0.005F;
        }
    }
}