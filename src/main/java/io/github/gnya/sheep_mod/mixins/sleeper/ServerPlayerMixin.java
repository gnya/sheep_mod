package io.github.gnya.sheep_mod.mixins.sleeper;

import com.mojang.authlib.GameProfile;
import io.github.gnya.sheep_mod.SheepMod;
import io.github.gnya.sheep_mod.api.PlayableSheepSleeper;
import io.github.gnya.sheep_mod.api.SheepSleeper;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.*;

@Mixin(ServerPlayer.class)
@Implements(@Interface(iface = PlayableSheepSleeper.class, prefix = "sheep_mod$"))
public abstract class ServerPlayerMixin extends Player {
    @Shadow
    public ServerGamePacketListenerImpl connection;

    @Shadow
    @Final
    private MinecraftServer server;

    public ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    public void sheep_mod$startSleeping(final Sheep sheep) {
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        // super.startSleeping(Sheep sheep) の代わり
        ((SheepSleeper) this).LivingEntity$startSleeping(sheep);
        // ServerPlayer.startRidingの中身と同じ処理です
        sheep.positionRider(this);
        this.connection.teleport(new PositionMoveRotation(this.position(), Vec3.ZERO, 0.0F, 0.0F), Relative.ROTATION);
        this.server.getPlayerList().sendActiveEffects(sheep, this.connection);
        this.connection.send(new ClientboundSetPassengersPacket(sheep));

        SheepMod.LOGGER.info("START SLEEP (Server)");
    }
}
