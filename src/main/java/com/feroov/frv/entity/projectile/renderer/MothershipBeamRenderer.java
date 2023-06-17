package com.feroov.frv.entity.projectile.renderer;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.projectile.MothershipBeam;
import com.feroov.frv.entity.projectile.model.MothershipBeamModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MothershipBeamRenderer extends GeoEntityRenderer<MothershipBeam>
{
    public MothershipBeamRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new MothershipBeamModel());
    }

    @Override
    public ResourceLocation getTextureLocation(MothershipBeam animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/projectile/raygun_beam.png");
    }

    @Override
    public void preRender(PoseStack poseStack, MothershipBeam animatable, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                          int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);
        poseStack.scale(2.5F, 2.5F, 2.5F);
    }

    @Override
    protected int getBlockLightLevel(MothershipBeam entity, BlockPos blockPos) { return 15; }
}
