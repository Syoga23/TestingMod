package net.syoga78.gloom_mod;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.*;

@Mod(Gloom.MOD_ID)
public class Gloom {

    public static final String MOD_ID = "gloom_mod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static List<? extends String> SAFE_DIMENSIONS;
    public static int DARKNESS_THRESHOLD;
    public static int DAMAGE_INTERVAL_TICKS;
    public static float DAMAGE_AMOUNT;

    public Gloom(IEventBus modEvents, ModContainer modContainer) {
        modEvents.addListener(this::commonSetup);

        ModSound.SOUND_EVENTS.register(modEvents);
        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

        SAFE_DIMENSIONS = Config.DIMENSIONS_SAFE_LIST.get();
        DARKNESS_THRESHOLD = Config.DARKNESS_THRESHOLD.getRaw();
        DAMAGE_INTERVAL_TICKS = Config.DAMAGE_INTERVAL_TICKS.getRaw();
        DAMAGE_AMOUNT = Config.LAST_HIT_DAMAGE.getRaw();

        LOGGER.info("Gloom mod have been installed!(Client side)");

    }

    @EventBusSubscriber(modid = Gloom.MOD_ID, value = Dist.CLIENT)
    static class ClientModEvents {
        @SubscribeEvent
        static void onClientSetup(FMLClientSetupEvent event) {
            ModContainer modContainer = ModList.get().getModContainerById(Gloom.MOD_ID).orElseThrow();
            LOGGER.info("InfiniteDurability mod loaded!");
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

        LOGGER.info("Gloom mod have been installed!(Server side)");
    }
}
