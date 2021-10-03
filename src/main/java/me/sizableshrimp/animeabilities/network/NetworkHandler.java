package me.sizableshrimp.animeabilities.network;

import com.google.common.collect.ImmutableList;
import me.sizableshrimp.animeabilities.AnimeAbilitiesMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.List;
import java.util.function.BiConsumer;

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
        List<BiConsumer<SimpleChannel, Integer>> packets = ImmutableList.<BiConsumer<SimpleChannel, Integer>>builder()
                .add(KiStatusPacket::register)
                .add(KiChargePacket::register)
                .add(SpiritBombStatusPacket::register)
                .add(UseSpiritBombPacket::register)
                .add(BoostFlyPacket::register)
                .add(UseKiBlastPacket::register)
                .add(SwitchTitanPacket::register)
                .add(TitanStatusPacket::register)
                .add(MindMovePacket::register)
                .add(UseKamehamehaPacket::register)
                .add(SpiritBombExplodedPacket::register)
                .add(AbilityStatusPacket::register)
                .add(OpenAbilitiesPacket::register)
                .build();

        packets.forEach(consumer -> consumer.accept(INSTANCE, getNextId()));
    }

    private static int getNextId() {
        return nextId++;
    }
}
