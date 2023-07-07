package com.feroov.frv.entity.monster.renderer;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.monster.CelestroidShip;
import com.feroov.frv.entity.monster.model.CelestroidShipModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CelestroidShipRenderer extends GeoEntityRenderer<CelestroidShip>
{
    public CelestroidShipRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new CelestroidShipModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull CelestroidShip animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/celestroid.png");
    }

    @Override
    public void render(@NotNull CelestroidShip entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight)
    {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected int getBlockLightLevel(@NotNull CelestroidShip entity, @NotNull BlockPos blockPos) { return 15; }
}
