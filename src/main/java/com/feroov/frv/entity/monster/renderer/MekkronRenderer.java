package com.feroov.frv.entity.monster.renderer;

import com.feroov.frv.STLCON;
import com.feroov.frv.entity.monster.Mekkron;
import com.feroov.frv.entity.monster.model.MekkronModel;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
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

    @Override
    public boolean shouldShowName(Mekkron entity)
    {
        return true;
    }

    private boolean shouldRender(Mekkron entity)
    {
        // Get the player's camera position
        double playerX = this.entityRenderDispatcher.camera.getPosition().x();
        double playerY = this.entityRenderDispatcher.camera.getPosition().y();
        double playerZ = this.entityRenderDispatcher.camera.getPosition().z();

        // Get the entity's position
        double entityX = entity.getX();
        double entityY = entity.getY();
        double entityZ = entity.getZ();

        // Calculate the distance between the player's camera and the entity
        double distance = Math.sqrt((playerX - entityX) * (playerX - entityX) + (playerY - entityY) * (playerY - entityY) + (playerZ - entityZ) * (playerZ - entityZ));
        return distance < 8.0D;
    }

    @Override
    protected void renderNameTag(Mekkron entity, Component displayName, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight)
    {
        double distanceSquared = this.entityRenderDispatcher.distanceToSqr(entity);
        if (net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance(entity, distanceSquared) && shouldRender(entity))
        {
            boolean shouldRenderHealthText = !entity.isDiscrete();
            float yOffset = entity.getNameTagOffsetY() + 0.5F;

            poseStack.pushPose();
            poseStack.translate(0.0F, yOffset, 0.0F);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = poseStack.last().pose();
            float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            int backgroundOpacityColor = (int) (backgroundOpacity * 255.0F) << 24;
            Font fontRenderer = this.getFont();
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();

            String healthText = String.format("%d/%d", (int) entity.getHealth(), (int) entity.getMaxHealth());
            String entityName = "Mekkron";

            int entityNameColor = 0xFFFF0000;
            int healthTextColor = 0xFFFFFFFF; // White color

            int healthTextWidth = fontRenderer.width(healthText);
            int entityNameWidth = fontRenderer.width(entityName);

            int maxWidth = Math.max(healthTextWidth, entityNameWidth);

            float textXOffset = (float) (-maxWidth / 2);

            float healthTextXOffset = textXOffset + (maxWidth - healthTextWidth) / 2;
            float entityNameXOffset = textXOffset + (maxWidth - entityNameWidth) / 2;

            fontRenderer.drawInBatch(entityName, entityNameXOffset, -10, entityNameColor, false, matrix4f, bufferSource, Font.DisplayMode.SEE_THROUGH, backgroundOpacityColor, packedLight);
            fontRenderer.drawInBatch(healthText, healthTextXOffset, 0, healthTextColor, false, matrix4f, bufferSource, shouldRenderHealthText ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, backgroundOpacityColor, packedLight);

            if (shouldRenderHealthText)
            {
                fontRenderer.drawInBatch(healthText, healthTextXOffset, 0, -1, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);
            }

            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();

            poseStack.popPose();
        }
    }
}
