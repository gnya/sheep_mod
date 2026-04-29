package io.github.gnya.sheep_mod.mixins;

import io.github.gnya.sheep_mod.api.IHumanoidRenderStateMixin;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidMobRenderer.class)
public class HumanoidMobRendererMixin {
    @Inject(method = "extractHumanoidRenderState", at = @At("TAIL"))
    private static void extractHumanoidRenderState(
            final LivingEntity entity, final HumanoidRenderState state, final float partialTicks,
            final ItemModelResolver itemModelResolver, CallbackInfo info
    ) {
        if (!entity.isPassenger()) {
            return;
        }

        Entity vehicle = entity.getVehicle();

        if (!(vehicle instanceof Sheep)) {
            return;
        }

        if (!(state instanceof IHumanoidRenderStateMixin customState)) {
            return;
        }

        // 現状、ひとまず羊の上に乗ってさえいればtrueにする
        // TODO 羊の上に乗った状態と羊の上に寝ている状態を区別する
        customState.sheep_mod$setSleepingOnSheep(true);
    }
}
