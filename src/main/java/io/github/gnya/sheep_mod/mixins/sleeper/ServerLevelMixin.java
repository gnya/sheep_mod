package io.github.gnya.sheep_mod.mixins.sleeper;

import io.github.gnya.sheep_mod.api.SheepSleeper;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
  @Shadow @Final private List<ServerPlayer> players;

  @Shadow
  protected abstract void wakeUpAllPlayers();

  @Redirect(
      method = "tick",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/server/level/ServerLevel;wakeUpAllPlayers()V"))
  public void redirectTick(ServerLevel level) {
    // Happyな羊の上で寝ると最大体力の30%が回復します
    this.players.stream()
        .filter(LivingEntity::isSleeping)
        .filter(player -> ((SheepSleeper) player).isSleepInSheep())
        .forEach(player -> player.setHealth(player.getHealth() + player.getMaxHealth() * 0.3F));
    this.wakeUpAllPlayers();
  }
}
