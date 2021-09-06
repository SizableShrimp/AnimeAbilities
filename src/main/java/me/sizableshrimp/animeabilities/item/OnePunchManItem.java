package me.sizableshrimp.animeabilities.item;

import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;


public class OnePunchManItem extends AbilityItem {
    private static final Method DEATH_SOUND_METHOD = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "func_184615_bR");
    private static final Method SOUND_VOLUME_METHOD = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "func_70599_aP");
    private static final Method VOICE_PITCH_METHOD = ObfuscationReflectionHelper.findMethod(LivingEntity.class, "func_70647_i");

    public OnePunchManItem(Properties properties) {
        super(properties);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingAttackEvent event) {
        if (!(event.getSource().getEntity() instanceof PlayerEntity))
            return;
        LivingEntity target = event.getEntityLiving();
        PlayerEntity attacker = (PlayerEntity) event.getSource().getEntity();

        if (attacker == event.getSource().getEntity() || !hasEmptyHand(attacker) || !hasThisAbility(attacker))
            return;

        EntityDamageSource damageSource = new EntityDamageSource("one_punch", attacker) {
            @Override
            public ITextComponent getLocalizedDeathMessage(LivingEntity killed) {
                String translationKey = "death.attack." + this.msgId;
                return new TranslationTextComponent(translationKey, killed.getDisplayName(), this.entity.getDisplayName());
            }
        };
        target.getCombatTracker().recordDamage(damageSource, target.getHealth(), target.getHealth());
        target.setHealth(0);
        try {
            SoundEvent deathSound = (SoundEvent) DEATH_SOUND_METHOD.invoke(target);
            if (deathSound != null) {
                target.playSound(deathSound, (Float) SOUND_VOLUME_METHOD.invoke(target), (Float) VOICE_PITCH_METHOD.invoke(target));
            }
        } catch (Exception e) {
        }

        target.die(damageSource);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // swing time of -1 means it just started
        PlayerEntity player = event.player;
        if (event.phase == TickEvent.Phase.END || event.side == LogicalSide.CLIENT
                || !hasEmptyHand(player) || player.swingTime != -1 || !hasThisAbility(player))
            return;

        RayTraceResult raytrace = player.pick(200, 1, false);
        if (raytrace.getType() == RayTraceResult.Type.MISS)
            return;

        BlockPos pos = new BlockPos(raytrace.getLocation());
        // Should not be close to the player cuz "mountains"
        if (pos.distManhattan(player.blockPosition()) < 20)
            return;
        Set<BlockPos> sphere = getSpherePositions(pos, 5);
        sphere.forEach(p -> player.level.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState()));
    }

    private Set<BlockPos> getSpherePositions(BlockPos center, int radius) {
        Set<BlockPos> sphere = new HashSet<>();
        BlockPos.Mutable mutPos = new BlockPos.Mutable();
        int rSquared = radius * radius;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x * x + y * y + z * z <= rSquared)
                        sphere.add(mutPos.setWithOffset(center, x, y, z).immutable());
                }
            }
        }
        return sphere;
    }

    public static boolean hasEmptyHand(PlayerEntity player) {
        return player.getItemInHand(Hand.MAIN_HAND).isEmpty();
    }
}
