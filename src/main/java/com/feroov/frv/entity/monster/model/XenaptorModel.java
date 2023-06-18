package com.feroov.frv.entity.monster.model;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.monster.Xenaptor;
import com.feroov.frv.entity.neutral.XeronGuard;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class XenaptorModel extends GeoModel<Xenaptor>
{
    @Override
    public ResourceLocation getModelResource(Xenaptor animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "geo/xenaptor.json");
    }

    @Override
    public ResourceLocation getTextureResource(Xenaptor animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/xenaptor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Xenaptor animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "animations/xenaptor.json");
    }

    @Override
    public void setCustomAnimations(Xenaptor animatable, long instanceId, AnimationState<Xenaptor> animationState)
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
