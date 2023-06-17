package com.feroov.frv.entity.monster.renderer;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.monster.Celestroid;
import com.feroov.frv.entity.monster.model.CelestroidModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CelestroidRenderer extends GeoEntityRenderer<Celestroid>
{
    public CelestroidRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new CelestroidModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Celestroid animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/celestroid.png");
    }

    @Override
    public void render(@NotNull Celestroid entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight)
    {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected int getBlockLightLevel(@NotNull Celestroid entity, @NotNull BlockPos blockPos) { return 15; }
}
