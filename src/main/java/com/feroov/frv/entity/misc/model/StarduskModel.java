package com.feroov.frv.entity.misc.model;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.misc.Stardusk;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StarduskModel extends GeoModel<Stardusk>
{
    @Override
    public ResourceLocation getModelResource(Stardusk animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "geo/stardusk.json");
    }

    @Override
    public ResourceLocation getTextureResource(Stardusk animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/stardusk.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Stardusk animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "animations/stardusk.json");
    }
}
