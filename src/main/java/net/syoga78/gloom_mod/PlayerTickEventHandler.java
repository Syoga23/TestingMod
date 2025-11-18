package net.syoga78.gloom_mod;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import static net.minecraft.world.level.LightLayer.SKY;

@EventBusSubscriber(modid = Gloom.MOD_ID, value = Dist.CLIENT)
public class PlayerTickEventHandler {

    private static int tick = 0;
    private static boolean soundPlayed = false;

    private static final int warningSoundTime = 10;
    private static final int DAMAGE_DELAY_TICKS = 100;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {

        Player player = event.getEntity();
        Level world = player.level();
        BlockPos pos = player.blockPosition().above();
        int lightLevel = getLightLevel(player, pos);
        boolean isDark = lightLevel <= Gloom.DARKNESS_THRESHOLD;

        if(!player.isAlive()) return;
        if(player.isCreative() || player.isSpectator()) return;
        if(Gloom.SAFE_DIMENSIONS.contains(world.dimension().location().toString()))  return;
        if(player.hasEffect(MobEffects.NIGHT_VISION)) return;

        if (!isDark) {
            tick = 0;
            return;
        }

        //100 - 10 = 90

        if ((tick >= (DAMAGE_DELAY_TICKS - warningSoundTime)) && !soundPlayed) {
            player.playSound(ModSound.ATTACK_WARNING.get(),1.0F, 1.0F);

            soundPlayed = true;
        }

        if (tick >= DAMAGE_DELAY_TICKS) {

            float currentHealth = player.getHealth() + player.getAbsorptionAmount();
            float damage;

            if (currentHealth > 1.0f) {
                damage = Math.round(currentHealth / 2.0f);
            } else {
                damage = Gloom.DAMAGE_AMOUNT;
            }
            DamageSource gloomDamage = world.damageSources().generic();
            int oldInvulnerableTime = player.invulnerableTime;
            player.invulnerableTime = 0;
            player.hurt(gloomDamage, damage);
            player.invulnerableTime = oldInvulnerableTime;

            tick = 0;

            soundPlayed = false;
            return;
        }

        tick++;

    }

    private static int getLightLevel(Player player, BlockPos pos) {
        Level world = player.level();
        int blockLight = world.getChunkSource().getLightEngine().getRawBrightness(pos, 0);
        if (!world.dimensionType().hasSkyLight()) {
            return blockLight;
        }
        int skyDarken = world.getSkyDarken();
        int skyLightRaw = world.getBrightness(SKY, pos) - skyDarken;
        float sunAngle = world.getSunAngle(1.0F);
        if (skyLightRaw > 0) {
            float adjustedAngle = sunAngle < (float) Math.PI ? sunAngle : ((float) Math.PI * 2.0F);
            adjustedAngle += (adjustedAngle < 0.0F ? (float) Math.PI * 2.0F : 0.0F) * 0.2F;
            skyLightRaw = Math.round((float) skyLightRaw * Mth.cos(adjustedAngle));
        }
        int skyLight = Mth.clamp(skyLightRaw, 0, 15);
        if (world.isRainingAt(pos)) {
            skyLight -= world.isThundering() ? 3 : 2;
        }
        return Math.max(blockLight, skyLight);
    }

}
