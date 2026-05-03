package io.github.gnya.sheep_mod.mixins.renderer;

import io.github.gnya.sheep_mod.api.IMixinLivingEntityRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin {
  @Redirect(
      method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V",
      at =
          @At(
              value = "FIELD",
              target =
                  "Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;isPassenger:Z",
              opcode = Opcodes.GETFIELD))
  public boolean redirectSetupAnim(final HumanoidRenderState state) {
    // 羊の上で寝ている場合にはEntityの上に乗ったときの姿勢にしない
    return state.isPassenger && !((IMixinLivingEntityRenderState) state).isSleepInSheep();
  }
}
