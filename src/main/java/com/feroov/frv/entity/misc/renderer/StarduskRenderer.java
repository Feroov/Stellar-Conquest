package com.feroov.frv.entity.misc.renderer;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.misc.Stardusk;
import com.feroov.frv.entity.misc.model.StarduskModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StarduskRenderer extends GeoEntityRenderer<Stardusk>
{
    public StarduskRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new StarduskModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Stardusk animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/stardusk.png");
    }

    @Override
    public void render(@NotNull Stardusk entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight)
    {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected int getBlockLightLevel(@NotNull Stardusk entity, @NotNull BlockPos blockPos) { return 15; }

    @Override
    protected float getDeathMaxRotation(Stardusk entityLivingBaseIn)  { return 0; }
}
