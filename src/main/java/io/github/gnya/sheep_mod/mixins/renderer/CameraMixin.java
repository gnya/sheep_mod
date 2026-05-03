package io.github.gnya.sheep_mod.mixins.renderer;

import io.github.gnya.sheep_mod.api.SheepSleeper;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
  @Shadow private @Nullable Entity entity;

  @Shadow
  protected abstract void move(float forwards, float up, float right);

  @Shadow
  protected abstract void setRotation(float yRot, float xRot);

  @Shadow
  protected abstract float getMaxZoom(float cameraDist);

  @Shadow
  public abstract boolean isDetached();

  @Redirect(
      method = "alignWithEntity",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"))
  private boolean redirectAlignWithEntity(LivingEntity entity) {
    return entity.isSleeping() && !((SheepSleeper) entity).isSleepInSheep();
  }

  @Inject(method = "alignWithEntity", at = @At("TAIL"))
  private void alignWithEntity(float partialTicks, CallbackInfo ci) {
    if (!this.isDetached()
        && this.entity instanceof LivingEntity
        && ((SheepSleeper) entity).isSleepInSheep()) {
      // TODO getBedSheep()を追加する
      if (entity.getVehicle() instanceof Sheep sheep) {
        float angle = sheep.getPreciseBodyRotation(partialTicks);

        this.setRotation(angle, 40.0F);
        this.move(-this.getMaxZoom(sheep.getScale()), 0.5F, 0.0F);
      }
    }
  }
}
