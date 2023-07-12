package com.feroov.frv.entity.monster.model;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.monster.Celestobese;
import com.feroov.frv.entity.monster.Celestroid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CelestobeseModel extends GeoModel<Celestobese>
{
    @Override
    public ResourceLocation getModelResource(Celestobese animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "geo/celestobese.json");
    }

    @Override
    public ResourceLocation getTextureResource(Celestobese animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/celestroid.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Celestobese animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "animations/celestroid.json");
    }

    @Override
    public void setCustomAnimations(Celestobese animatable, long instanceId, AnimationState<Celestobese> animationState)
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
