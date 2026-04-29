package io.github.gnya.sheep_mod.mixins;

import io.github.gnya.sheep_mod.api.IHumanoidRenderStateMixin;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HumanoidRenderState.class)
public class HumanoidRenderStateMixin implements IHumanoidRenderStateMixin {
    @Unique
    private boolean sheep_mod$isSleepingOnSheep;

    @Override
    public boolean sheep_mod$isSleepingOnSheep() {
        return this.sheep_mod$isSleepingOnSheep;
    }

    @Override
    public void sheep_mod$setSleepingOnSheep(boolean value) {
        this.sheep_mod$isSleepingOnSheep = value;
    }
}
