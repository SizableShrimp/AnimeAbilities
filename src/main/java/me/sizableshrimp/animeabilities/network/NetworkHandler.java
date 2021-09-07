package me.sizableshrimp.animeabilities.network;

import me.sizableshrimp.animeabilities.AnimeAbilitiesMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AnimeAbilitiesMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int nextId = 0;

    public static void register() {
        KiStatusPacket.register(INSTANCE, getNextId());
        KiChargePacket.register(INSTANCE, getNextId());
        SpiritBombStatusPacket.register(INSTANCE, getNextId());
        UseSpiritBombPacket.register(INSTANCE, getNextId());
        BoostFlyPacket.register(INSTANCE, getNextId());
        UseKiBlastPacket.register(INSTANCE, getNextId());
    }

    private static int getNextId() {
        return nextId++;
    }
}
