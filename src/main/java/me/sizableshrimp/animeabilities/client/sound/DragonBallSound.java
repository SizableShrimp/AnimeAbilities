package me.sizableshrimp.animeabilities.client.sound;

import me.sizableshrimp.animeabilities.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.TickableSound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

import java.util.function.Predicate;

public class DragonBallSound extends TickableSound {
    private final Minecraft mc = Minecraft.getInstance();
    private final Predicate<PlayerEntity> continuePred;

    public DragonBallSound(SoundEvent soundEvent, Predicate<PlayerEntity> continuePred, float volume) {
        super(soundEvent, SoundCategory.PLAYERS);
        this.delay = 0;
        this.looping = true;
        this.volume = volume;
        this.continuePred = continuePred;
        setPos(mc.player);
    }

    @Override
    public void tick() {
        if (mc.player != null && mc.player.isAlive() && Registration.DRAGON_BALL.get().hasThisAbility(mc.player) && continuePred.test(mc.player)) {
            setPos(mc.player);
        } else {
            this.stop();
        }
    }

    private void setPos(Entity entity) {
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }
}
