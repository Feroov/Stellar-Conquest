package com.feroov.frv.entity.monster.model;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.monster.Mothership;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MothershipModel extends GeoModel<Mothership>
{
    @Override
    public ResourceLocation getModelResource(Mothership animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "geo/mothership.json");
    }

    @Override
    public ResourceLocation getTextureResource(Mothership animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/mothership.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Mothership animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "animations/mothership.json");
    }

    @Override
    public void setCustomAnimations(Mothership animatable, long instanceId, AnimationState<Mothership> animationState)
    {
        CoreGeoBone head = getAnimationProcessor().getBone("celestroid");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
