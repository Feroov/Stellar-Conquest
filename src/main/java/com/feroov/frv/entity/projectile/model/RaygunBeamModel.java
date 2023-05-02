package com.feroov.frv.entity.projectile.model;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.projectile.RaygunBeam;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RaygunBeamModel extends GeoModel<RaygunBeam>
{
    @Override
    public ResourceLocation getModelResource(RaygunBeam animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "geo/projectile/raygun_beam.json");
    }

    @Override
    public ResourceLocation getTextureResource(RaygunBeam animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/projectile/raygun_beam.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RaygunBeam animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "animations/raygun_beam.json");
    }
}