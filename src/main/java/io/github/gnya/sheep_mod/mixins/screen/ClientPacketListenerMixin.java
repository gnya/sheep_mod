package io.github.gnya.sheep_mod.mixins.screen;

import io.github.gnya.sheep_mod.api.IMixinClientboundSetPassengersPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.BitSet;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin extends ClientCommonPacketListenerImpl {
    @Shadow
    private ClientLevel level;

    private ClientPacketListenerMixin(Minecraft minecraft, Connection connection, CommonListenerCookie cookie) {
        // ダミーコンストラクタ
        super(minecraft, connection, cookie);
    }

    @ModifyVariable(method = "handleSetEntityPassengersPacket", at = @At("STORE"), name = "wasPlayerMounted")
    public boolean modifyHandleSetEntityPassengersPacket(boolean wasPlayerMounted, ClientboundSetPassengersPacket packet) {
        int[] passengerId = packet.getPassengers();
        BitSet isSleepInSheep = ((IMixinClientboundSetPassengersPacket) packet).getIsSleepInSheep();

        for (int i = 0; i < passengerId.length; i++) {
            Entity passenger = this.level.getEntity(passengerId[i]);

            if (passenger == this.minecraft.player && isSleepInSheep.get(i)) {
                // 羊の上で寝ているときにはオーバーレイのメッセージを出さない
                wasPlayerMounted = true;
            }
        }

        return wasPlayerMounted;
    }
}
