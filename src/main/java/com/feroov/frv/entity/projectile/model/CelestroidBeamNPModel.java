package com.feroov.frv.entity.projectile.model;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.projectile.CelestroidBeamNP;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CelestroidBeamNPModel extends GeoModel<CelestroidBeamNP>
{
    @Override
    public ResourceLocation getModelResource(CelestroidBeamNP animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "geo/projectile/raygun_beam.json");
    }

    @Override
    public ResourceLocation getTextureResource(CelestroidBeamNP animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/projectile/raygun_beam.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CelestroidBeamNP animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "animations/raygun_beam.json");
    }
}