package io.github.gnya.sheep_mod.mixins.sleeper;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.gnya.sheep_mod.SheepMod;
import io.github.gnya.sheep_mod.api.IMixinSheep;
import io.github.gnya.sheep_mod.api.SheepSleeper;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
@Implements(@Interface(iface = SheepSleeper.class, prefix = "sheep_mod$"))
public abstract class LivingEntityMixin extends Entity {
  @Unique
  private static final EntityDataAccessor<Integer> DATA_SLEEP_IN_SHEEP =
      SynchedEntityData.defineId(LivingEntityMixin.class, EntityDataSerializers.INT);

  private LivingEntityMixin(EntityType<?> type, Level level) {
    // ダミーコンストラクタ
    super(type, level);
  }

  // LivingEntity.stopRiding()を呼び出すために必要
  @Shadow
  public abstract void stopRiding();

  @Shadow
  public abstract boolean isSleeping();

  @Shadow
  public abstract @NonNull EntityDimensions getDimensions(@NonNull Pose pose);

  @Shadow
  public abstract boolean isAlive();

  public SheepSleeper.SleepType sheep_mod$getSleepInSheepType() {
    return SheepSleeper.SleepType.values()[this.entityData.get(DATA_SLEEP_IN_SHEEP)];
  }

  @Unique
  private void private$setSleepInSheepType(SheepSleeper.SleepType type) {
    this.entityData.set(DATA_SLEEP_IN_SHEEP, type.ordinal());
  }

  public boolean sheep_mod$isSleepInSheep() {
    return this.sheep_mod$getSleepInSheepType() != SheepSleeper.SleepType.NONE;
  }

  public @Nullable Sheep sheep_mod$getBedSheep() {
    if (this.sheep_mod$isSleepInSheep()) {
      return (Sheep) this.getVehicle();
    } else {
      return null;
    }
  }

  @Override
  public @NonNull Vec3 getVehicleAttachmentPoint(final @NonNull Entity vehicle) {
    if (this.sheep_mod$isSleepInSheep() && vehicle instanceof Sheep sheep) {
      // 羊の上で寝たときの頭の相対位置を返します
      // x: 0.0
      // y: -(8.0 + 1.75) / 16
      // z: (8.0 + 1.75 - ZOffset(0.5)) / 16 - EyeHeight(1.62) / SheepScale(2.0)
      return this.getType()
          .getDimensions()
          .attachments()
          .get(EntityAttachment.VEHICLE, 0, 0.0F)
          .add(0.0, -0.609375, -0.231875)
          .scale(sheep.getScale())
          .yRot((float) Math.toRadians(-sheep.yBodyRot));
    } else {
      return super.getVehicleAttachmentPoint(vehicle);
    }
  }

  @ModifyReturnValue(method = "checkBedExists", at = @At("RETURN"))
  private boolean modifyCheckBedExists(boolean exists) {
    Sheep sheep = this.sheep_mod$getBedSheep();

    return sheep != null ? ((IMixinSheep) sheep).canSleepIn() : exists;
  }

  @ModifyReturnValue(method = "isSleeping", at = @At("RETURN"))
  public boolean modifyIsSleeping(boolean sleep) {
    return sleep || this.sheep_mod$isSleepInSheep();
  }

  public void sheep_mod$startSleeping(final Sheep sheep) {
    this.sheep_mod$LivingEntity$startSleeping(sheep);
  }

  public void sheep_mod$LivingEntity$startSleeping(final Sheep sheep) {
    SheepMod.LOGGER.debug("LivingEntity$startSleeping");

    if (!((IMixinSheep) sheep).canSleepIn()
        || this.isSleeping()
        || !this.canRide(sheep)
        || !sheep.getPassengers().isEmpty()) {
      return;
    }

    if (this.isPassenger()) {
      this.stopRiding();
    }

    this.setPose(Pose.SLEEPING);
    this.private$setSleepInSheepType(
        this.random.nextBoolean()
            ? SheepSleeper.SleepType.FACE_UP
            : SheepSleeper.SleepType.FACE_DOWN);
    this.setDeltaMovement(Vec3.ZERO);
    ((EntityAccessor) this).setVehicle(sheep);
    ((EntityAccessor) sheep).callAddPassenger(this);
    sheep.playSound(SoundEvents.WOOL_HIT, 1.0F, this.random.triangle(1.0F, 0.2F));
    this.needsSync = true;
  }

  @Inject(method = "stopSleeping", at = @At("HEAD"), cancellable = true)
  public void stopSleeping(CallbackInfo ci) {
    if (!this.sheep_mod$isSleepInSheep()) {
      return;
    }

    SheepMod.LOGGER.debug("stopSleeping: %s".formatted(this));

    this.stopRiding();
    this.setPose(Pose.STANDING);
    this.private$setSleepInSheepType(SheepSleeper.SleepType.NONE);
    ci.cancel();
  }

  @Inject(method = "defineSynchedData", at = @At("TAIL"))
  protected void defineSynchedDataMixin(SynchedEntityData.Builder entityData, CallbackInfo ci) {
    entityData.define(DATA_SLEEP_IN_SHEEP, SheepSleeper.SleepType.NONE.ordinal());
  }

  @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
  protected void addAdditionalSaveDataMixin(ValueOutput output, CallbackInfo ci) {
    output.putInt("SleepInSheep", this.sheep_mod$getSleepInSheepType().ordinal());
  }

  @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
  protected void readAdditionalSaveDataMixin(ValueInput input, CallbackInfo ci) {
    int ordinal = input.getIntOr("SleepInSheep", SheepSleeper.SleepType.NONE.ordinal());

    this.private$setSleepInSheepType(SheepSleeper.SleepType.values()[ordinal]);
  }

  @Inject(method = "tick", at = @At("TAIL"))
  public void tick(CallbackInfo ci) {
    if (this.level().isClientSide()
        && this.isAlive()
        && this.isSleeping()
        && this.tickCount % 30 == 0) {
      // 寝ているエンティティからいびきのパーティクルを出す
      this.level()
          .addParticle(
              SheepMod.SLEEP_PARTICLE.get(),
              this.getRandomX(0.7),
              this.getRandomY() + 0.3,
              this.getRandomZ(0.7),
              0.0,
              0.0,
              0.0);
    }
  }
}
