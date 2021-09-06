package me.sizableshrimp.animeabilities.client.event;

import me.sizableshrimp.animeabilities.AnimeAbilitiesMod;
import me.sizableshrimp.animeabilities.Registration;
import me.sizableshrimp.animeabilities.client.particle.DragonBallBoltParticle;
import me.sizableshrimp.animeabilities.client.renderer.OBJRenderer;
import me.sizableshrimp.animeabilities.client.renderer.SpiritBombRenderer;
import me.sizableshrimp.animeabilities.client.renderer.layer.DragonBallLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = AnimeAbilitiesMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetupHandler {
    private static final List<KeyBinding> keyBindings = new ArrayList<>();
    public static final KeyBinding KI_CHARGE_KEY = registerKeyBinding("key.animeabilities.charge_ki", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_C);
    public static final KeyBinding SPIRIT_BOMB_KEY = registerKeyBinding("key.animeabilities.use_spirit_bomb", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_J);

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(Registration.SPIRIT_BOMB_ENTITY_TYPE.get(), SpiritBombRenderer::new);
        event.enqueueWork(() -> Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap().values()
                .forEach(playerRenderer -> playerRenderer.addLayer(new DragonBallLayer(playerRenderer))));
        keyBindings.forEach(ClientRegistry::registerKeyBinding);
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

    private static KeyBinding registerKeyBinding(String description, KeyConflictContext conflictContext, int keyCode) {
        KeyBinding key = new KeyBinding(description, conflictContext, InputMappings.Type.KEYSYM.getOrCreate(keyCode), "key.categories.animeabilities");
        keyBindings.add(key);
        return key;
    }
}
