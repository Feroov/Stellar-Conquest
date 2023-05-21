package com.feroov.frv.entity.monster.renderer;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.monster.Xeron;
import com.feroov.frv.entity.monster.model.XeronModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class XeronRenderer extends GeoEntityRenderer<Xeron>
{
    public XeronRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new XeronModel());
        this.shadowRadius = 0.28F;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Xeron animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/xeron.png");
    }

    @Override
    public void render(@NotNull Xeron entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight)
    {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void preRender(PoseStack poseStack, Xeron animatable, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                          int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);
            poseStack.scale(0.75F, 0.8F, 0.75F);
    }

    @Override
    protected int getBlockLightLevel(@NotNull Xeron entity, @NotNull BlockPos blockPos) { return 15; }
}
