package com.feroov.frv.entity.passive.model;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.passive.Zephxen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ZephxenModel extends GeoModel<Zephxen>
{
    @Override
    public ResourceLocation getModelResource(Zephxen animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "geo/zephxen.json");
    }

    @Override
    public ResourceLocation getTextureResource(Zephxen animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/zephxen.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Zephxen animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "animations/zephxen.json");
    }

    @Override
    public void setCustomAnimations(Zephxen animatable, long instanceId, AnimationState<Zephxen> animationState)
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
