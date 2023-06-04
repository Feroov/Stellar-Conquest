package com.feroov.frv.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class XenospherePortalParticles extends SimpleAnimatedParticle
{
    private final double xStart;
    private final double yStart;
    private final double zStart;

    XenospherePortalParticles(ClientLevel clientLevel, double d0, double d1, double d2, double d3, double d4, double d5, SpriteSet spriteSet) {
        super(clientLevel, d0, d1, d2, spriteSet, 0.0125F);
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

        public Particle createParticle(SimpleParticleType p_106566_, ClientLevel p_106567_, double p_106568_, double p_106569_, double p_106570_, double p_106571_, double p_106572_, double p_106573_) {
            XenospherePortalParticles portalparticle =
                    new XenospherePortalParticles(p_106567_, p_106568_, p_106569_, p_106570_, p_106571_, p_106572_, p_106573_, this.sprite);
            portalparticle.pickSprite(this.sprite);
            return portalparticle;
        }
    }
}
