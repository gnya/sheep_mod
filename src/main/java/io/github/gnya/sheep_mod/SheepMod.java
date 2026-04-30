package io.github.gnya.sheep_mod;

import com.mojang.logging.LogUtils;
import io.github.gnya.sheep_mod.api.ISheepMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }

        // ひとまず強制的に眠らせる
        // TODO ここではなくisSleepOnSheepを監視して眠っている状態が解除されないようにする
        event.setResult(Result.ALLOW);
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
        }

        if (!sheep.canSleepIn()) {
            return;
        }

        ServerPlayer player = (ServerPlayer) event.getEntity();

        if (player.isSleeping() || !player.isAlive()) {
            return;
        }

        // 羊の上で眠る処理
        // * スポーンポイントは設定しない
        // * 羊の上に寝っ転がった状態になる
        //  - 羊に回転を追従させる
        // * Bed関係のイベントは発火させない
        //  - 余裕があれば今後専用イベントを追加
        // - 近くに敵がいる場合は眠れないようにする
        //  - メッセージを表示
        // - 最終的にはplayer.isCreative()がtrueの場合は寝させない
        // - BedRuleに相当するRuleを実装してメッセージを流せるようにする
        BlockPos pos = target.blockPosition();
        ServerLevel level = player.level();

        double hRange = 8.0;
        double vRange = 5.0;

        /*
        Vec3 bedCenter = Vec3.atBottomCenterOf(pos);
        List<Monster> monsters = level
                .getEntitiesOfClass(
                        Monster.class,
                        new AABB(
                                bedCenter.x() - hRange,
                                bedCenter.y() - vRange,
                                bedCenter.z() - hRange,
                                bedCenter.x() + hRange,
                                bedCenter.y() + vRange,
                                bedCenter.z() + hRange
                        ),
                        monster -> monster.isPreventingPlayerRest(level, player)
                );

        if (!monsters.isEmpty()) {
            // TODO ベッドと同様のメッセージを表示したい
            LOGGER.info(Player.BedSleepingProblem.NOT_SAFE.toString());

            return;
        }

        LOGGER.info("Start Sleeping...");
        player.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        if (player.isPassenger()) {
            player.stopRiding();
        }

        // TODO 羊の上で誰か既に寝ている場合は眠れないようにする
        // BlockState blockState = level.getBlockState(pos);
        // if (blockState.isBed(level, pos, player)) {
        //     blockState.setBedOccupied(level, pos, player, true);
        // }

        // ここでどんなポーズに設定してもどこかでPose.SLEEPINGで上書きされる
        player.setPose(Pose.SLEEPING);
        player.setPos(pos.getX() + 0.5, pos.getY() + 0.6875, pos.getZ() + 0.5);
        player.setSleepingPos(pos);
        player.setDeltaMovement(Vec3.ZERO);
        player.needsSync = true;

        // やりたくない…ですが、今のところこれしか方法が無いので…
        // TODO mixinでstartSleepOnSheep()を追加した方が良さそう（こんなことしなくて良くなる）
        Field sleepCounter = Player.class.getDeclaredField("sleepCounter");

        sleepCounter.setAccessible(true);
        sleepCounter.set(player, 0);
        sleepCounter.setAccessible(false);

        // 実績・統計周りの更新
        player.awardStat(Stats.SLEEP_IN_BED);
        CriteriaTriggers.SLEPT_IN_BED.trigger(player);

        // 時間経過を無効にしている場合は寝ても夜が明けない旨のメッセージを送信する
        if (!level.canSleepThroughNights()) {
            player.sendOverlayMessage(Component.translatable("sleep.not_possible"));
        }

        level.updateSleepingPlayerList();*/

        // プレイヤーを羊に乗せる
        player.startRiding(target);
    }
}
