package com.feroov.frv.entity.monster.model;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.monster.Mekkron;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MekkronModel extends GeoModel<Mekkron>
{
    @Override
    public ResourceLocation getModelResource(Mekkron animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "geo/mekkron.json");
    }

    @Override
    public ResourceLocation getTextureResource(Mekkron animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/mekkron.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Mekkron animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "animations/mekkron.json");
    }

    @Override
    public void setCustomAnimations(Mekkron animatable, long instanceId, AnimationState<Mekkron> animationState)
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
