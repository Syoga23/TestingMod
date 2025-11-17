package net.syoga78.gloom_mod;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import static net.minecraft.world.level.LightLayer.SKY;

@EventBusSubscriber(modid = Gloom.MOD_ID, value = Dist.CLIENT)
public class PlayerTickEventHandler {

    private static final Map<UUID, Boolean> soundFlags = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

        Player player = event.getEntity();
        Level world = player.level();

        UUID playerId = player.getUUID();

        BlockPos pos = player.blockPosition().above();
        int lightLevel = getLightLevel(event, pos);
        int warnThreshold = 8;

        boolean inDarkness = (lightLevel <= Gloom.DARKNESS_THRESHOLD);
        String dimensionKey = world.dimension().location().toString();
        boolean dimensionNotSafe = !Gloom.SAFE_DIMENSIONS.contains(dimensionKey);

        if (!dimensionNotSafe || player.isCreative()) {
            soundFlags.remove(playerId);
            Gloom.GLOOM_COUNTDOWN.remove(playerId);
            return;
        }

        if (!inDarkness) {
            soundFlags.remove(playerId);
            Gloom.GLOOM_COUNTDOWN.remove(playerId);
            return;
        }



        if (player.hasEffect(MobEffects.NIGHT_VISION)) {
            soundFlags.remove(playerId);
            Gloom.GLOOM_COUNTDOWN.remove(playerId);
            return;
        }

        Integer countdown = Gloom.GLOOM_COUNTDOWN.get(playerId);

        Gloom.LOGGER.info("счетчик:{}", countdown);

        if (countdown == null) {
            countdown = Gloom.DAMAGE_INTERVAL_TICKS;
            Gloom.GLOOM_COUNTDOWN.put(playerId, countdown);
            soundFlags.put(playerId, false);
            Gloom.LOGGER.info("New countdown started for {}",countdown);
        }

        boolean soundPlayed = soundFlags.getOrDefault(playerId, false);

        if (countdown <= warnThreshold && !soundPlayed) {
            player.playNotifySound(ModSound.ATTACK_WARNING.get(), SoundSource.MASTER, 1.0f, 1.0f);
            soundFlags.put(playerId, true);
            Gloom.LOGGER.info("Sound played:{}", countdown);
        }

        if (countdown > warnThreshold) {
            soundFlags.put(playerId, false);
        }

            if (countdown <= 0) {

                float currentHealth = player.getHealth() + player.getAbsorptionAmount();
                float damage;
                if (currentHealth > 1.0f) {
                    damage = Math.round(currentHealth / 2.0f);

                } else if (currentHealth <= 1.0f && player.isAlive()) {
                    damage = Gloom.DAMAGE_AMOUNT;
                } else {
                    return;
                }
                DamageSource gloomDamage = world.damageSources().sonicBoom(player);
                player.hurt(gloomDamage, damage);
                Gloom.LOGGER.info("Shadow hit at:{} with damage:{}", countdown, damage);
                soundFlags.put(playerId, false);
                Gloom.GLOOM_COUNTDOWN.put(playerId, Gloom.DAMAGE_INTERVAL_TICKS);
            } else {
                Gloom.GLOOM_COUNTDOWN.put(playerId, countdown - 1);
            }
        }

        /*
        private static int getInitialCountdown() {
            return Gloom.DAMAGE_INTERVAL_TICKS + Gloom.RANDOM.nextInt(101);
        }

        private static int getNextCountdown() {
            return Gloom.DAMAGE_INTERVAL_TICKS + Gloom.RANDOM.nextInt(121);
        } */

        private static int getLightLevel(PlayerTickEvent.Post event, BlockPos pos){

            Level world = event.getEntity().level();
            int result = 15;

            if (world.dimensionType().hasSkyLight()) {

                int skyLight;
                int blockLight = world.getChunkSource().getLightEngine().getRawBrightness(pos, 15);

                int i = world.getBrightness(SKY, pos) - world.getSkyDarken();
                float f = world.getSunAngle(1.0F);
                if (i > 0) {
                    float f1 = f < (float) Math.PI ? 0.0F : ((float) Math.PI * 2F);
                    f += (f1 - f) * 0.2F;
                    i = Math.round((float) i * Mth.cos(f));
                }
                skyLight = Mth.clamp(i, 0, 15);

                if (world.isRainingAt(pos)) {
                    if (world.isThundering()) {
                        skyLight -= 3;
                    } else {
                        skyLight -= 2;
                    }
                }

                result = Math.max(blockLight, skyLight);
            }
            return result;
        }

    }
