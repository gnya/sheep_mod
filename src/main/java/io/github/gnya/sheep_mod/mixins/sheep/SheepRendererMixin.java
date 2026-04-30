package io.github.gnya.sheep_mod.mixins.sheep;

import io.github.gnya.sheep_mod.api.ISheepMixin;
import io.github.gnya.sheep_mod.api.ISheepRenderStateMixin;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepRenderer.class)
public class SheepRendererMixin {
    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/animal/sheep/Sheep;Lnet/minecraft/client/renderer/entity/state/SheepRenderState;F)V",
            at = @At("TAIL")
    )
    public void extractRenderState(Sheep entity, SheepRenderState state, float partialTicks, CallbackInfo ci) {
        if (((ISheepMixin) entity).isHappy()) {
            ((ISheepRenderStateMixin) state).setHappy(true);

            // TODO デバッグのためHappyな羊の色がオレンジで表示されるようにします
            state.woolColor = DyeColor.ORANGE;
        } else {
            // TODO デバッグのためHappyでない羊の色が白で表示されるようにします
            state.woolColor = DyeColor.WHITE;
        }
    }
}
