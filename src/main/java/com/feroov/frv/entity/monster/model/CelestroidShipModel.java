package com.feroov.frv.entity.monster.model;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.monster.CelestroidShip;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CelestroidShipModel extends GeoModel<CelestroidShip>
{
    @Override
    public ResourceLocation getModelResource(CelestroidShip animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "geo/celestroid_ship.json");
    }

    @Override
    public ResourceLocation getTextureResource(CelestroidShip animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/celestroid.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CelestroidShip animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "animations/celestroid.json");
    }

    @Override
    public void setCustomAnimations(CelestroidShip animatable, long instanceId, AnimationState<CelestroidShip> animationState)
    {
        CoreGeoBone head = getAnimationProcessor().getBone("celestroid");

        if (head != null)
        {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
