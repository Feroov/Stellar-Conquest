package com.feroov.frv.entity.projectile.model;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.projectile.StarduskBeam;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StarduskBeamModel extends GeoModel<StarduskBeam>
{
    @Override
    public ResourceLocation getModelResource(StarduskBeam animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "geo/projectile/raygun_beam.json");
    }

    @Override
    public ResourceLocation getTextureResource(StarduskBeam animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/projectile/raygun_beam.png");
    }

    @Override
    public ResourceLocation getAnimationResource(StarduskBeam animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "animations/raygun_beam.json");
    }
}