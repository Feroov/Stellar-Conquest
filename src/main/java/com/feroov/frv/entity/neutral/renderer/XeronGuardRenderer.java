package com.feroov.frv.entity.neutral.renderer;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.neutral.XeronGuard;
import com.feroov.frv.entity.neutral.model.XeronGuardModel;
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
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import javax.annotation.Nullable;

public class XeronGuardRenderer extends GeoEntityRenderer<XeronGuard>
{
    public XeronGuardRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new XeronGuardModel());
        this.shadowRadius = 0.28F;
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, XeronGuard animatable) {
                return switch (bone.getName()) {
                    case "sword" -> new ItemStack(ItemsSTLCON.XENOSTONE_SWORD.get());
                    default -> null;
                };
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, XeronGuard animatable) {
                return switch (bone.getName()) {
                    default -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                };
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, XeronGuard animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-80));
                poseStack.mulPose(Axis.YP.rotationDegrees(0));
                poseStack.mulPose(Axis.ZP.rotationDegrees(0));
                poseStack.translate(0.0D, 0.1D, -0.1D);
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });

        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Nullable
            @Override
            protected ItemStack getStackForBone(GeoBone bone, XeronGuard animatable) {
                return switch (bone.getName()) {
                    case "shield" -> new ItemStack(Items.SHIELD);
                    default -> null;
                };
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, XeronGuard animatable) {
                return switch (bone.getName()) {
                    default -> ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
                };
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, XeronGuard animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.mulPose(Axis.ZP.rotationDegrees(0));
                poseStack.translate(0.0D, 0.120D, -1.2D);
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull XeronGuard animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/xeron.png");
    }

    @Override
    public void render(@NotNull XeronGuard entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight)
    {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void preRender(PoseStack poseStack, XeronGuard animatable, BakedGeoModel model,
                          MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
                          int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);
            poseStack.scale(0.75F, 0.8F, 0.75F);
    }

    @Override
    protected int getBlockLightLevel(@NotNull XeronGuard entity, @NotNull BlockPos blockPos) { return 15; }
}
