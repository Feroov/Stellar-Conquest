package com.feroov.frv.entity.monster.renderer;

import com.feroov.frv.entity.monster.MirrorbornSlime;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;

public class MirrorbornOuterLayer extends RenderLayer<MirrorbornSlime, SlimeModel<MirrorbornSlime>>
{
    private final SlimeModel<MirrorbornSlime> outer;

    public MirrorbornOuterLayer(RenderLayerParent<MirrorbornSlime, SlimeModel<MirrorbornSlime>> entityRenderer, SlimeModel<MirrorbornSlime> outerModel)
    {
        super(entityRenderer);
        this.outer = outerModel;
    }

    public void render(@Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int packedLight, @Nonnull
    MirrorbornSlime mirrorbornSlime, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = minecraft.shouldEntityAppearGlowing(mirrorbornSlime) && mirrorbornSlime.isInvisible();
        if (!mirrorbornSlime.isInvisible() || flag) {
            VertexConsumer consumer;
            if (flag) {
                consumer = buffer.getBuffer(RenderType.outline(this.getTextureLocation(mirrorbornSlime)));
            } else {
                consumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(mirrorbornSlime)));
            }
            this.getParentModel().copyPropertiesTo(this.outer);
            this.outer.prepareMobModel(mirrorbornSlime, limbSwing, limbSwingAmount, partialTicks);
            this.outer.setupAnim(mirrorbornSlime, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.outer.renderToBuffer(poseStack, consumer, packedLight, LivingEntityRenderer
                    .getOverlayCoords(mirrorbornSlime, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
