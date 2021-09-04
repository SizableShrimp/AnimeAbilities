package me.sizableshrimp.animeabilities.client.event;

import me.sizableshrimp.animeabilities.AnimeAbilitiesMod;
import me.sizableshrimp.animeabilities.client.renderlayer.DragonBallLayer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AnimeAbilitiesMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetupHandler {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap().values().forEach(playerRenderer -> playerRenderer.addLayer(new DragonBallLayer(playerRenderer)));
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        ModelLoader.addSpecialModel(DragonBallRenderer.SPHERE_MODEL);
    }
}
