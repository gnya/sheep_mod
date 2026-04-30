package io.github.gnya.sheep_mod.mixins.sheep;

import io.github.gnya.sheep_mod.api.ISheepRenderStateMixin;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SheepRenderState.class)
@Implements(@Interface(iface = ISheepRenderStateMixin.class, prefix = "sheep_mod$"))
public abstract class SheepRenderStateMixin {
    @Unique
    private boolean sheep_mod$isHappy = false;

    public boolean sheep_mod$isHappy() {
        return this.sheep_mod$isHappy;
    }

    public void sheep_mod$setHappy(boolean value) {
        this.sheep_mod$isHappy = value;
    }
}
