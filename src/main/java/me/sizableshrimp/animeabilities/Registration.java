package me.sizableshrimp.animeabilities;

import me.sizableshrimp.animeabilities.entity.SpiritBombEntity;
import me.sizableshrimp.animeabilities.item.DragonBallItem;
import me.sizableshrimp.animeabilities.item.OnePunchManItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class Registration {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AnimeAbilitiesMod.MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, AnimeAbilitiesMod.MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, AnimeAbilitiesMod.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, AnimeAbilitiesMod.MODID);

    // public static final RegistryObject<Item> ATTACK_ON_TITAN = ITEMS.register("attack_on_titan", () -> new Item(new Item.Properties()));
    public static final RegistryObject<OnePunchManItem> ONE_PUNCH_MAN = ITEMS.register("one_punch_man", () -> new OnePunchManItem(new Item.Properties()));
    public static final RegistryObject<DragonBallItem> DRAGON_BALL = ITEMS.register("dragon_ball", () -> new DragonBallItem(new Item.Properties()));
    public static final RegistryObject<Item> KAIKOEN_UPGRADE = ITEMS.register("kaikoen_upgrade",  () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SUPER_SAIYEN_UPGRADE = ITEMS.register("super_saiyen_upgrade",  () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ONE_THIRD_UPGRADE = ITEMS.register("one_third_upgrade",  () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TWO_THIRD_UPGRADE = ITEMS.register("two_third_upgrade",  () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NINETY_NINE_UPGRADE = ITEMS.register("ninety_nine_upgrade",  () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> QUESTION_MARKS_UPGRADE = ITEMS.register("question_marks_upgrade",  () -> new Item(new Item.Properties()));

    public static final RegistryObject<BasicParticleType> DRAGONBALL_BOLT = PARTICLE_TYPES.register("dragonball_bolt", () -> new BasicParticleType(true));

    public static final RegistryObject<SoundEvent> SPIRIT_BOMB_CHARGE_SOUND = registerSound("spirit_bomb_charge");

    public static final RegistryObject<EntityType<SpiritBombEntity>> SPIRIT_BOMB_ENTITY_TYPE = registerEntity("spirit_bomb",
            () -> EntityType.Builder.<SpiritBombEntity>of(SpiritBombEntity::new, EntityClassification.MISC).sized(1F, 1F));

    private static RegistryObject<SoundEvent> registerSound(String name) {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(AnimeAbilitiesMod.MODID, name)));
    }

    public static void register() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(modBus);
        PARTICLE_TYPES.register(modBus);
        SOUND_EVENTS.register(modBus);
        ENTITY_TYPES.register(modBus);
    }

    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name, Supplier<EntityType.Builder<T>> supplier) {
        return ENTITY_TYPES.register(name, () -> supplier.get().build(name));
    }
}
