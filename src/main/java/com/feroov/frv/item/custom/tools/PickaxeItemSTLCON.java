package com.feroov.frv.item.custom.tools;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;

public class PickaxeItemSTLCON extends DiggerItem
{
    public PickaxeItemSTLCON(Tier p_42961_, float p_42962_, float p_42963_, Item.Properties p_42964_) {
        super((float)p_42962_, p_42963_, p_42961_, BlockTags.MINEABLE_WITH_PICKAXE, p_42964_);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, net.minecraftforge.common.ToolAction toolAction) {
        return net.minecraftforge.common.ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction);
    }
}
