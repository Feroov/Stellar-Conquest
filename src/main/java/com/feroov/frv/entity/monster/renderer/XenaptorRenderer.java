package com.feroov.frv.entity.monster.renderer;

import com.feroov.frv.entity.monster.Xenaptor;
import com.feroov.frv.entity.monster.model.XenaptorModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class XenaptorRenderer extends GeoEntityRenderer<Xenaptor>
{
    public XenaptorRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new XenaptorModel());
        this.shadowRadius = 0.78F;
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void render(@NotNull Xenaptor entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight)
    {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
