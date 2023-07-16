package com.feroov.frv.entity.monster.renderer;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.monster.Mekkron;
import com.feroov.frv.entity.monster.model.MekkronModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class MekkronRenderer extends GeoEntityRenderer<Mekkron>
{
    public MekkronRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new MekkronModel());
        this.shadowRadius = 1.28F;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull Mekkron animatable)
    {
        return new ResourceLocation(STLCON.MOD_ID, "textures/entity/mekkron.png");
    }

    @Override
    public void render(@NotNull Mekkron entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight)
    {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
