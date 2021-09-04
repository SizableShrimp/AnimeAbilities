package me.sizableshrimp.animeabilities;

import me.sizableshrimp.animeabilities.capability.KiHolderCapability;
import me.sizableshrimp.animeabilities.network.NetworkHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AnimeAbilitiesMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AnimeAbilitiesMod {
    public static final String MODID = "animeabilities";
    public static final Logger LOGGER = LogManager.getLogger();

    public AnimeAbilitiesMod() {
        Registration.register();
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        KiHolderCapability.register();
        NetworkHandler.register();
    }
}
