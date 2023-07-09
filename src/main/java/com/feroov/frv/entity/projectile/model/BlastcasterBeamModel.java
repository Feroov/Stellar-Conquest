package com.feroov.frv.entity.projectile.model;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.projectile.BlastcasterBeam;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BlastcasterBeamModel extends GeoModel<BlastcasterBeam>
{
    @Override
    public ResourceLocation getModelResource(BlastcasterBeam animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "geo/projectile/raygun_beam.json");
    }

    @Override
    public ResourceLocation getTextureResource(BlastcasterBeam animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/projectile/blastcaster_beam.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BlastcasterBeam animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "animations/raygun_beam.json");
    }
}