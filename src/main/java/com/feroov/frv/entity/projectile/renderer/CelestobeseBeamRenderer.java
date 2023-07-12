package com.feroov.frv.entity.projectile.renderer;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.projectile.CelestobeseBeam;
import com.feroov.frv.entity.projectile.model.CelestobeseBeamModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CelestobeseBeamRenderer extends GeoEntityRenderer<CelestobeseBeam>
{
    public CelestobeseBeamRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new CelestobeseBeamModel());
    }

    @Override
    public ResourceLocation getTextureLocation(CelestobeseBeam animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/projectile/blastcaster_beam.png");
    }

    @Override
    public void preRender(PoseStack poseStack, CelestobeseBeam animatable, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                          int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);
        poseStack.scale(0.8F, 0.8F, 0.8F);
    }

    @Override
    protected int getBlockLightLevel(CelestobeseBeam entity, BlockPos blockPos) { return 15; }
}
