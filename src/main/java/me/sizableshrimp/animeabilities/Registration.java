package me.sizableshrimp.animeabilities;

import me.sizableshrimp.animeabilities.item.DragonBallItem;
import me.sizableshrimp.animeabilities.item.OnePunchManItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Registration {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AnimeAbilitiesMod.MODID);

    // public static final RegistryObject<Item> ATTACK_ON_TITAN = ITEMS.register("attack_on_titan", () -> new Item(new Item.Properties()));
    public static final RegistryObject<OnePunchManItem> ONE_PUNCH_MAN = ITEMS.register("one_punch_man", () -> new OnePunchManItem(new Item.Properties()));
    public static final RegistryObject<DragonBallItem> DRAGON_BALL = ITEMS.register("dragon_ball", () -> new DragonBallItem(new Item.Properties()));
    public static final RegistryObject<Item> KAIKOEN_UPGRADE = ITEMS.register("kaikoen_upgrade",  () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SUPER_SAIYEN_UPGRADE = ITEMS.register("super_saiyen_upgrade",  () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ONE_THIRD_UPGRADE = ITEMS.register("one_third_upgrade",  () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TWO_THIRD_UPGRADE = ITEMS.register("two_third_upgrade",  () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NINETY_NINE_UPGRADE = ITEMS.register("ninety_nine_upgrade",  () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> QUESTION_MARKS_UPGRADE = ITEMS.register("question_marks_upgrade",  () -> new Item(new Item.Properties()));

    public static void register() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(modBus);
    }
}
