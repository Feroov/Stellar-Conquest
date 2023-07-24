package com.feroov.frv.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BannedParticles extends TextureSheetParticle
{
    private final SpriteSet sprites;

    protected BannedParticles(ClientLevel clientLevel, double d0, double d1, double d2, double d3, double d4, double d5, SpriteSet spriteSet)
    {
        super(clientLevel, d0, d1, d2);
        this.gravity = -0.1F;
        this.friction = 0.9F;
        this.sprites = spriteSet;
        this.xd = d3 + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
        this.yd = d4 * 0.15 + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
        this.zd = d5 + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
        float f = this.random.nextFloat() * 0.3F + 0.7F;
        this.rCol = f;
        this.gCol = f;
        this.bCol = f;
        this.quadSize = 0.1F * (this.random.nextFloat() * this.random.nextFloat() * 6.0F + 1.0F);
        this.lifetime = 60;
        this.setSpriteFromAge(spriteSet);
    }

    public ParticleRenderType getRenderType() { return ParticleRenderType.PARTICLE_SHEET_OPAQUE; }

    public void tick()
    {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet sprites;

        public Provider(SpriteSet p_106588_) { this.sprites = p_106588_; }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel,
                                       double d0, double d1, double d2, double d3, double d4, double bannedParticles)
        {
            return new BannedParticles(clientLevel, d0, d1, d2, d3, d4, bannedParticles, this.sprites);
        }
    }
}