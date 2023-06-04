package com.feroov.frv.entity.passive.renderer;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.passive.Wispxen;
import com.feroov.frv.entity.passive.model.WispxenModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WispxenRenderer extends GeoEntityRenderer<Wispxen>
{
    public WispxenRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new WispxenModel());
        this.shadowRadius = 0.11F;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Wispxen animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/wispxen.png");
    }

    @Override
    public void render(@NotNull Wispxen entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight)
    {
        if(entity.isBaby()) {
            poseStack.scale(0.6f, 0.6f, 0.6f);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected int getBlockLightLevel(@NotNull Wispxen entity, @NotNull BlockPos blockPos) { return 15; }
}
