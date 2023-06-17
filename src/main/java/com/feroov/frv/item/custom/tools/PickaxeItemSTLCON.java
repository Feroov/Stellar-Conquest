package com.feroov.frv.item.custom.tools;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;

public class PickaxeItemSTLCON extends DiggerItem
{
    public PickaxeItemSTLCON(Tier tier, float f1, float f2, Item.Properties properties)
    {
        super((float)f1, f2, tier, BlockTags.MINEABLE_WITH_PICKAXE, properties);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction)
    {
        return net.minecraftforge.common.ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction);
    }
}
