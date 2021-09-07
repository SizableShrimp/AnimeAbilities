package me.sizableshrimp.animeabilities.client.event;

import me.sizableshrimp.animeabilities.AnimeAbilitiesMod;
import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.client.AnimeKeyBindings;
import me.sizableshrimp.animeabilities.client.particle.DragonBallBoltParticle;
import me.sizableshrimp.animeabilities.client.renderer.KiBlastRenderer;
import me.sizableshrimp.animeabilities.client.renderer.OBJRenderer;
import me.sizableshrimp.animeabilities.client.renderer.SpiritBombRenderer;
import me.sizableshrimp.animeabilities.client.renderer.layer.DragonBallLayer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = AnimeAbilitiesMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetupHandler {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(Registration.SPIRIT_BOMB_ENTITY_TYPE.get(), SpiritBombRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registration.KI_BLAST_ENTITY_TYPE.get(), KiBlastRenderer::new);
        event.enqueueWork(() -> Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap().values()
                .forEach(playerRenderer -> playerRenderer.addLayer(new DragonBallLayer(playerRenderer))));
        AnimeKeyBindings.getKeyBindings().forEach(ClientRegistry::registerKeyBinding);
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        ModelLoader.addSpecialModel(OBJRenderer.SPHERE_MODEL);
        ModelLoader.addSpecialModel(OBJRenderer.CYLINDER_MODEL);
    }

    @SubscribeEvent
    public static void onParticleFactoryRegistry(ParticleFactoryRegisterEvent event) {
        Minecraft.getInstance().particleEngine.register(Registration.DRAGONBALL_BOLT.get(), DragonBallBoltParticle.Factory::new);
    }
}
