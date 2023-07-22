package com.feroov.frv.util;

import com.feroov.frv.STLCON;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class TagsSTLCON
{

    public static class Blocks
    {
        public static final TagKey<Block> NEEDS_ASTRALITE_TOOL = create("needs_astralite_tool");


        private static TagKey<Block> create(String location)
        {
            return BlockTags.create(new ResourceLocation(STLCON.MOD_ID, location));
        }
    }
}