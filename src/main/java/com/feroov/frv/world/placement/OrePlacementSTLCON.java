package com.feroov.frv.world.placement;

import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class OrePlacementSTLCON
{
    public static List<PlacementModifier> orePlacement(PlacementModifier placementModifier, PlacementModifier placementModifier1)
    {
        return List.of(placementModifier, InSquarePlacement.spread(), placementModifier1, BiomeFilter.biome());
    }

    public static List<PlacementModifier> commonOrePlacement(int p_195344_, PlacementModifier p_195345_)
    {
        return orePlacement(CountPlacement.of(p_195344_), p_195345_);
    }

    public static List<PlacementModifier> rareOrePlacement(int p_195350_, PlacementModifier p_195351_)
    {
        return orePlacement(RarityFilter.onAverageOnceEvery(p_195350_), p_195351_);
    }
}
