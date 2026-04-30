package io.github.gnya.sheep_mod.mixins.sheep;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.gnya.sheep_mod.SheepMod;
import io.github.gnya.sheep_mod.api.ISheepMixin;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheep.class)
@Implements(@Interface(iface = ISheepMixin.class, prefix = "sheep_mod$"))
public abstract class SheepMixin extends LivingEntity {
    @Unique
    private static final Identifier HAPPY_SHEEP_HEALTH_ID = Identifier.fromNamespaceAndPath(
            "sheep_mod", "happy_sheep/health");

    @Unique
    private static final Identifier HAPPY_SHEEP_SCALE_ID = Identifier.fromNamespaceAndPath(
            "sheep_mod", "happy_sheep/scale");

    @Unique
    private static final float HAPPY_SHEEP_HEALTH = 4.0F;

    @Unique
    private static final float HAPPY_SHEEP_SCALE = 2.0F;

    @Unique
    private static final float HAPPY_SHEEP_PITCH = 0.5F;

    @Shadow
    public abstract boolean isSheared();

    @Unique
    private static final EntityDataAccessor<Boolean> DATA_HAPPY = SynchedEntityData.defineId(
            SheepMixin.class, EntityDataSerializers.BOOLEAN);

    private SheepMixin(EntityType<? extends LivingEntity> type, Level level) {
        // LivingEntityのメンバを使うためのダミーのコンストラクタです
        super(type, level);
    }

    @Unique
    private void private$setHappySheepHealth(boolean happy, boolean lastHappy) {
        AttributeInstance healthAttribute = this.getAttribute(Attributes.MAX_HEALTH);

        if (healthAttribute != null) {
            healthAttribute.removeModifier(HAPPY_SHEEP_HEALTH_ID);

            if (happy) {
                // Happyな羊の場合は体力にバフをかける
                healthAttribute.addPermanentModifier(new AttributeModifier(
                        HAPPY_SHEEP_HEALTH_ID, HAPPY_SHEEP_HEALTH - 1.0,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ));
            }

            if (happy != lastHappy) {
                if (happy) {
                    this.setHealth(this.getHealth() * HAPPY_SHEEP_HEALTH);
                } else {
                    this.setHealth(this.getHealth() / HAPPY_SHEEP_HEALTH);
                }
            }
        }
    }

    @Unique
    private void private$setHappySheepScale(boolean happy) {
        AttributeInstance scaleAttribute = this.getAttribute(Attributes.SCALE);

        if (scaleAttribute != null) {
            scaleAttribute.removeModifier(HAPPY_SHEEP_SCALE_ID);

            if (happy) {
                // Happyな羊の場合は身体が大きくなる
                scaleAttribute.addPermanentModifier(new AttributeModifier(
                        HAPPY_SHEEP_SCALE_ID, HAPPY_SHEEP_SCALE - 1.0,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ));
            }
        }
    }

    public boolean sheep_mod$isHappy() {
        return this.getEntityData().get(DATA_HAPPY);
    }

    public void sheep_mod$setHappy(boolean value) {
        boolean lastValue = this.sheep_mod$isHappy();
        this.private$setHappySheepHealth(value, lastValue);
        this.private$setHappySheepScale(value);
        this.getEntityData().set(DATA_HAPPY, value);
    }

    public boolean sheep_mod$canSleepIn() {
        return this.sheep_mod$isHappy() && !this.isSheared() && !this.isBaby();
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedDataMixin(SynchedEntityData.Builder entityData, CallbackInfo ci) {
        // SynchedDataにDATA_HAPPYを追加する
        entityData.define(DATA_HAPPY, false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    protected void addAdditionalSaveDataMixin(ValueOutput output, CallbackInfo ci) {
        output.putBoolean("Happy", this.sheep_mod$isHappy());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    protected void readAdditionalSaveDataMixin(ValueInput input, CallbackInfo ci) {
        this.sheep_mod$setHappy(input.getBooleanOr("Happy", false));
    }

    @Override
    public void playSound(final @NonNull SoundEvent sound, final float volume, final float pitch) {
        if (this.sheep_mod$isHappy()) {
            // Happyな羊の場合はピッチを下げる
            super.playSound(sound, volume, pitch * HAPPY_SHEEP_PITCH);
        } else {
            super.playSound(sound, volume, pitch);
        }
    }

    @Override
    public @NonNull Vec3 getPassengerRidingPosition(final @NonNull Entity passenger) {
        if (this.sheep_mod$canSleepIn()) {
            // Happyな羊の場合は乗る位置をずらす
            Vec3 offset = Vec3.ZERO;
            float zOffset = -0.5F;

            // 0.6375: EntityType.SHEEP (1.2375 - 0.6) の値
            offset = offset.add(0.0, (8.0 + 1.75) / 16 + 0.6375, -(8.0 + 1.75 + zOffset) / 16);
            offset = offset.scale(HAPPY_SHEEP_SCALE);
            offset = offset.add(Avatar.DEFAULT_VEHICLE_ATTACHMENT);
            offset = offset.yRot((float) Math.toRadians(-this.yBodyRot));

            return this.position().add(offset);
        } else {
            return super.getPassengerRidingPosition(passenger);
        }
    }

    @ModifyReturnValue(
            method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/animal/sheep/Sheep;",
            at = @At(
                    "RETURN"
            )
    )
    public Sheep modifyGetBreedOffspring(Sheep sheep, @Local(argsOnly = true) AgeableMob partner) {
        SheepMod.LOGGER.info("getBreedOffspring");

        if (sheep != null && this.sheep_mod$isHappy() && ((ISheepMixin) partner).isHappy()) {
            // 両親がHappyな羊であれば子もHappyな羊になる
            ((ISheepMixin) sheep).setHappy(true);
        }

        return sheep;
    }

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
    public void finalizeSpawnMixin(
            ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnReason,
            SpawnGroupData groupData, CallbackInfoReturnable<SpawnGroupData> cir
    ) {
        int selection = level.getRandom().nextInt(10);

        if (selection < 5) {
            this.sheep_mod$setHappy(true);
        }
    }
}
