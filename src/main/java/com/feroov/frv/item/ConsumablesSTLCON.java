package com.feroov.frv.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ConsumablesSTLCON
{

    public static final FoodProperties BLUSHTHORN_NECTAR_BOTTLE = (new FoodProperties.Builder())
            .nutrition(3)
            .saturationMod(0.6F)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 400 ,1), 1.0f)
            .build();

    public static final FoodProperties TOXIC_RESILIENCE = (new FoodProperties.Builder())
            .nutrition(1)
            .saturationMod(1.5F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 200 ,2), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 300 ,2), 1.0f)
            .build();

    public static final FoodProperties LUMIBLOOM = (new FoodProperties.Builder())
            .nutrition(4)
            .saturationMod(0.8F)
            .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 400), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.DIG_SPEED, 400), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 1), 1.0f)
            .build();

    public static final FoodProperties ZEPHXEN_MEAT = (new FoodProperties.Builder()).nutrition(8).saturationMod(0.7F).meat().build();
    public static final FoodProperties COOKED_ZEPHXEN_MEAT = (new FoodProperties.Builder()).nutrition(13).saturationMod(1.4F).meat().build();
}
