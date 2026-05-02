package io.github.gnya.sheep_mod.mixins.gui;

import io.github.gnya.sheep_mod.api.ILivingEntityMixin;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @ModifyVariable(method = "getPlayerVehicleWithHealth", at = @At("STORE"), name = "player")
    public Player modifyGetPlayerVehicleWithHealth(Player player) {
        if (((ILivingEntityMixin) player).isSleepInSheep()) {
            // 羊の上で寝ているときには羊の体力バーを消す
            return null;
        } else {
            return player;
        }
    }
}
