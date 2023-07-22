package com.feroov.frv.entity.monster.renderer;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.monster.Xenaptor;
import com.feroov.frv.entity.monster.model.XenaptorModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class XenaptorRenderer extends GeoEntityRenderer<Xenaptor>
{
    public XenaptorRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new XenaptorModel());
        this.shadowRadius = 0.78F;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Xenaptor animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/xenaptor.png");
    }

    @Override
    public void render(@NotNull Xenaptor entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight)
    {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
