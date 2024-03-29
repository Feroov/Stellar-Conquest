package com.feroov.frv.item.tiers;

import com.feroov.frv.block.BlocksSTLCON;
import com.feroov.frv.item.ItemsSTLCON;
import com.feroov.frv.util.TagsSTLCON;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

public class TiersSTLCON
{
    public static final ForgeTier ADMIN = new ForgeTier(999,999999,999f,9999f,10, Tags.Blocks.NEEDS_GOLD_TOOL, () -> Ingredient.of(Blocks.BEDROCK));

    public static final ForgeTier XENOSTONE = new ForgeTier(1,181,4.4f,1.5f,5, Tags.Blocks.NEEDS_GOLD_TOOL, () -> Ingredient.of(BlocksSTLCON.XENOCOBBLESTONE.get()));
    public static final ForgeTier XENITE = new ForgeTier(2,443,7.0f,2.0f,5, Tags.Blocks.NEEDS_GOLD_TOOL, () -> Ingredient.of(ItemsSTLCON.XENITE_INGOT.get()));
    public static final Tier ASTRALITE = TierSortingRegistry.registerTier(new ForgeTier(3,1820,8.5f,3.0f,5, TagsSTLCON.Blocks.NEEDS_ASTRALITE_TOOL, () -> Ingredient.of(ItemsSTLCON.ASTRALITE_INGOT.get())), new ResourceLocation("frv:needs_astralite_tool"), List.of(Tiers.DIAMOND), List.of());

    public static final ForgeTier ASTRALITE_HAMMER = new ForgeTier(4,443,1.5f,2.0f,5, Tags.Blocks.NEEDS_GOLD_TOOL, () -> Ingredient.of(ItemsSTLCON.ASTRALITE_INGOT.get()));


    // Vanilla tiers
    /**
     *    WOOD(0, 59, 2.0F, 0.0F, 15, () -> {
     *       return Ingredient.of(ItemTags.PLANKS);
     *    }),
     *    STONE(1, 131, 4.0F, 1.0F, 5, () -> {
     *       return Ingredient.of(ItemTags.STONE_TOOL_MATERIALS);
     *    }),
     *    IRON(2, 250, 6.0F, 2.0F, 14, () -> {
     *       return Ingredient.of(Items.IRON_INGOT);
     *    }),
     *    DIAMOND(3, 1561, 8.0F, 3.0F, 10, () -> {
     *       return Ingredient.of(Items.DIAMOND);
     *    }),
     *    GOLD(0, 32, 12.0F, 0.0F, 22, () -> {
     *       return Ingredient.of(Items.GOLD_INGOT);
     *    }),
     *    NETHERITE(4, 2031, 9.0F, 4.0F, 15, () -> {
     *       return Ingredient.of(Items.NETHERITE_INGOT);
     *    });
     */
}
