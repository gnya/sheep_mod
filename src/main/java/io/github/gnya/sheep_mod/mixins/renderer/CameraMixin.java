package io.github.gnya.sheep_mod.mixins.renderer;

import io.github.gnya.sheep_mod.api.SheepSleeper;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.phys.Vec3;
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
  @Shadow private Vec3 position;

  @Shadow
  protected abstract void move(float forwards, float up, float right);

  @Shadow
  protected abstract void setRotation(float yRot, float xRot);

  @Shadow
  protected abstract float getMaxZoom(float cameraDist);

  @Shadow
  public abstract boolean isDetached();

  @Shadow
  protected abstract void setPosition(Vec3 position);

  @Redirect(
      method = "alignWithEntity",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z"))
  private boolean redirectAlignWithEntity(LivingEntity entity) {
    return entity.isSleeping() && !((SheepSleeper) entity).isSleepInSheep();
  }

  @Inject(method = "alignWithEntity", at = @At("TAIL"))
  private void alignWithEntity(float partialTicks, CallbackInfo ci) {
    if (!this.isDetached() && this.entity instanceof LivingEntity) {
      Sheep sheep = ((SheepSleeper) this.entity).getBedSheep();

      if (sheep != null) {
        float angle = sheep.getPreciseBodyRotation(partialTicks);
        Vec3 offset = Vec3.Z_AXIS.scale(0.4).yRot((float) Math.toRadians(-angle));

        this.setPosition(this.position.add(offset));
        this.setRotation(angle + 180.0F, 45.0F);
        this.move(-this.getMaxZoom(sheep.getScale()), 0.0F, 0.0F);
      }
    }
  }
}
