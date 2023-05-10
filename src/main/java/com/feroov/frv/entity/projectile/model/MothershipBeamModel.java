package com.feroov.frv.entity.projectile.model;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.projectile.MothershipBeam;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MothershipBeamModel extends GeoModel<MothershipBeam>
{
    @Override
    public ResourceLocation getModelResource(MothershipBeam animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "geo/projectile/raygun_beam.json");
    }

    @Override
    public ResourceLocation getTextureResource(MothershipBeam animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/projectile/raygun_beam.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MothershipBeam animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "animations/raygun_beam.json");
    }
}