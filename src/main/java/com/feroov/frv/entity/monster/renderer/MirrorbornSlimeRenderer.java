package com.feroov.frv.entity.monster.renderer;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.monster.MirrorbornSlime;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;

public class MirrorbornSlimeRenderer extends MobRenderer<MirrorbornSlime, SlimeModel<MirrorbornSlime>>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(STLCON.MOD_ID, "textures/entity/mirrorborn_slime.png");

    public MirrorbornSlimeRenderer(EntityRendererProvider.Context context)
    {
        super(context, new SlimeModel<>(context.bakeLayer(ModelLayers.SLIME)), 0.3F);
        this.addLayer(new MirrorbornOuterLayer(this, new SlimeModel<>(context.bakeLayer(ModelLayers.SLIME_OUTER))));
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull MirrorbornSlime mirrorbornSlime)
    {
        return TEXTURE;
    }

    @Override
    protected void scale(MirrorbornSlime swet, @Nonnull PoseStack poseStack, float partialTickTime)
    {
        float scale = 1.5F;
        if (!swet.getPassengers().isEmpty())
        {
            scale += (swet.getPassengers().get(0).getBbWidth() + swet.getPassengers().get(0).getBbHeight()) * 0.75F;
        }

        float height = Mth.lerp(partialTickTime, swet.oMirrorbornHeight, swet.mirrorbornHeight);
        float width = Mth.lerp(partialTickTime, swet.oMirrorbornWidth, swet.mirrorbornWidth);

        poseStack.scale(width * scale, height * scale, width * scale);
        poseStack.scale(swet.getScale(), swet.getScale(), swet.getScale());
        this.shadowRadius = 0.3F * width;
    }
}