package io.github.gnya.sheep_mod.mixins.screen;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.gnya.sheep_mod.api.IMixinClientboundSetPassengersPacket;
import io.github.gnya.sheep_mod.api.SheepSleeper;
import java.util.BitSet;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundSetPassengersPacket.class)
@Implements(@Interface(iface = IMixinClientboundSetPassengersPacket.class, prefix = "sheep_mod$"))
public abstract class ClientboundSetPassengersPacketMixin {
  @Unique private BitSet sheep_mod$isSleepInSheep;

  @Inject(method = "<init>(Lnet/minecraft/world/entity/Entity;)V", at = @At("TAIL"))
  public void onInit(
      Entity vehicle, CallbackInfo ci, @Local(name = "entities") List<Entity> entities) {
    this.sheep_mod$isSleepInSheep = new BitSet(entities.size());

    for (int i = 0; i < entities.size(); i++) {
      Entity passenger = entities.get(i);
      boolean isSleepInSheep =
          passenger instanceof SheepSleeper && ((SheepSleeper) passenger).isSleepInSheep();

      this.sheep_mod$isSleepInSheep.set(i, isSleepInSheep);
    }
  }

  @Inject(method = "<init>(Lnet/minecraft/network/FriendlyByteBuf;)V", at = @At("TAIL"))
  public void onInit(FriendlyByteBuf input, CallbackInfo ci) {
    this.sheep_mod$isSleepInSheep = input.readBitSet();
  }

  @Inject(method = "write", at = @At("TAIL"))
  public void write(FriendlyByteBuf output, CallbackInfo ci) {
    output.writeBitSet(this.sheep_mod$isSleepInSheep);
  }

  public BitSet sheep_mod$getIsSleepInSheep() {
    return this.sheep_mod$isSleepInSheep;
  }
}
