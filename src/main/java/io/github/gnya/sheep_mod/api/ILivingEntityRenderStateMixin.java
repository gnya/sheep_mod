package io.github.gnya.sheep_mod.api;

public interface ILivingEntityRenderStateMixin {
    boolean isSleepInSheep();

    void setSleepInSheep(final boolean value);

    float getBedSheepYRot();

    void setBedSheepYRot(final float value);
}
