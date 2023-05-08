package com.feroov.frv.entity.monster.renderer;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.monster.Mothership;
import com.feroov.frv.entity.monster.model.MothershipModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MothershipRenderer extends GeoEntityRenderer<Mothership>
{
    public MothershipRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new MothershipModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Mothership animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/mothership.png");
    }

    @Override
    public void render(@NotNull Mothership entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight)
    {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    protected int getBlockLightLevel(@NotNull Mothership entity, @NotNull BlockPos blockPos) {
        return 15;
    }
}
