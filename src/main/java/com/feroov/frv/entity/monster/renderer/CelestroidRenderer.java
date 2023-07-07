package com.feroov.frv.entity.monster.renderer;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.monster.Celestroid;
import com.feroov.frv.entity.monster.model.CelestroidModel;
import com.feroov.frv.entity.neutral.XeronGuard;
import com.feroov.frv.item.ItemsSTLCON;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import javax.annotation.Nullable;

public class CelestroidRenderer extends GeoEntityRenderer<Celestroid>
{
    public CelestroidRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new CelestroidModel());
        this.shadowRadius = 0.28F;
        addRenderLayer(new BlockAndItemGeoLayer<>(this)
        {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, Celestroid animatable)
            {
                return switch (bone.getName()) { case "gun" -> new ItemStack(ItemsSTLCON.COSMIC_RAY_GUN.get()); default -> null; };
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, Celestroid animatable)
            {
                return switch (bone.getName()) { default -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND; };
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, Celestroid animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay)
            {
                poseStack.mulPose(Axis.XP.rotationDegrees(-95));
                poseStack.mulPose(Axis.YP.rotationDegrees(0));
                poseStack.mulPose(Axis.ZP.rotationDegrees(0));
                poseStack.translate(0.28D, -0.05D, 1.5D);
                poseStack.scale(0.8F, 0.9F, 1.0F);
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Celestroid animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/celestroid.png");
    }

    @Override
    public void preRender(PoseStack poseStack, Celestroid animatable, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                          int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);
        poseStack.scale(0.75F, 0.8F, 0.75F);
    }


    @Override
    protected int getBlockLightLevel(@NotNull Celestroid entity, @NotNull BlockPos blockPos) { return 15; }
}
