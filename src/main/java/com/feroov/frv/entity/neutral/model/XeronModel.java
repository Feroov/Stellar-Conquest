package com.feroov.frv.entity.neutral.model;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.neutral.Xeron;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class XeronModel extends GeoModel<Xeron>
{
    @Override
    public ResourceLocation getModelResource(Xeron animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "geo/xeron.json");
    }

    @Override
    public ResourceLocation getTextureResource(Xeron animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/xeron.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Xeron animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "animations/xeron.json");
    }

    @Override
    public void setCustomAnimations(Xeron animatable, long instanceId, AnimationState<Xeron> animationState)
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
