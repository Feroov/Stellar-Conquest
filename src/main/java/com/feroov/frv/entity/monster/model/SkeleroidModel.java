package com.feroov.frv.entity.monster.model;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.monster.Skeleroid;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SkeleroidModel extends GeoModel<Skeleroid>
{
    @Override
    public ResourceLocation getModelResource(Skeleroid animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "geo/skeleroid.json");
    }

    @Override
    public ResourceLocation getTextureResource(Skeleroid animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/skeleroid.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Skeleroid animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "animations/celestroid.json");
    }

    @Override
    public void setCustomAnimations(Skeleroid animatable, long instanceId, AnimationState<Skeleroid> animationState)
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
