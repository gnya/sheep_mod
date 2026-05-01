package io.github.gnya.sheep_mod.api;

public interface ILivingEntityRenderStateMixin {
    boolean isSleepingInSheep();

    void setSleepingInSheep(final boolean value);

    float getBedSheepYRot();

    void setBedSheepYRot(final float value);
}
