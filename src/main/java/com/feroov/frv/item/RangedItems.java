package com.feroov.frv.item;



import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;


public class RangedItems
{

    public static void addRanged()  { makeRanged(ItemsSTLCON.COSMIC_RAY_GUN.get()); }

    private static void makeRanged(Item item)
    {
        ItemProperties.register(item, new ResourceLocation("pull"), (itemStack, clientLevel, livingEntity, i) ->
        {
            if (livingEntity == null) { return 0.0F; }
            else
            {
                return livingEntity.getUseItem() != itemStack ? 0.0F : (float)(itemStack.getUseDuration() - livingEntity.getUseItemRemainingTicks()) / 20.0F;
            }
        });

        ItemProperties.register(item, new ResourceLocation("pulling"), (itemStack, clientLevel, livingEntity, i) ->
        {
            return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1.0F : 0.0F;
        });
    }
}
