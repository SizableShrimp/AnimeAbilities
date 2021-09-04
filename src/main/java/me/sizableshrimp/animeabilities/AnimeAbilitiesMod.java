package me.sizableshrimp.animeabilities;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AnimeAbilitiesMod.MODID)
@Mod.EventBusSubscriber
public class AnimeAbilitiesMod {
    public static final String MODID = "animeabilities";
    public static final Logger LOGGER = LogManager.getLogger();

    public AnimeAbilitiesMod() {}

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
