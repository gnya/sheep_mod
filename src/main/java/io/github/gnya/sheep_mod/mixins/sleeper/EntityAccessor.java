package io.github.gnya.sheep_mod.mixins.sleeper;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
  @Accessor("vehicle")
  void setVehicle(final Entity vehicle);

  @Invoker("addPassenger")
  void callAddPassenger(final Entity passenger);
}
