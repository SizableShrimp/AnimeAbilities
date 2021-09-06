package me.sizableshrimp.animeabilities.event;

import com.tm.calemiutils.init.InitItems;
import me.sizableshrimp.animeabilities.AnimeAbilitiesMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AnimeAbilitiesMod.MODID)
public class EventHandler {
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity livingEntity = event.getEntityLiving();
        if (!(livingEntity instanceof MobEntity))
            return;
        ItemStack moneyStack = new ItemStack(InitItems.COIN_DOLLAR.get());
        event.getDrops().add(new ItemEntity(livingEntity.level, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), moneyStack));
    }
}
