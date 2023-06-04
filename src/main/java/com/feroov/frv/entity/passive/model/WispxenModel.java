package com.feroov.frv.entity.passive.model;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.passive.Wispxen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class WispxenModel extends GeoModel<Wispxen>
{
    @Override
    public ResourceLocation getModelResource(Wispxen animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "geo/wispxen.json");
    }

    @Override
    public ResourceLocation getTextureResource(Wispxen animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/wispxen.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Wispxen animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "animations/wispxen.json");
    }

    @Override
    public void setCustomAnimations(Wispxen animatable, long instanceId, AnimationState<Wispxen> animationState)
    {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null)
        {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
