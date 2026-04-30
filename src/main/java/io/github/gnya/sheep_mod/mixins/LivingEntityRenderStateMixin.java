package io.github.gnya.sheep_mod.mixins;

import io.github.gnya.sheep_mod.api.ILivingEntityRenderStateMixin;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HumanoidRenderState.class)
@Implements(@Interface(iface = ILivingEntityRenderStateMixin.class, prefix = "sheep_mod$"))
public abstract class LivingEntityRenderStateMixin {
    @Unique
    private boolean sheep_mod$isSleepingOnSheep;

    @Unique
    private float sheep_mod$vehicleSheepYRot;

    public boolean sheep_mod$isSleepingOnSheep() {
        return this.sheep_mod$isSleepingOnSheep;
    }

    public void sheep_mod$setSleepingOnSheep(boolean value) {
        this.sheep_mod$isSleepingOnSheep = value;
    }

    public float sheep_mod$getVehicleSheepYRot() {
        return this.sheep_mod$vehicleSheepYRot;
    }

    public void sheep_mod$setVehicleSheepYRot(float value) {
        this.sheep_mod$vehicleSheepYRot = value;
    }
}
