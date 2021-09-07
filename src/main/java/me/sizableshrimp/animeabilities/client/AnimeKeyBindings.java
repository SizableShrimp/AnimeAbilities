package me.sizableshrimp.animeabilities.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class AnimeKeyBindings {
    private static final List<KeyBinding> keyBindings = new ArrayList<>();

    public static final KeyBinding KI_CHARGE = registerKeyBinding("key.animeabilities.charge_ki", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_C);
    public static final KeyBinding KI_BLAST = registerKeyBinding("key.animeabilities.ki_blast", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_N);
    public static final KeyBinding SPIRIT_BOMB = registerKeyBinding("key.animeabilities.use_spirit_bomb", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_J);
    public static final KeyBinding BOOST = registerKeyBinding("key.animeabilities.boost", KeyConflictContext.IN_GAME, GLFW.GLFW_KEY_V);

    private static KeyBinding registerKeyBinding(String description, KeyConflictContext conflictContext, int keyCode) {
        KeyBinding key = new KeyBinding(description, conflictContext, InputMappings.Type.KEYSYM.getOrCreate(keyCode), "key.categories.animeabilities");
        keyBindings.add(key);
        return key;
    }

    public static List<KeyBinding> getKeyBindings() {
        return keyBindings;
    }
}
