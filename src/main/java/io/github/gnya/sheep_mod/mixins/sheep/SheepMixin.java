package io.github.gnya.sheep_mod.mixins.sheep;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.gnya.sheep_mod.SheepMod;
import io.github.gnya.sheep_mod.api.ISheepMixin;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    private static final float HAPPY_SHEEP_HEALTH_SCALE = 4.0F;

    @Unique
    private static final float HAPPY_SHEEP_SCALE = 2.0F;

    @Unique
    private static final float HAPPY_SHEEP_PITCH_SCALE = 0.5F;

    @Unique
    private static final int HAPPY_SHEEP_DROP_SCALE = 4;

    @Unique
    private static final Identifier HAPPY_SHEEP_HEALTH_ID = Identifier.fromNamespaceAndPath(
            "sheep_mod", "happy_sheep/health");

    @Unique
    private static final Identifier HAPPY_SHEEP_SCALE_ID = Identifier.fromNamespaceAndPath(
            "sheep_mod", "happy_sheep/scale");

    @Unique
    private static final EntityDataAccessor<Boolean> DATA_HAPPY = SynchedEntityData.defineId(
            SheepMixin.class, EntityDataSerializers.BOOLEAN);

    private SheepMixin(EntityType<? extends LivingEntity> type, Level level) {
        // ダミーコンストラクタ
        super(type, level);
    }

    @Shadow
    public abstract boolean isSheared();

    @Shadow
    public abstract DyeColor getColor();

    @Unique
    private void private$setHappySheepHealth(boolean happy, boolean lastHappy) {
        AttributeInstance healthAttribute = this.getAttribute(Attributes.MAX_HEALTH);

        if (healthAttribute != null) {
            healthAttribute.removeModifier(HAPPY_SHEEP_HEALTH_ID);

            if (happy) {
                // Happyな羊の場合は体力にバフをかける
                healthAttribute.addPermanentModifier(new AttributeModifier(
                        HAPPY_SHEEP_HEALTH_ID, HAPPY_SHEEP_HEALTH_SCALE - 1.0,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ));
            }

            if (happy != lastHappy) {
                if (happy) {
                    this.setHealth(this.getHealth() * HAPPY_SHEEP_HEALTH_SCALE);
                } else {
                    this.setHealth(this.getHealth() / HAPPY_SHEEP_HEALTH_SCALE);
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
        return this.entityData.get(DATA_HAPPY);
    }

    public void sheep_mod$setHappy(final boolean value) {
        boolean lastValue = this.sheep_mod$isHappy();
        this.private$setHappySheepHealth(value, lastValue);
        this.private$setHappySheepScale(value);
        this.entityData.set(DATA_HAPPY, value);
    }

    public boolean sheep_mod$canSleepIn() {
        return this.sheep_mod$isHappy() && !this.isSheared() && !this.isBaby();
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedDataMixin(SynchedEntityData.Builder entityData, CallbackInfo ci) {
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

    @Inject(method = "aiStep", at = @At("HEAD"))
    public void aiStep(CallbackInfo ci) {
        if (this.level().isClientSide() && this.sheep_mod$isHappy() && !this.isSheared()) {
            // Happyな羊からパーティクルを出す
            this.level()
                    .addParticle(
                            new DustParticleOptions(this.getColor().getTextureDiffuseColor(), 1.0F),
                            this.getRandomX(0.7),
                            this.getRandomY(),
                            this.getRandomZ(0.7),
                            0.0,
                            0.0,
                            0.0
                    );
        }
    }

    @Override
    public void playSound(final @NonNull SoundEvent sound, final float volume, final float pitch) {
        float scaledPitch = pitch;

        if (this.sheep_mod$isHappy()) {
            // Happyな羊の場合はピッチを下げる
            scaledPitch *= HAPPY_SHEEP_PITCH_SCALE;
        }

        super.playSound(sound, volume, scaledPitch);
    }

    @Override
    public ItemEntity spawnAtLocation(
            final @NonNull ServerLevel level, final @NonNull ItemStack itemStack, final float offset) {
        int reward = itemStack.getCount();

        if (this.sheep_mod$isHappy()) {
            // Happyな羊の場合はドロップが多くなる
            reward *= HAPPY_SHEEP_DROP_SCALE;
        }

        return super.spawnAtLocation(level, itemStack.copyWithCount(reward), offset);
    }

    @Override
    protected int getBaseExperienceReward(final @NonNull ServerLevel level) {
        int reward = super.getBaseExperienceReward(level);

        if (this.sheep_mod$isHappy()) {
            // Happyな羊の場合は経験値が多くなる
            reward *= HAPPY_SHEEP_DROP_SCALE;
        }

        return reward;
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
            // 一定確率でHappyな羊が生まれる
            this.sheep_mod$setHappy(true);
        }
    }
}
