package io.github.gnya.sheep_mod.api;

public interface ILivingEntityRenderStateMixin {
    boolean isSleepingOnSheep();

    void setSleepingOnSheep(boolean value);

    float getVehicleSheepYRot();

    void setVehicleSheepYRot(float value);
}
