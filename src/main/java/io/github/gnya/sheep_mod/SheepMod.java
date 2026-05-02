package io.github.gnya.sheep_mod;

import com.mojang.logging.LogUtils;
import io.github.gnya.sheep_mod.api.PlayableSheepSleeper;
import io.github.gnya.sheep_mod.api.ISheepMixin;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(SheepMod.MODID)
public class SheepMod {
    public static final String MODID = "sheep_mod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SheepMod() {
        MinecraftForge.EVENT_BUS.register(SheepMod.class);
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getSide().isClient()) {
            return;
        } else if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        Entity target = event.getTarget();

        if (!(target instanceof ISheepMixin sheep)) {
            return;
        } else if (!sheep.canSleepIn()) {
            return;
        }

        ServerPlayer player = (ServerPlayer) event.getEntity();

        if (player.isSleeping() || !player.isAlive()) {
            return;
        }

        // プレイヤーを羊の上に寝かせる
        ((PlayableSheepSleeper) player).startSleepInBed((Sheep) sheep).ifLeft(problem -> {
            if (problem.message() != null) {
                player.sendOverlayMessage(problem.message());
            }
        });
    }
}
