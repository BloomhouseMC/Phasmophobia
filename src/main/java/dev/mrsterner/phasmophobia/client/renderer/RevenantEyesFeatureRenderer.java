package dev.mrsterner.phasmophobia.client.renderer;

import dev.mrsterner.phasmophobia.Phasmophobia;
import dev.mrsterner.phasmophobia.common.entity.RevenantEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

@Environment(EnvType.CLIENT)
public class RevenantEyesFeatureRenderer extends GeoLayerRenderer<RevenantEntity> {
    private static final RenderLayer SKIN_1 = RenderLayer.getEyes(new Identifier(Phasmophobia.MODID,"textures/entity/revenant_eyes_1.png"));
    private static final RenderLayer SKIN_2 = RenderLayer.getEyes(new Identifier(Phasmophobia.MODID,"textures/entity/revenant_eyes_2.png"));
    private final IGeoRenderer<RevenantEntity> renderer;

    public RevenantEyesFeatureRenderer(RevenantEntityRenderer entityRendererIn, RevenantEntityRenderer.RevenantEyesRenderer revenantEyesRenderer) {
        super(entityRendererIn);
        this.renderer = revenantEyesRenderer;
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, RevenantEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        renderer.render(getEntityModel().getModel(new Identifier(Phasmophobia.MODID, "geo/revenant.geo.json")), entitylivingbaseIn, partialTicks, SKIN_1, matrixStackIn, bufferIn, bufferIn.getBuffer(SKIN_1), packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        renderer.render(getEntityModel().getModel(new Identifier(Phasmophobia.MODID, "geo/revenant.geo.json")), entitylivingbaseIn, partialTicks, SKIN_2, matrixStackIn, bufferIn, bufferIn.getBuffer(SKIN_2), packedLightIn, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);

    }
}
