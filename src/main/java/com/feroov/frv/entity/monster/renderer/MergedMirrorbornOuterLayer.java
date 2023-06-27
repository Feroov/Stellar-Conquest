package com.feroov.frv.entity.monster.renderer;

import com.feroov.frv.entity.monster.MergedMirrorborn;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import javax.annotation.Nonnull;

public class MergedMirrorbornOuterLayer extends RenderLayer<MergedMirrorborn, SlimeModel<MergedMirrorborn>>
{
    private final SlimeModel<MergedMirrorborn> outer;

    public MergedMirrorbornOuterLayer(RenderLayerParent<MergedMirrorborn, SlimeModel<MergedMirrorborn>> entityRenderer, SlimeModel<MergedMirrorborn> outerModel)
    {
        super(entityRenderer);
        this.outer = outerModel;
    }

    public void render(@Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int packedLight, @Nonnull
    MergedMirrorborn mirrorbornSlime, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
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
