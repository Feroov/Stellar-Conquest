package com.feroov.frv.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ConsumablesSTLCON
{

    public static final FoodProperties BLUSHTHORN_NECTAR_BOTTLE = (new FoodProperties.Builder())
            .nutrition(3)
            .saturationMod(0.6F)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 400 ,3), 1.0f)
            .build();
}
