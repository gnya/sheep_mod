package io.github.gnya.sheep_mod;

import com.mojang.logging.LogUtils;
import io.github.gnya.sheep_mod.api.ILivingEntityMixin;
import io.github.gnya.sheep_mod.api.IPlayerMixin;
import io.github.gnya.sheep_mod.api.ISheepMixin;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Result;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
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
    public static void onSleepingLocationCheck(SleepingLocationCheckEvent event) {
        // TODO 動いてる？
        LivingEntity entity = event.getEntity();

        if (entity instanceof ILivingEntityMixin) {
            if (((ILivingEntityMixin) entity).isSleepInSheep()) {
                // TODO getBedSheep()を追加する
                Entity vehicle = entity.getVehicle();

                if (vehicle instanceof Sheep sheep && ((ISheepMixin) sheep).canSleepIn()) {
                    SheepMod.LOGGER.info("Allow Sleep!: " + entity.isSleeping());
                    event.setResult(Result.ALLOW);
                }
            }
        }
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
        ((IPlayerMixin) player).startSleepInBed((Sheep) sheep);
    }
}
