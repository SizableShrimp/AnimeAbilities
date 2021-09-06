package me.sizableshrimp.animeabilities.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

import javax.annotation.Nullable;

public class DragonBallBoltParticle extends SpriteTexturedParticle {
    private final IAnimatedSprite sprites;

    public DragonBallBoltParticle(ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, IAnimatedSprite sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        this.setSize(1.0F, 1.5F);
        this.quadSize *= 5.0F;
        this.lifetime = 1;//Math.max(1, 12 + (this.random.nextInt(6) - 3));
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.xd = xSpeed * 1.5D;
        this.yd = ySpeed * 1.5D;
        this.zd = zSpeed * 1.5D;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
        super.render(buffer, renderInfo, partialTicks);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSprite(sprites.get(this.age % 3 + 1, 3));
    }

    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprites;

        public Factory(IAnimatedSprite pSprites) {
            this.sprites = pSprites;
        }

        @Nullable
        @Override
        public Particle createParticle(BasicParticleType type, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new DragonBallBoltParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}
