package com.feroov.frv.world.dimension.sky;

import com.feroov.frv.STLCON;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import javax.annotation.Nullable;


public class XenosphereSky extends DimensionSpecialEffects
{
    public static final ResourceLocation EARTH = new ResourceLocation(STLCON.MOD_ID, "textures/environment/celestial/earth.png");
    public static final ResourceLocation SUN_LOCATION = new ResourceLocation(STLCON.MOD_ID, "textures/environment/celestial/sun.png");
    public static final ResourceLocation GALAXY1 = new ResourceLocation(STLCON.MOD_ID, "textures/environment/celestial/galaxy1.png");
    public static final ResourceLocation PLANET1 = new ResourceLocation(STLCON.MOD_ID, "textures/environment/celestial/planet1.png");
    public static final ResourceLocation PLANET2 = new ResourceLocation(STLCON.MOD_ID, "textures/environment/celestial/planet2.png");

    @Nullable
    private VertexBuffer skyBuffer;

    public XenosphereSky() {
        super(Float.NaN, true, SkyType.NORMAL, false, false);

        //create sky
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        if (skyBuffer != null) skyBuffer.close();
        skyBuffer = new VertexBuffer();
        BufferBuilder.RenderedBuffer bufferbuilder$renderedbuffer = StarrySky.drawStars(bufferbuilder);
        skyBuffer.bind();
        skyBuffer.upload(bufferbuilder$renderedbuffer);
        VertexBuffer.unbind();
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 vec, float f) {
        return vec.scale(0.15);
    }

    @Override
    public boolean isFoggyAt(int i, int ii) {
        return false;
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        setupFog.run();
        Vec3 vec3 = level.getSkyColor(camera.getPosition(), partialTick);
        float f = (float) vec3.x, f1 = (float) vec3.y, f2 = (float) vec3.z;
        FogRenderer.levelFogColor();
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(f, f1, f2, 1F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        poseStack.pushPose();
        float f11 = 1F - level.getRainLevel(partialTick);
        RenderSystem.setShaderColor(1F, 1F, 1F, f11);
        poseStack.mulPose(Axis.YP.rotationDegrees(-90F));
        poseStack.mulPose(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTick) * 360F));
        Matrix4f matrix4f1 = poseStack.last().pose();
        float f12 = 5F;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, EARTH);
        int k = level.getMoonPhase(), l = k % 4, i1 = k / 4 % 2;
        float f13 = (l + 0) / 4F, f14 = (i1 + 0) / 2F, f15 = (l + 1) / 4F, f16 = (i1 + 1) / 2F;
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f1, -f12, -100F, f12).uv(f15, f16).endVertex();
        bufferbuilder.vertex(matrix4f1, f12, -100F, f12).uv(f13, f16).endVertex();
        bufferbuilder.vertex(matrix4f1, f12, -100F, -f12).uv(f13, f14).endVertex();
        bufferbuilder.vertex(matrix4f1, -f12, -100F, -f12).uv(f15, f14).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
        float f18 = 10F;
        RenderSystem.setShaderTexture(0, SUN_LOCATION);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f1, -f18, 100.0F, -f18).uv(0.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, f18, 100.0F, -f18).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, f18, 100.0F, f18).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, -f18, 100.0F, f18).uv(0.0F, 1.0F).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());

        float f17 = 150.0F;
        float x = 350.0F;
        float y = 350.0F;
        float z = 350.0F;

        RenderSystem.setShaderTexture(0, GALAXY1);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f1, x - f17, y, z - f17).uv(0.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, x + f17, y, z - f17).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, x + f17, y, z + f17).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, x - f17, y, z + f17).uv(0.0F, 1.0F).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());


        float f19 = 50.0F;
        float x2 = -500.0F;
        float y2 = 525.0F;

        RenderSystem.setShaderTexture(0, PLANET1);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f1, x2 - f19, y2, -100 - f19).uv(0.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, x2 + f19, y2, -100 - f19).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, x2 + f19, y2, -130 + f19).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix4f1, x2 - f19, y2, -130 + f19).uv(0.0F, 1.0F).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());

        float f20 = 5.0F;

        RenderSystem.setShaderTexture(0, PLANET2);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f1, 80-f20, -50F, f20).uv(f15, f16).endVertex();
        bufferbuilder.vertex(matrix4f1, 100+ f20, -50F, f20).uv(f13, f16).endVertex();
        bufferbuilder.vertex(matrix4f1, 108- f20, -50F, -f20).uv(f13, f14).endVertex();
        bufferbuilder.vertex(matrix4f1, 80-f20, -50F, -f20).uv(f15, f14).endVertex(); BufferUploader.drawWithShader(bufferbuilder.end());



        float f10 = 1.0F;
        RenderSystem.setShaderColor(f10, f10, f10, f10);
        FogRenderer.setupNoFog();
        skyBuffer.bind();
        skyBuffer.drawWithShader(poseStack.last().pose(), projectionMatrix, GameRenderer.getPositionShader());
        VertexBuffer.unbind();
        setupFog.run();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        poseStack.popPose();
        RenderSystem.depthMask(true);
        return true;
    }
}