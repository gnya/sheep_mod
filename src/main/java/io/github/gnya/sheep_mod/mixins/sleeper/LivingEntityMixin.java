package io.github.gnya.sheep_mod.mixins.sleeper;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.gnya.sheep_mod.SheepMod;
import io.github.gnya.sheep_mod.api.SheepSleeper;
import io.github.gnya.sheep_mod.api.IMixinSheep;
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
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
@Implements(@Interface(iface = SheepSleeper.class, prefix = "sheep_mod$"))
public abstract class LivingEntityMixin extends Entity {
    @Unique
    private static final EntityDataAccessor<Boolean> DATA_SLEEP_IN_SHEEP = SynchedEntityData.defineId(
            LivingEntityMixin.class, EntityDataSerializers.BOOLEAN);

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

    public boolean sheep_mod$isSleepInSheep() {
        return this.entityData.get(DATA_SLEEP_IN_SHEEP);
    }

    @Override
    public @NonNull Vec3 getVehicleAttachmentPoint(final @NonNull Entity vehicle) {
        if (this.sheep_mod$isSleepInSheep() && vehicle instanceof Sheep) {
            float zOffset = -0.5F;
            Vec3 offset = this.getDimensions(Pose.STANDING).attachments().get(EntityAttachment.VEHICLE, 0, 0.0F);

            offset = offset.add(0.0, -(8.0 + 1.75) / 16, (8.0 + 1.75 + zOffset) / 16);
            offset = offset.scale(((Sheep) vehicle).getScale());

            return offset.yRot((float) Math.toRadians(-((Sheep) vehicle).yBodyRot));
        } else {
            return super.getVehicleAttachmentPoint(vehicle);
        }
    }

    @ModifyReturnValue(method = "checkBedExists", at = @At("RETURN"))
    private boolean modifyCheckBedExists(boolean exists) {
        if (this.sheep_mod$isSleepInSheep()) {
            // TODO getBedSheep()を追加する
            Entity vehicle = this.getVehicle();

            return vehicle instanceof Sheep sheep && ((IMixinSheep) sheep).canSleepIn();
        } else {
            return exists;
        }
    }

    @ModifyReturnValue(method = "isSleeping", at = @At("RETURN"))
    public boolean modifyIsSleeping(boolean sleep) {
        return sleep || this.sheep_mod$isSleepInSheep();
    }

    public void sheep_mod$startSleeping(final Sheep sheep) {
        this.sheep_mod$LivingEntity$startSleeping(sheep);
    }

    public void sheep_mod$LivingEntity$startSleeping(final Sheep sheep) {
        SheepMod.LOGGER.info("LivingEntity$startSleeping");

        if (!((IMixinSheep) sheep).canSleepIn()) {
            return;
        } else if (this.isSleeping()) {
            return;
        } else if (!this.canRide(sheep)) {
            return;
        } else if (!sheep.getPassengers().isEmpty()) {
            return;
        }

        if (this.isPassenger()) {
            this.stopRiding();
        }

        this.setPose(Pose.SLEEPING);
        this.entityData.set(DATA_SLEEP_IN_SHEEP, true);
        this.setDeltaMovement(Vec3.ZERO);
        ((EntityAccessor) this).setVehicle(sheep);
        ((EntityAccessor) sheep).callAddPassenger(this);
        sheep.playSound(SoundEvents.WOOL_HIT, 1.0F, this.random.triangle(1.0F, 0.2F));
        this.needsSync = true;
    }

    @Inject(method = "stopSleeping", at = @At("HEAD"), cancellable = true)
    public void stopSleeping(CallbackInfo ci) {
        SheepMod.LOGGER.info("stopSleeping: %s".formatted(this));

        if (!this.sheep_mod$isSleepInSheep()) {
            return;
        }

        this.stopRiding();
        this.setPose(Pose.STANDING);
        this.entityData.set(DATA_SLEEP_IN_SHEEP, false);
        ci.cancel();
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedDataMixin(SynchedEntityData.Builder entityData, CallbackInfo ci) {
        entityData.define(DATA_SLEEP_IN_SHEEP, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    protected void addAdditionalSaveDataMixin(ValueOutput output, CallbackInfo ci) {
        output.putBoolean("SleepInSheep", this.sheep_mod$isSleepInSheep());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    protected void readAdditionalSaveDataMixin(ValueInput input, CallbackInfo ci) {
        this.entityData.set(DATA_SLEEP_IN_SHEEP, input.getBooleanOr("SleepInSheep", false));
    }
}
