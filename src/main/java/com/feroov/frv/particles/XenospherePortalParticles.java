package com.feroov.frv.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class XenospherePortalParticles extends TextureSheetParticle
{
    private final double xStart;
    private final double yStart;
    private final double zStart;

    protected XenospherePortalParticles(ClientLevel clientLevel, double d0, double d1, double d2, double d3, double d4, double d5)
    {
        super(clientLevel, d0, d1, d2);
        this.xd = d3;
        this.yd = d4;
        this.zd = d5;
        this.x = d0;
        this.y = d1;
        this.z = d2;
        this.xStart = this.x;
        this.yStart = this.y;
        this.zStart = this.z;
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.2F + 0.5F);
        float f = this.random.nextFloat() * 0.6F + 0.4F;
        this.lifetime = (int)(Math.random() * 10.0D) + 40;
    }

    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void move(double p_107560_, double p_107561_, double p_107562_)
    {
        this.setBoundingBox(this.getBoundingBox().move(p_107560_, p_107561_, p_107562_));
        this.setLocationFromBoundingbox();
    }

    public float getQuadSize(float p_107567_)
    {
        float f = ((float)this.age + p_107567_) / (float)this.lifetime;
        f = 1.0F - f;
        f *= f;
        f = 1.0F - f;
        return this.quadSize * f;
    }

    public int getLightColor(float p_107564_)
    {
        int i = super.getLightColor(p_107564_);
        float f = (float)this.age / (float)this.lifetime;
        f *= f;
        f *= f;
        int j = i & 255;
        int k = i >> 16 & 255;
        k += (int)(f * 15.0F * 16.0F);

        if (k > 240) { k = 240; }
        return j | k << 16;
    }

    public void tick()
    {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) { this.remove(); }
        else
        {
            float f = (float)this.age / (float)this.lifetime;
            float f1 = -f + f * f * 2.0F;
            float f2 = 1.0F - f1;
            this.x = this.xStart + this.xd * (double)f2;
            this.y = this.yStart + this.yd * (double)f2 + (double)(1.0F - f);
            this.z = this.zStart + this.zd * (double)f2;
            this.setPos(this.x, this.y, this.z);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet sprite;
        public Provider(SpriteSet p_107570_) { this.sprite = p_107570_; }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d0, double d1, double d2, double d3, double d4, double d5)
        {
            XenospherePortalParticles portalparticle = new XenospherePortalParticles(clientLevel, d0, d1, d2, d3, d4, d5);
            portalparticle.pickSprite(this.sprite);
            return portalparticle;
        }
    }
}
