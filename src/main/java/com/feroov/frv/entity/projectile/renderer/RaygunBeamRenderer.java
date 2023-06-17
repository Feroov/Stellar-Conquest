package com.feroov.frv.entity.projectile.renderer;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.projectile.RaygunBeam;
import com.feroov.frv.entity.projectile.model.RaygunBeamModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RaygunBeamRenderer extends GeoEntityRenderer<RaygunBeam>
{
    public RaygunBeamRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new RaygunBeamModel());
    }

    @Override
    public ResourceLocation getTextureLocation(RaygunBeam animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/projectile/raygun_beam.png");
    }

    @Override
    public void preRender(PoseStack poseStack, RaygunBeam animatable, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                          int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);
        poseStack.scale(0.8F, 0.8F, 0.8F);
    }

    @Override
    protected int getBlockLightLevel(RaygunBeam entity, BlockPos blockPos) { return 15; }
}
